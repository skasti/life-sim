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

        val baseAffinity = (base.single().capabilities.single() as SequenceBinder).affinity
        val mutatedAffinity = (mutated.single().capabilities.single() as SequenceBinder).affinity

        assertTrue(baseAffinity != mutatedAffinity)
    }

    @Test
    fun `interpreter returns empty when no motifs match`() {
        val domains = ProteinInterpreter.interpret(Polypeptide.of("AAAAAAA"))

        assertTrue(domains.isEmpty())
    }
}
