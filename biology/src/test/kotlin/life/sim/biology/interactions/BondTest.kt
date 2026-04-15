package life.sim.biology.interactions

import life.sim.biology.molecules.MRna

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BondTest {
    @Test
    fun `swapped site to site endpoints compare equal and share hash code`() {
        val firstSurface = MRna.of("AUGCUA").bindingSurface(MoleculeId(1))
        val secondSurface = MRna.of("CGAAUU").bindingSurface(MoleculeId(2))

        val first = Bond(
            left = SiteEndpoint(firstSurface.site(1, 4)),
            right = SiteEndpoint(secondSurface.site(0, 3)),
            strength = 0.8,
            decayPerTick = 0.1,
        )
        val mirrored = Bond(
            left = first.right,
            right = first.left,
            strength = first.strength,
            decayPerTick = first.decayPerTick,
        )

        assertEquals(first, mirrored)
        assertEquals(first.hashCode(), mirrored.hashCode())
    }

    @Test
    fun `swapped site to whole endpoints compare equal and share hash code`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(3))
        val siteToWhole = Bond(
            left = SiteEndpoint(surface.site(2, 5)),
            right = WholeMoleculeEndpoint(MoleculeId(4)),
            strength = 0.7,
            decayPerTick = 0.05,
        )
        val wholeToSite = Bond(
            left = siteToWhole.right,
            right = siteToWhole.left,
            strength = siteToWhole.strength,
            decayPerTick = siteToWhole.decayPerTick,
        )

        assertEquals(siteToWhole, wholeToSite)
        assertEquals(siteToWhole.hashCode(), wholeToSite.hashCode())
    }

    @Test
    fun `swapped whole to whole endpoints compare equal and share hash code`() {
        val first = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(9)),
            right = WholeMoleculeEndpoint(MoleculeId(10)),
            strength = 0.6,
            decayPerTick = 0.03,
        )
        val mirrored = Bond(
            left = first.right,
            right = first.left,
            strength = first.strength,
            decayPerTick = first.decayPerTick,
        )

        assertEquals(first, mirrored)
        assertEquals(first.hashCode(), mirrored.hashCode())
    }

    @Test
    fun `different strength still makes mirrored bonds unequal`() {
        val first = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(11)),
            right = WholeMoleculeEndpoint(MoleculeId(12)),
            strength = 0.6,
            decayPerTick = 0.03,
        )
        val differentStrength = Bond(
            left = first.right,
            right = first.left,
            strength = 0.5,
            decayPerTick = first.decayPerTick,
        )

        assertNotEquals(first, differentStrength)
    }

    @Test
    fun `different decay still makes mirrored bonds unequal`() {
        val first = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(13)),
            right = WholeMoleculeEndpoint(MoleculeId(14)),
            strength = 0.6,
            decayPerTick = 0.03,
        )
        val differentDecay = Bond(
            left = first.right,
            right = first.left,
            strength = first.strength,
            decayPerTick = 0.04,
        )

        assertNotEquals(first, differentDecay)
    }

    @Test
    fun `signed zero values are compared consistently with hash code`() {
        val positiveZeroStrength = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(15)),
            right = WholeMoleculeEndpoint(MoleculeId(16)),
            strength = 0.0,
            decayPerTick = 0.1,
        )
        val negativeZeroStrength = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(16)),
            right = WholeMoleculeEndpoint(MoleculeId(15)),
            strength = -0.0,
            decayPerTick = 0.1,
        )

        assertNotEquals(positiveZeroStrength, negativeZeroStrength)
        assertNotEquals(positiveZeroStrength.hashCode(), negativeZeroStrength.hashCode())
    }
}
