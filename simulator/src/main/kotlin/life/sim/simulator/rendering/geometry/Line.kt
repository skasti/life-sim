package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext

internal data class Line(
    val a: Vector2,
    val b: Vector2,
    val width: Float,
    val color: Color,
) : GeometryElement {
    override fun render(context: RenderContext) {
        context.drawLine(a, b, width, color)
    }
}
