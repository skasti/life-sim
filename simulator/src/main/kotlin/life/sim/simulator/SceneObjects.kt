package life.sim.simulator

import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers

/** Marker interface for objects that can participate in simulator scene lifecycle management. */
interface SimObject

/** Optional behavior for scene objects that update over simulation ticks. */
interface Updateable {
    fun update(deltaSeconds: Float)
}

/** Optional behavior for scene objects that render through the simulator render context. */
interface Renderable {
    fun render(context: RenderContext)
}

/**
 * Owns a deterministic ordered set of scene objects and coordinates update/render capabilities.
 */
class ObjectManager {
    private val objects = mutableListOf<SimObject>()
    private val addQueue = mutableListOf<SimObject>()
    private val removeQueue = mutableListOf<SimObject>()

    fun add(vararg newObjects: SimObject) {
        for (obj in newObjects)
            addQueue += obj
    }

    fun remove(obj: SimObject) {
        removeQueue += obj
    }

    fun update(deltaSeconds: Float) {
        updateablesInOrder().forEach { it.update(deltaSeconds) }

        // Apply queued changes after update pass to avoid concurrent modification issues and ensure deterministic ordering.
        objects -= removeQueue.toSet()
        removeQueue.clear()

        objects += addQueue
        addQueue.clear()
    }

    fun render(context: RenderContext) {
        renderablesInOrder().forEach { it.render(context) }
    }

    internal fun updateablesInOrder(): List<Updateable> =
        objects.toList().mapNotNull { it as? Updateable }

    internal fun renderablesInOrder(): List<Renderable> =
        objects.toList().mapNotNull { it as? Renderable }

    internal fun objectCount(): Int = objects.size
}

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
