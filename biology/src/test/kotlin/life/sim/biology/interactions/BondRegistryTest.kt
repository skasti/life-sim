package life.sim.biology.interactions

import life.sim.biology.molecules.MRna

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BondRegistryTest {
    @Test
    fun `registry supports site to site, site to whole, and whole to whole bonds`() {
        val firstSurface = MRna.of("AUGCUA").bindingSurface(MoleculeId(11))
        val secondSurface = MRna.of("CGAAUU").bindingSurface(MoleculeId(12))

        val siteToSite = Bond(
            left = SiteEndpoint(firstSurface.site(1, 4)),
            right = SiteEndpoint(secondSurface.site(0, 3)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val siteToWhole = Bond(
            left = SiteEndpoint(firstSurface.site(4, 6)),
            right = WholeMoleculeEndpoint(MoleculeId(13)),
            strength = 0.9,
            decayPerTick = 0.2,
        )
        val wholeToWhole = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(13)),
            right = WholeMoleculeEndpoint(MoleculeId(14)),
            strength = 0.7,
            decayPerTick = 0.05,
        )

        val registry = BondRegistry(listOf(siteToSite, siteToWhole, wholeToWhole))

        assertEquals(listOf(siteToSite, siteToWhole), registry.bondsFor(MoleculeId(11)))
        assertEquals(listOf(siteToWhole, wholeToWhole), registry.bondsFor(MoleculeId(13)))
        assertEquals(listOf(wholeToWhole), registry.bondsFor(MoleculeId(14)))
    }

    @Test
    fun `registry overlap queries only consider site-aware endpoints`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(17))
        val siteToWhole = Bond(
            left = SiteEndpoint(surface.site(1, 4)),
            right = WholeMoleculeEndpoint(MoleculeId(21)),
            strength = 0.9,
            decayPerTick = 0.2,
        )
        val wholeToWhole = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(21)),
            right = WholeMoleculeEndpoint(MoleculeId(22)),
            strength = 0.9,
            decayPerTick = 0.2,
        )
        val registry = BondRegistry(listOf(siteToWhole, wholeToWhole))

        assertEquals(listOf(siteToWhole), registry.overlapping(surface.site(2, 3)))
        assertTrue(registry.overlapping(surface.site(5, 5)).isEmpty())
        assertTrue(registry.overlapping(MRna.of("CC").bindingSurface(MoleculeId(22)).site(0, 2)).isEmpty())
    }

    @Test
    fun `registry can query bonds by exact site and by surface`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(15))
        val exactSite = surface.site(1, 4)
        val otherSite = surface.site(4, 6)
        val rightOnlySite = surface.site(0, 2)
        val first = Bond(
            left = SiteEndpoint(exactSite),
            right = WholeMoleculeEndpoint(MoleculeId(99)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val second = Bond(
            left = SiteEndpoint(otherSite),
            right = WholeMoleculeEndpoint(MoleculeId(100)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val siteOnRight = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(102)),
            right = SiteEndpoint(rightOnlySite),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val wholeOnly = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(15)),
            right = WholeMoleculeEndpoint(MoleculeId(101)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val registry = BondRegistry(listOf(first, second, siteOnRight, wholeOnly))

        assertEquals(listOf(first), registry.bondsInvolving(exactSite))
        assertEquals(listOf(siteOnRight), registry.bondsInvolving(rightOnlySite))
        assertEquals(listOf(first, second, siteOnRight), registry.bondsOnSurface(surface.site(0, 1)))
        assertEquals(listOf(siteOnRight), registry.overlapping(surface.site(0, 1)))
    }

    @Test
    fun `registry ignores inactive bonds supplied at construction`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(15))
        val active = Bond(
            left = SiteEndpoint(surface.site(1, 4)),
            right = WholeMoleculeEndpoint(MoleculeId(31)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val inactive = Bond(
            left = SiteEndpoint(surface.site(4, 6)),
            right = WholeMoleculeEndpoint(MoleculeId(32)),
            strength = 0.0,
            decayPerTick = 0.2,
        )

        val registry = BondRegistry(listOf(active, inactive))

        assertEquals(listOf(active), registry.toList())
        assertEquals(listOf(active), registry.bondsFor(MoleculeId(15)))
        assertTrue(registry.overlapping(surface.site(4, 6)).isEmpty())
        assertEquals(listOf(active), registry.bondsOnSurface(surface.site(0, 1)))
    }

    @Test
    fun `registry add ignores inactive bonds`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(16))
        val inactive = Bond(
            left = SiteEndpoint(surface.site(1, 4)),
            right = WholeMoleculeEndpoint(MoleculeId(90)),
            strength = 0.0,
            decayPerTick = 0.1,
        )
        val registry = BondRegistry()

        val returned = registry.add(inactive)

        assertEquals(inactive, returned)
        assertTrue(registry.isEmpty())
        assertTrue(registry.bondsFor(MoleculeId(16)).isEmpty())
        assertTrue(registry.bondsOnSurface(surface.site(0, 1)).isEmpty())
        assertTrue(registry.overlapping(surface.site(1, 4)).isEmpty())
    }

    @Test
    fun `registry stores only one active entry for mirrored bonds`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(18))
        val bond = Bond(
            left = SiteEndpoint(surface.site(1, 4)),
            right = WholeMoleculeEndpoint(MoleculeId(23)),
            strength = 0.9,
            decayPerTick = 0.1,
        )
        val mirrored = Bond(
            left = bond.right,
            right = bond.left,
            strength = bond.strength,
            decayPerTick = bond.decayPerTick,
        )
        val registry = BondRegistry()

        registry.add(bond)
        registry.add(mirrored)

        assertEquals(1, registry.size)
        assertEquals(listOf(bond), registry.toList())
    }

    @Test
    fun `registry iterator is a snapshot and is unaffected by later mutations`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(24))
        val first = Bond(
            left = SiteEndpoint(surface.site(0, 2)),
            right = WholeMoleculeEndpoint(MoleculeId(25)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val second = Bond(
            left = SiteEndpoint(surface.site(2, 4)),
            right = WholeMoleculeEndpoint(MoleculeId(26)),
            strength = 0.7,
            decayPerTick = 0.1,
        )
        val registry = BondRegistry(listOf(first))

        val iterator = registry.iterator()
        registry.add(second)

        assertEquals(listOf(first), iterator.asSequence().toList())
        assertEquals(listOf(first, second), registry.toList())
    }

    @Test
    fun `registry decay removes inactive bonds and keeps surviving strength`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(12))
        val transient = Bond(
            left = SiteEndpoint(surface.site(0, 2)),
            right = WholeMoleculeEndpoint(MoleculeId(41)),
            strength = 0.1,
            decayPerTick = 0.1,
        )
        val stable = Bond(
            left = SiteEndpoint(surface.site(2, 5)),
            right = WholeMoleculeEndpoint(MoleculeId(42)),
            strength = 0.9,
            decayPerTick = 0.2,
        )
        val registry = BondRegistry(listOf(transient, stable))

        val remaining = registry.decayAll(2)

        assertEquals(1, remaining.size)
        assertEquals(0.5, remaining.single().strength, absoluteTolerance = 1.0e-9)
        assertEquals(1, registry.size)
    }

    @Test
    fun `bond decay rejects negative tick counts`() {
        val bond = Bond(
            left = SiteEndpoint(MRna.of("AUG").bindingSurface(MoleculeId(13)).site(0, 2)),
            right = WholeMoleculeEndpoint(MoleculeId(50)),
            strength = 1.0,
            decayPerTick = 0.1,
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            bond.decay(-1)
        }

        assertEquals("Bond decay ticks must be greater than or equal to zero, but was -1.", exception.message)
    }

    @Test
    fun `bond reports whether it is still active`() {
        val bond = Bond(
            left = SiteEndpoint(MRna.of("AUG").bindingSurface(MoleculeId(14)).site(0, 2)),
            right = WholeMoleculeEndpoint(MoleculeId(51)),
            strength = 0.0,
            decayPerTick = 0.1,
        )

        assertFalse(bond.isActive())
        assertTrue(bond.copy(strength = 0.01).isActive())
    }
}
