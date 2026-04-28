package life.sim.simulator

import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.Renderers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class DemoSceneTest {
    @Test
    fun `sample returns a scene with deterministic nucleotide sequence and dna fixtures`() {
        val scene = DemoScene.sample()

        assertIs<Scene>(scene)
        assertEquals(Nucleotide.G, scene.nucleotide)
        assertEquals(">AUGCGAUCGUAA>", scene.sequenceText)
        assertEquals(">ACGUACGUAC>", scene.dnaForwardText)
        assertEquals("<UGCAUGCAUG<", scene.dnaReverseText)
    }

    @Test
    fun `sample initializes type specific renderers for nucleotide sequence and dna`() {
        DemoScene.sample()

        assertNotNull(Renderers.forType<Nucleotide>())
        assertNotNull(Renderers.forType<NucleotideSequence>())
        assertNotNull(Renderers.forType<Dna>())
    }
}
