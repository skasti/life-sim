package life.sim.simulator

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers

/**
 * Bridges domain objects to simulator scene lifecycle by resolving a renderer once at construction.
 *
 * `position` is the wrapped object's visual center in world coordinates.
 *
 * `rotation` is in degrees and is passed directly to the resolved renderer.
 * The simulator follows libGDX angle conventions: `0f` is unrotated and positive values rotate
 * counterclockwise around `position`.
 */
class SimWrapper(
    val content: Any,
    val position: Vector2,
    var rotation: Float = 0f,
) : SimObject, Renderable, Updateable {
    private val renderer: Renderer<Any> = requireNotNull(Renderers.forValue(content)) {
        "No renderer registered for type ${content::class.qualifiedName}."
    }

    override fun update(deltaSeconds: Float, input: Input) {
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