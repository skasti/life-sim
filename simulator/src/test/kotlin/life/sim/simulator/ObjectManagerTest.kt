package life.sim.simulator

import com.badlogic.gdx.Input
import life.sim.simulator.rendering.RenderContext
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectManagerTest {
    @Test
    fun `ObjectManager tracks updatable and renderable objects in deterministic insertion order`() {
        val manager = ObjectManager()
        val first = UpdateOnlyObject("first")
        val second = RenderOnlyObject("second")
        val third = UpdateAndRenderObject("third")

        manager.add(first)
        manager.add(second)
        manager.add(third)
        manager.add(InertObject)
        manager.processQueues()

        assertEquals(listOf(first, third), manager.updatablesList())
        assertEquals(listOf(second, third), manager.renderablesList())
    }

    @Test
    fun `ObjectManager add does not immediately track objects before processQueues`() {
        val manager = ObjectManager()
        val updatable = UpdateOnlyObject("updatable")
        val renderable = RenderOnlyObject("renderable")
        val updateAndRender = UpdateAndRenderObject("updateAndRender")

        manager.add(updatable)
        manager.add(renderable)
        manager.add(updateAndRender)

        assertEquals(emptyList(), manager.updatablesList())
        assertEquals(emptyList(), manager.renderablesList())
        assertEquals(0, manager.objectCount())

        manager.processQueues()

        assertEquals(listOf(updatable, updateAndRender), manager.updatablesList())
        assertEquals(listOf(renderable, updateAndRender), manager.renderablesList())
        assertEquals(3, manager.objectCount())
    }

    @Test
    fun `ObjectManager remove does not immediately untrack objects before processQueues and processQueues removes them`() {
        val manager = ObjectManager()
        val updatable = UpdateOnlyObject("updatable")
        val renderable = RenderOnlyObject("renderable")
        val updateAndRender = UpdateAndRenderObject("updateAndRender")

        manager.add(updatable)
        manager.add(renderable)
        manager.add(updateAndRender)
        manager.processQueues()

        manager.remove(updatable)
        manager.remove(renderable)

        assertEquals(listOf(updatable, updateAndRender), manager.updatablesList())
        assertEquals(listOf(renderable, updateAndRender), manager.renderablesList())
        assertEquals(3, manager.objectCount())

        manager.processQueues()

        assertEquals(listOf(updateAndRender), manager.updatablesList())
        assertEquals(listOf(updateAndRender), manager.renderablesList())
        assertEquals(1, manager.objectCount())
    }

    @Test
    fun `ObjectManager removes objects from the lists`() {
        val manager = ObjectManager()
        val tracked = UpdateAndRenderObject("tracked")

        manager.add(tracked)
        manager.processQueues()

        assertEquals(listOf(tracked), manager.updatablesList())
        assertEquals(listOf(tracked), manager.renderablesList())

        manager.remove(tracked)
        manager.processQueues()

        assertEquals(emptyList(), manager.updatablesList())
        assertEquals(emptyList(), manager.renderablesList())
    }

    @Test
    fun `ObjectManager does not duplicate entries when adding the same object multiple times`() {
        val manager = ObjectManager()
        val tracked = UpdateAndRenderObject("tracked")

        manager.add(tracked)
        manager.add(tracked)
        manager.add(tracked)

        manager.processQueues()

        assertEquals(listOf(tracked), manager.updatablesList())
        assertEquals(listOf(tracked), manager.renderablesList())
        assertEquals(1, manager.objectCount())
    }

    @Test
    fun `ObjectManager remove cancels pending add for the same object in the same queue drain`() {
        val manager = ObjectManager()
        val tracked = UpdateAndRenderObject("tracked")

        manager.add(tracked)
        manager.remove(tracked)

        manager.processQueues()

        assertEquals(emptyList(), manager.updatablesList())
        assertEquals(emptyList(), manager.renderablesList())
        assertEquals(0, manager.objectCount())
    }

    private object InertObject : SimObject

    private class UpdateOnlyObject(private val label: String) : SimObject, Updateable {
        override fun update(deltaSeconds: Float, input : Input) = Unit
        override fun toString(): String = label
    }

    private class RenderOnlyObject(private val label: String) : SimObject, Renderable {
        override fun render(context: RenderContext) = Unit
        override fun toString(): String = label
    }

    private class UpdateAndRenderObject(private val label: String) : SimObject, Updateable, Renderable {
        override fun update(deltaSeconds: Float, input : Input) = Unit
        override fun render(context: RenderContext) = Unit
        override fun toString(): String = label
    }
}
