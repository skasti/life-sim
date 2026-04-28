package life.sim.simulator

import life.sim.simulator.rendering.RenderContext

/**
 * Scene/state contract for simulator runtime behavior.
 */
interface Scene {
    val objectManager: ObjectManager

    /**
     * One-time scene setup hook for object/resource initialization that may depend on other
     * systems being constructed.
     */
    fun init() = Unit

    fun update(deltaSeconds: Float) {
        objectManager.update(deltaSeconds)
    }

    fun render(
        context: RenderContext,
    ) {
        try {
            objectManager.render(context)
        } finally {
            context.finish()
        }
    }
}
