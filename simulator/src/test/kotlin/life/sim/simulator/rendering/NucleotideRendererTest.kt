package life.sim.simulator.rendering

import kotlin.test.Test
import kotlin.test.assertEquals
import life.sim.biology.primitives.Nucleotide

class NucleotideRendererTest {
    private val renderer = NucleotideRenderer()

    @Test
    fun `compatibilityProfile maps A and U to the same connector style`() {
        assertEquals(
            renderer.compatibilityProfile(Nucleotide.A),
            renderer.compatibilityProfile(Nucleotide.U),
        )
    }

    @Test
    fun `compatibilityProfile maps C and G to the same connector style`() {
        assertEquals(
            renderer.compatibilityProfile(Nucleotide.C),
            renderer.compatibilityProfile(Nucleotide.G),
        )
    }
}
