package life.sim.simulator

import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers

/**
 * Bridges domain objects to simulator scene lifecycle by resolving a renderer once at construction.
 */
class SimWrapper(
    val position: Vector2,
    val content: Any,
) : SimObject, Renderable {
    private val renderer: Renderer<Any> = requireNotNull(Renderers.forValue(content)) {
        "No renderer registered for type ${content::class.qualifiedName}."
    }

    override fun render(context: RenderContext) {
        renderer.render(content, position, context)
    }
}