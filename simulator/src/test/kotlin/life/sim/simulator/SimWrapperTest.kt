package life.sim.simulator

import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class SimWrapperTest {
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
}