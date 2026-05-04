package life.sim.simulator

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderer
import life.sim.simulator.rendering.Renderers
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class SimWrapperTest {
    @Test
    fun `SimWrapper stores position rotation and content during construction`() {
        val position = Vector2(1f, 2f)
        val rotation = 45f
        val content = WrappedType("demo")
        Renderers.register(WrappedType::class, WrappedTypeRenderer)

        val wrapper = SimWrapper(content, position, rotation)

        assertSame(position, wrapper.position)
        assertEquals(rotation, wrapper.rotation)
        assertSame(content, wrapper.content)
    }

    @Test
    fun `SimWrapper constructor fails when no renderer is registered for content type`() {
        assertFailsWith<IllegalArgumentException> {
            SimWrapper(NoRendererType, Vector2(0f, 0f), 0f)
        }
    }

    @Test
    fun `SimWrapper update rotates when SPACE is pressed`() {
        Renderers.register(WrappedType::class, WrappedTypeRenderer)
        val wrapper = SimWrapper(WrappedType("demo"), Vector2(0f, 0f), 10f)

        wrapper.update(deltaSeconds = 0.5f, input = dummyInput(pressedKeys = setOf(Input.Keys.SPACE)))

        assertEquals(55f, wrapper.rotation)
    }

    @Test
    fun `SimWrapper update resets rotation when R is just pressed`() {
        Renderers.register(WrappedType::class, WrappedTypeRenderer)
        val wrapper = SimWrapper(WrappedType("demo"), Vector2(0f, 0f), 33f)

        wrapper.update(deltaSeconds = 1f, input = dummyInput(justPressedKeys = setOf(Input.Keys.R)))

        assertEquals(0f, wrapper.rotation)
    }

    @Test
    fun `SimWrapper update prefers SPACE behavior when SPACE and R are both active`() {
        Renderers.register(WrappedType::class, WrappedTypeRenderer)
        val wrapper = SimWrapper(WrappedType("demo"), Vector2(0f, 0f), 10f)

        wrapper.update(
            deltaSeconds = 1f,
            input = dummyInput(
                pressedKeys = setOf(Input.Keys.SPACE),
                justPressedKeys = setOf(Input.Keys.R),
            ),
        )

        assertEquals(100f, wrapper.rotation)
    }

    private fun dummyInput(
        pressedKeys: Set<Int> = emptySet(),
        justPressedKeys: Set<Int> = emptySet(),
    ): Input = Proxy.newProxyInstance(
        Input::class.java.classLoader,
        arrayOf(Input::class.java),
    ) { _, method, args ->
        when (method.name) {
            "isKeyPressed" -> pressedKeys.contains(args?.get(0) as Int)
            "isKeyJustPressed" -> justPressedKeys.contains(args?.get(0) as Int)
            "toString" -> "DummyInput"
            else -> defaultValue(method.returnType)
        }
    } as Input

    private fun defaultValue(returnType: Class<*>): Any? = when (returnType) {
        java.lang.Boolean.TYPE -> false
        java.lang.Byte.TYPE -> 0.toByte()
        java.lang.Short.TYPE -> 0.toShort()
        Integer.TYPE -> 0
        java.lang.Long.TYPE -> 0L
        java.lang.Float.TYPE -> 0f
        java.lang.Double.TYPE -> 0.0
        Character.TYPE -> '\u0000'
        else -> null
    }

    private data class WrappedType(val value: String)
    private data object NoRendererType

    private object WrappedTypeRenderer : Renderer<WrappedType> {
        override fun render(value: WrappedType, position: Vector2, rotation: Float, context: RenderContext) = Unit
        override fun init() = Unit
    }
}