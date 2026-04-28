package life.sim.simulator

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

data class DesktopShellConfig(
    val title: String = "Life-Sim Simulator",
    val width: Int = 1280,
    val height: Int = 720,
    val foregroundFps: Int = 175,
    val vsyncEnabled: Boolean = true,
)

fun main() {
    Lwjgl3Application(SimulatorApplication(), defaultDesktopConfiguration())
}

fun defaultDesktopConfiguration(shellConfig: DesktopShellConfig = DesktopShellConfig()): Lwjgl3ApplicationConfiguration {
    return Lwjgl3ApplicationConfiguration().apply {
        setTitle(shellConfig.title)
        setWindowedMode(shellConfig.width, shellConfig.height)
        useVsync(shellConfig.vsyncEnabled)
        setForegroundFPS(shellConfig.foregroundFps)
    }
}
