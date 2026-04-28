package life.sim.simulator

import kotlin.test.Test
import kotlin.test.assertEquals

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
}

