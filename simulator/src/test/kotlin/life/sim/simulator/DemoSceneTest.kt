package life.sim.simulator

import life.sim.biology.primitives.Nucleotide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
    fun `init populates demo scene object manager once`() {
        SimulatorApplication.initializeRenderers()
        val scene = DemoScene.sample()

        assertEquals(0, scene.objectManager.objectCount())

        scene.init()
        scene.init()

        assertEquals(3, scene.objectManager.objectCount())
    }
}
