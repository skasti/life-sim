package life.sim.simulator

import life.sim.biology.primitives.Nucleotide
import kotlin.test.Test
import kotlin.test.assertEquals

class DemoSceneTest {
    @Test
    fun `sample scene includes nucleotide sequence and dna objects for rendering baseline`() {
        val scene = DemoScene.sample()

        assertEquals(Nucleotide.G, scene.nucleotide)
        assertEquals(">AUGCGAUCGUAA>", scene.sequence.toString())
        assertEquals(">ACGUACGUAC>", scene.dna.forward.toString())
        assertEquals("<UGCAUGCAUG<", scene.dna.reverse.toString())
    }
}
