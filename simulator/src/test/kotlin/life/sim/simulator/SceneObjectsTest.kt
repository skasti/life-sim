package life.sim.simulator

import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class SceneObjectsTest {
    @Test
    fun `ObjectManager tracks updateable and renderable objects in deterministic insertion order`() {
        val manager = ObjectManager()
        val first = UpdateOnlyObject("first")
        val second = RenderOnlyObject("second")
        val third = UpdateAndRenderObject("third")

        manager.add(first)
        manager.add(second)
        manager.add(third)
        manager.add(InertObject)

        assertEquals(listOf(first, third), manager.updateablesInOrder())
        assertEquals(listOf(second, third), manager.renderablesInOrder())
    }

    @Test
    fun `ObjectManager remove prevents later update and render classification`() {
        val manager = ObjectManager()
        val tracked = UpdateAndRenderObject("tracked")

        manager.add(tracked)
        manager.remove(tracked)

        assertEquals(emptyList(), manager.updateablesInOrder())
        assertEquals(emptyList(), manager.renderablesInOrder())
    }

    @Test
    fun `SimWrapper stores position content and resolves renderer during construction`() {
        val position = Vector2(1f, 2f)
        val content = WrappedType("demo")
        Renderers.register(WrappedType::class, WrappedTypeRenderer)

        val wrapper = SimWrapper(position, content)

        assertSame(position, wrapper.position)
        assertSame(content, wrapper.content)
    }

    @Test
    fun `SimWrapper constructor fails when no renderer is registered for content type`() {
        assertFailsWith<IllegalArgumentException> {
            SimWrapper(Vector2(0f, 0f), NoRendererType)
        }
    }

    private data class WrappedType(val value: String)
    private data object NoRendererType

    private object WrappedTypeRenderer : Renderer<WrappedType> {
        override fun render(value: WrappedType, position: Vector2, context: RenderContext) = Unit
        override fun init() = Unit
    }

    private object InertObject : SimObject

    private class UpdateOnlyObject(private val label: String) : SimObject, Updateable {
        override fun update(deltaSeconds: Float) = Unit
        override fun toString(): String = label
    }

    private class RenderOnlyObject(private val label: String) : SimObject, Renderable {
        override fun render(context: RenderContext) = Unit
        override fun toString(): String = label
    }

    private class UpdateAndRenderObject(private val label: String) : SimObject, Updateable, Renderable {
        override fun update(deltaSeconds: Float) = Unit
        override fun render(context: RenderContext) = Unit
        override fun toString(): String = label
    }
}
