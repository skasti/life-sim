package life.sim.simulator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopLauncherTest {
    @Test
    fun `DesktopShellConfig default values define the simulator application shell`() {
        val defaults = DesktopShellConfig()

        assertEquals("Life-Sim Simulator", defaults.title)
        assertEquals(1280, defaults.width)
        assertEquals(720, defaults.height)
        assertEquals(60, defaults.foregroundFps)
        assertTrue(defaults.vsyncEnabled)
    }
}
