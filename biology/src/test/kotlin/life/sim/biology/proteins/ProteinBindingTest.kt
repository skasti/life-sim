package life.sim.biology.proteins

import life.sim.biology.interactions.BondRegistry
import life.sim.biology.interactions.MoleculeId
import life.sim.biology.interactions.bindingSurface
import life.sim.biology.molecules.MRna
import life.sim.biology.molecules.Polypeptide
import life.sim.biology.primitives.NucleotideSequence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProteinBindingTest {
    @Test
    fun `interpreted binder can create a runtime bond and register it`() {
        val binder = interpretedBinderFrom("AAKRGKAA")
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(20))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(99),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNotNull(bond)
        assertEquals(1, registry.size)
        assertEquals(bond, registry.toList().single())
        assertTrue(bond.strength in 0.0..1.0)
        assertEquals(0.05, bond.decayPerTick, absoluteTolerance = 1.0e-9)
        assertEquals(MoleculeId(99), bond.left.moleculeId)
        assertEquals(MoleculeId(20), bond.right.moleculeId)
    }

    @Test
    fun `tryBind returns null and leaves registry unchanged when no complementary site exists`() {
        val binder = interpretedBinderFrom("AAKRGKAA")
        val target = MRna.of("AAAAAAA").bindingSurface(MoleculeId(30))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(101),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertTrue(registry.isEmpty())
    }


    @Test
    fun `tryBind returns null when affinity normalizes to inactive bond strength`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.0)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(32))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(203),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertTrue(registry.isEmpty())
    }

    @Test
    fun `different amino acid context yields different binder target and different match site`() {
        val firstBinder = interpretedBinderFrom("AAKRGKAA")
        val secondBinder = interpretedBinderFrom("AAKRGKDA")
        val target = MRna.of(buildTargetWithOnlySecondMatch(secondBinder.bindingPattern)).bindingSurface(MoleculeId(31))
        val registry = BondRegistry()

        val firstBond = ProteinBinding.tryBind(
            proteinId = MoleculeId(201),
            binder = firstBinder,
            target = target,
            registry = registry,
        )
        val secondBond = ProteinBinding.tryBind(
            proteinId = MoleculeId(202),
            binder = secondBinder,
            target = target,
            registry = registry,
        )

        assertNull(firstBond)
        assertNotNull(secondBond)
        assertEquals(1, registry.size)
    }

    private fun interpretedBinderFrom(sequence: String): SequenceBinder {
        val domain = ProteinInterpreter.interpret(Polypeptide.of(sequence)).single()
        return domain.capabilities.single() as SequenceBinder
    }

    private fun buildTargetWithOnlySecondMatch(pattern: NucleotideSequence): String {
        return "AAAA${asText(pattern.complement())}AAAA"
    }

    private fun asText(sequence: NucleotideSequence): String =
        sequence.toList().joinToString(separator = "") { it.symbol.toString() }
}
