package life.sim.simulator

import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.Renderers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SimulatorApplicationTest {
    @Test
    fun `formatFpsCounterText returns the expected debug label`() {
        assertEquals("FPS: 60", SimulatorApplication.formatFpsCounterText(60))
    }

    @Test
    fun `fpsCounterBaselineY uses padding when there is enough vertical space`() {
        assertEquals(
            34f,
            SimulatorApplication.fpsCounterBaselineY(viewportHeight = 120f, lineHeight = 22f),
        )
    }

    @Test
    fun `fpsCounterBaselineY avoids pushing the baseline below a readable line height in short windows`() {
        assertEquals(
            22f,
            SimulatorApplication.fpsCounterBaselineY(viewportHeight = 28f, lineHeight = 22f),
        )
    }

    @Test
    fun `initializeRenderers initializes type specific renderers for nucleotide sequence and dna`() {
        SimulatorApplication.initializeRenderers()

        assertNotNull(Renderers.forType<Nucleotide>())
        assertNotNull(Renderers.forType<NucleotideSequence>())
        assertNotNull(Renderers.forType<Dna>())
    }
}

