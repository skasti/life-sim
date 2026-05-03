package life.sim.simulator

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers

/**
 * Bridges domain objects to simulator scene lifecycle by resolving a renderer once at construction.
 *
 * `position` is interpreted as the visual center for wrapped content so all renderers can rotate
 * around that same world-space point without coupling to each other's anchor math.
 */
class SimWrapper(
    val position: Vector2,
    var rotation: Float,
    val content: Any,
) : SimObject, Renderable, Updateable {
    private val renderer: Renderer<Any> = requireNotNull(Renderers.forValue(content)) {
        "No renderer registered for type ${content::class.qualifiedName}."
    }

    override fun update(deltaSeconds: Float, input : Input) {
        if (input.isKeyPressed(Input.Keys.SPACE)) {
            rotation += 90f * deltaSeconds
        } else if (input.isKeyJustPressed(Input.Keys.R)) {
            rotation = 0f
        }
    }

    override fun render(context: RenderContext) {
        renderer.render(content, position, rotation, context)
    }
}