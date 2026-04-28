package life.sim.simulator

import life.sim.simulator.rendering.RenderContext

/**
 * Owns a deterministic ordered set of scene objects and coordinates update/render capabilities.
 */
class ObjectManager {
    private val objects = linkedSetOf<SimObject>()
    private val addQueue = linkedSetOf<SimObject>()
    private val removeQueue = linkedSetOf<SimObject>()

    private val updatable = mutableListOf<Updateable>()
    private val renderable = mutableListOf<Renderable>()

    fun add(vararg newObjects: SimObject) {
        for (obj in newObjects) {
            addQueue += obj
        }
    }

    fun remove(obj: SimObject) {
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
            addQueue -= obj
            if (objects.remove(obj)) {
                if (obj is Updateable) {
                    updatable -= obj
                }

                if (obj is Renderable) {
                    renderable -= obj
                }
            }
        }
        removeQueue.clear()

        for (obj in addQueue) {
            if (objects.add(obj)) {
                if (obj is Updateable) {
                    updatable += obj
                }

                if (obj is Renderable) {
                    renderable += obj
                }
            }
        }
        addQueue.clear()
    }
    internal fun updatablesList(): List<Updateable> = updatable.toList()
    internal fun renderablesList(): List<Renderable> = renderable.toList()
    internal fun objectCount(): Int = objects.size
}
