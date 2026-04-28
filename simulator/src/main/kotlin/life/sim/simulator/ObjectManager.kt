package life.sim.simulator

import life.sim.simulator.rendering.RenderContext

/**
 * Owns a deterministic ordered set of scene objects and coordinates update/render capabilities.
 */
class ObjectManager {
    private val objects = mutableListOf<SimObject>()
    private val addQueue = mutableListOf<SimObject>()
    private val removeQueue = mutableListOf<SimObject>()

    private val updatable = mutableListOf<Updateable>()
    private val renderable = mutableListOf<Renderable>()

    fun add(vararg newObjects: SimObject) {
        for (obj in newObjects) {
            if (addQueue.contains(obj))
                continue

            addQueue += obj
        }
    }

    fun remove(obj: SimObject) {
        if (removeQueue.contains(obj))
            return

        removeQueue += obj
    }

    fun update(deltaSeconds: Float) {
        updatable.forEach { it.update(deltaSeconds) }
        processQueues()
    }

    fun render(context: RenderContext) {
        renderable.forEach { it.render(context) }
    }

    internal fun processQueues() {
        for (obj in removeQueue) {
            objects -= obj
            if (obj is Updateable) {
                updatable -= obj
            }

            if (obj is Renderable) {
                renderable -= obj
            }
        }
        removeQueue.clear()

        for (obj in addQueue) {
            if (objects.contains(obj))
                continue

            objects += obj
            if (obj is Updateable && !updatable.contains(obj)) {
                updatable += obj
            }

            if (obj is Renderable && !renderable.contains(obj)) {
                renderable += obj
            }
        }
        addQueue.clear()
    }
    internal fun updatablesList(): List<Updateable> = updatable.toList()
    internal fun renderablesList(): List<Renderable> = renderable.toList()
    internal fun objectCount(): Int = objects.size
}