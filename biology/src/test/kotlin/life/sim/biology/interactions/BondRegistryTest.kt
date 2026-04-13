package life.sim.biology.interactions

import life.sim.biology.molecules.MRna

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BondRegistryTest {
    private data class TestAgent(
        val label: String,
    ) : BoundAgent

    @Test
    fun `registry stores bonds and queries them by molecule and overlap`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(11))
        val first = Bond(surface.site(1, 4), TestAgent("repressor"), strength = 0.8, decayPerTick = 0.1)
        val second = Bond(surface.site(4, 6), TestAgent("polymerase"), strength = 0.9, decayPerTick = 0.2)
        val registry = BondRegistry(listOf(first, second))

        assertEquals(listOf(first, second), registry.bondsFor(MoleculeId(11)))
        assertEquals(listOf(first), registry.overlapping(surface.site(2, 4)))
        assertEquals(listOf(first, second), registry.bondsOnSurface(surface.site(0, 1)))
    }

    @Test
    fun `registry ignores inactive bonds supplied at construction`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(15))
        val active = Bond(surface.site(1, 4), TestAgent("active"), strength = 0.8, decayPerTick = 0.1)
        val inactive = Bond(surface.site(4, 6), TestAgent("inactive"), strength = 0.0, decayPerTick = 0.2)

        val registry = BondRegistry(listOf(active, inactive))

        assertEquals(listOf(active), registry.toList())
        assertEquals(listOf(active), registry.bondsFor(MoleculeId(15)))
        assertTrue(registry.overlapping(surface.site(4, 6)).isEmpty())
        assertEquals(listOf(active), registry.bondsOnSurface(surface.site(0, 1)))
    }

    @Test
    fun `registry add ignores inactive bonds`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(16))
        val inactive = Bond(surface.site(1, 4), TestAgent("inactive"), strength = 0.0, decayPerTick = 0.1)
        val registry = BondRegistry()

        val returned = registry.add(inactive)

        assertEquals(inactive, returned)
        assertTrue(registry.isEmpty())
        assertTrue(registry.bondsFor(MoleculeId(16)).isEmpty())
        assertTrue(registry.bondsOnSurface(surface.site(0, 1)).isEmpty())
        assertTrue(registry.overlapping(surface.site(1, 4)).isEmpty())
    }

    @Test
    fun `registry decay removes inactive bonds and keeps surviving strength`() {
        val surface = MRna.of("AUGCUA").bindingSurface(MoleculeId(12))
        val transient = Bond(surface.site(0, 2), TestAgent("weak"), strength = 0.1, decayPerTick = 0.1)
        val stable = Bond(surface.site(2, 5), TestAgent("stable"), strength = 0.9, decayPerTick = 0.2)
        val registry = BondRegistry(listOf(transient, stable))

        val remaining = registry.decayAll(2)

        assertEquals(1, remaining.size)
        assertEquals(0.5, remaining.single().strength, absoluteTolerance = 1.0e-9)
        assertEquals(1, registry.size)
    }

    @Test
    fun `bond decay rejects negative tick counts`() {
        val bond = Bond(MRna.of("AUG").bindingSurface(MoleculeId(13)).site(0, 2), TestAgent("repressor"), 1.0, 0.1)

        val exception = assertFailsWith<IllegalArgumentException> {
            bond.decay(-1)
        }

        assertEquals("Bond decay ticks must be greater than or equal to zero, but was -1.", exception.message)
    }

    @Test
    fun `bond reports whether it is still active`() {
        val bond = Bond(MRna.of("AUG").bindingSurface(MoleculeId(14)).site(0, 2), TestAgent("repressor"), 0.0, 0.1)

        assertFalse(bond.isActive())
        assertTrue(bond.copy(strength = 0.01).isActive())
    }
}

