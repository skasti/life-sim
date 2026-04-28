package life.sim.simulator

import life.sim.biology.primitives.Nucleotide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DemoSceneTest {
    @Test
    fun `sample returns a scene with deterministic nucleotide sequence and dna fixtures`() {
        SimulatorApplication.initializeRenderers()
        val scene = DemoScene.sample()

        assertIs<Scene>(scene)
        assertEquals(Nucleotide.G, scene.nucleotide)
        assertEquals(">AUGCGAUCGUAA>", scene.sequenceText)
        assertEquals(">ACGUACGUAC>", scene.dnaForwardText)
        assertEquals("<UGCAUGCAUG<", scene.dnaReverseText)
    }
}
