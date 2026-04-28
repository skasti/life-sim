package life.sim.simulator

import life.sim.simulator.rendering.RenderContext

/**
 * Scene/state contract for simulator runtime behavior.
 */
interface Scene {
    val objectManager: ObjectManager

    fun update(deltaSeconds: Float) {
        objectManager.update(deltaSeconds)
    }

    fun render(
        context: RenderContext,
    ) {
        objectManager.render(context)
    }
}
