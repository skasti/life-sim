package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

internal enum class PolygonDrawMode {
    FILLED,
    WIREFRAME,
}

internal data class Polygon(
    val vertices: List<Vector2>,
    val drawMode: PolygonDrawMode,
)

internal object polygon {
    fun of(vararg vertices: Vector2, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): PolygonBuilder =
        PolygonBuilder(vertices = vertices.toList().map(Vector2::cpy).toMutableList(), drawMode = drawMode)
}

internal data class ArcPath(
    val start: Vector2,
    val center: Vector2,
    val end: Vector2,
    val segments: Int = 18,
)

internal class PolygonBuilder internal constructor(
    private val vertices: MutableList<Vector2>,
    private val drawMode: PolygonDrawMode,
) {
    fun add(vararg points: Vector2): PolygonBuilder {
        vertices += points.map(Vector2::cpy)
        return this
    }

    fun add(points: Iterable<Vector2>): PolygonBuilder {
        vertices += points.map(Vector2::cpy)
        return this
    }

    fun close(): Polygon {
        val closedVertices = vertices.toMutableList()
        if (closedVertices.size > 2 && closedVertices.first() != closedVertices.last()) {
            closedVertices += closedVertices.first().cpy()
        }
        return Polygon(vertices = closedVertices, drawMode = drawMode)
    }
}

internal fun arc(start: Vector2, center: Vector2, end: Vector2, segments: Int = 18): List<Vector2> {
    require(segments >= 2) { "segments must be >= 2." }
    val radius = start.dst(center)
    require(radius > 0f) { "Arc radius must be > 0." }

    val startAngle = kotlin.math.atan2((start.y - center.y).toDouble(), (start.x - center.x).toDouble())
    val endAngle = kotlin.math.atan2((end.y - center.y).toDouble(), (end.x - center.x).toDouble())

    var delta = endAngle - startAngle
    if (delta <= 0.0) {
        delta += Math.PI * 2.0
    }

    return (0..segments).map { i ->
        val t = i.toDouble() / segments
        val angle = startAngle + delta * t
        Vector2(
            (center.x + radius * kotlin.math.cos(angle)).toFloat(),
            (center.y + radius * kotlin.math.sin(angle)).toFloat(),
        )
    }
}
