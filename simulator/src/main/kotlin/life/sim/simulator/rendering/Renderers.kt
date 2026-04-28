package life.sim.simulator.rendering

import com.badlogic.gdx.math.Vector2
import kotlin.reflect.KClass

object Renderers {
    private val renderers = mutableMapOf<KClass<*>, Renderer<*>>()

    fun <T : Any> register(type: KClass<T>, renderer: Renderer<T>) {
        renderers[type] = renderer
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> forType(type: KClass<T>): Renderer<T>? =
        renderers[type] as? Renderer<T>

    inline fun <reified T : Any> forType(): Renderer<T>? = forType(T::class)

    @Suppress("UNCHECKED_CAST")
    fun forValue(value: Any): Renderer<Any>? =
        renderers[value::class] as? Renderer<Any>

    fun render(value: Any, position: Vector2, context: RenderContext) {
        requireNotNull(forValue(value)) {
            "No renderer registered for type ${value::class.qualifiedName}."
        }.render(value, position, context)
    }
}
