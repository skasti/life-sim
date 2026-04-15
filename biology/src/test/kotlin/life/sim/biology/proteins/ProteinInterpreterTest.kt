package life.sim.biology.proteins

import life.sim.biology.molecules.Polypeptide

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProteinInterpreterTest {
    @Test
    fun `interpreter detects multiple domains from one chain`() {
        val domains = ProteinInterpreter.interpret(Polypeptide.of("AAKRGKTTHEMHPPW"))

        assertEquals(listOf("BinderDomain", "CutterDomain", "BlockerDomain"), domains.map { it.name })
        assertEquals(listOf("KRGK", "HEMH", "PPW"), domains.map { it.motif })
    }

    @Test
    fun `small mutation near motif tunes binder affinity without removing domain`() {
        val base = ProteinInterpreter.interpret(Polypeptide.of("AAKRGKAA"))
        val mutated = ProteinInterpreter.interpret(Polypeptide.of("AAKRGKDA"))

        assertEquals(1, base.size)
        assertEquals(1, mutated.size)
        assertEquals("BinderDomain", base.single().name)
        assertEquals("BinderDomain", mutated.single().name)

        val baseBinder = base.single().capabilities.single() as SequenceBinder
        val mutatedBinder = mutated.single().capabilities.single() as SequenceBinder

        assertTrue(baseBinder.affinity != mutatedBinder.affinity)
    }

    @Test
    fun `small mutation near motif alters binder target pattern`() {
        val base = ProteinInterpreter.interpret(Polypeptide.of("AAKRGKAA"))
        val mutated = ProteinInterpreter.interpret(Polypeptide.of("AAKRGKDA"))

        val baseBinder = base.single().capabilities.single() as SequenceBinder
        val mutatedBinder = mutated.single().capabilities.single() as SequenceBinder

        assertTrue(baseBinder.bindingPattern != mutatedBinder.bindingPattern)
        assertEquals(6, baseBinder.bindingPattern.size)
        assertEquals(6, mutatedBinder.bindingPattern.size)
    }

    @Test
    fun `interpreter detects ligase motif and clamps catalytic strength`() {
        val domains = ProteinInterpreter.interpret(Polypeptide.of("AAGGHAA"))

        assertEquals(1, domains.size)
        val ligaseDomain = domains.single()
        assertEquals("LigaseDomain", ligaseDomain.name)
        assertEquals("GGH", ligaseDomain.motif)

        val ligaseCapability = ligaseDomain.capabilities.single() as Ligase
        assertTrue(ligaseCapability.catalyticStrength in 0.0..1.0)
    }

    @Test
    fun `interpreter returns empty when no motifs match`() {
        val domains = ProteinInterpreter.interpret(Polypeptide.of("AAAAAAA"))

        assertTrue(domains.isEmpty())
    }
}
