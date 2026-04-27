package life.sim.simulator

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.utils.ScreenUtils

/**
 * Minimal libGDX application shell for the simulator.
 *
 * This only clears the frame each render tick and provides a stable
 * starting point for adding world rendering later.
 */
class SimulatorApplication : ApplicationAdapter() {
    override fun render() {
        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f)
    }
}
