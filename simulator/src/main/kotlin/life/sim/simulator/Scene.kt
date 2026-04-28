package life.sim.simulator

import life.sim.simulator.rendering.RenderContext

/**
 * Scene/state contract for simulator runtime behavior.
 */
interface Scene {
    fun update(deltaSeconds: Float)

    fun render(
        context: RenderContext,
    )
}
