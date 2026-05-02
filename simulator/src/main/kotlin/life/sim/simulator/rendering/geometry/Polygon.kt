package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.RenderContext

internal enum class PolygonDrawMode {
    FILLED,
    WIREFRAME,
}

internal enum class ArcSweepDirection {
    COUNTERCLOCKWISE,
    CLOCKWISE,
}

internal data class Polygon(
    val vertices: List<Vector2>,
    val color: Color,
    val drawMode: PolygonDrawMode,
) : GeometryElement {
    override fun render(context: RenderContext) {
        context.drawPolygon(vertices, drawMode, color)
    }

    companion object {
        fun of(vararg vertices: Vector2, color: Color, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): PolygonBuilder =
            PolygonBuilder(vertices = vertices.toList().map(Vector2::cpy).toMutableList(), color = color, drawMode = drawMode)

        fun rect(x: Float, y: Float, width: Float, height: Float, color: Color, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): Polygon =
            of(
                Vector2(x, y),
                Vector2(x + width, y),
                Vector2(x + width, y + height),
                Vector2(x, y + height),
                color = color,
                drawMode = drawMode,
            ).close()

        fun triangle(a: Vector2, b: Vector2, c: Vector2, color: Color, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): Polygon =
            of(a, b, c, color = color, drawMode = drawMode).close()

        fun circle(center: Vector2, radius: Float, color: Color, segments: Int = 24, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): Polygon {
            require(segments >= 3) { "segments must be >= 3." }
            require(radius > 0f) { "radius must be > 0." }
            val points = (0 until segments).map { i ->
                val angle = (Math.PI * 2.0 * i.toDouble()) / segments.toDouble()
                Vector2(
                    (center.x + radius * kotlin.math.cos(angle)).toFloat(),
                    (center.y + radius * kotlin.math.sin(angle)).toFloat(),
                )
            }
            return PolygonBuilder(vertices = points.toMutableList(), color = color, drawMode = drawMode).close()
        }

    }
}

internal class PolygonBuilder internal constructor(
    private val vertices: MutableList<Vector2>,
    private val color: Color,
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
        return Polygon(vertices = closedVertices, color = color, drawMode = drawMode)
    }
}

internal fun arc(
    start: Vector2,
    center: Vector2,
    end: Vector2,
    segments: Int = 18,
    sweepDirection: ArcSweepDirection = ArcSweepDirection.COUNTERCLOCKWISE,
): List<Vector2> {
    require(segments >= 2) { "segments must be >= 2." }
    val radius = start.dst(center)
    require(radius > 0f) { "Arc radius must be > 0." }

    val startAngle = kotlin.math.atan2((start.y - center.y).toDouble(), (start.x - center.x).toDouble())
    val endAngle = kotlin.math.atan2((end.y - center.y).toDouble(), (end.x - center.x).toDouble())

    var delta = endAngle - startAngle
    if (sweepDirection == ArcSweepDirection.COUNTERCLOCKWISE) {
        if (delta <= 0.0) {
            delta += Math.PI * 2.0
        }
    } else {
        if (delta >= 0.0) {
            delta -= Math.PI * 2.0
        }
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

internal fun triangulatePolygon(vertices: List<Vector2>): List<Triangle> {
    val outline = polygonOutline(vertices)
    if (outline.size < 3) return emptyList()

    val packedVertices = FloatArray(outline.size * 2)
    outline.forEachIndexed { index, vertex ->
        packedVertices[index * 2] = vertex.x
        packedVertices[index * 2 + 1] = vertex.y
    }

    val triangleIndices = EarClippingTriangulator().computeTriangles(packedVertices)
    val indices = triangleIndices.items

    return buildList(triangleIndices.size / 3) {
        var index = 0
        while (index < triangleIndices.size) {
            val a = outline[indices[index].toInt()]
            val b = outline[indices[index + 1].toInt()]
            val c = outline[indices[index + 2].toInt()]
            add(Triangle(a.x, a.y, b.x, b.y, c.x, c.y))
            index += 3
        }
    }
}

internal fun polygonOutline(vertices: List<Vector2>): List<Vector2> =
    if (vertices.size > 1 && vertices.first() == vertices.last()) {
        vertices.dropLast(1)
    } else {
        vertices
    }
