package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2

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
    val drawMode: PolygonDrawMode,
) {
    companion object {
        fun of(vararg vertices: Vector2, drawMode: PolygonDrawMode = PolygonDrawMode.FILLED): PolygonBuilder =
            PolygonBuilder(vertices = vertices.toList().map(Vector2::cpy).toMutableList(), drawMode = drawMode)
    }
}

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

/**
 * Generates an arc from `start` to `end` around `center`.
 *
 * The radius is taken from the distance between `start` and `center`, and the returned list contains
 * `segments + 1` evenly spaced points including the generated start and final arc point.
 *
 * `sweepDirection` controls which side of the circle is traced when `start` and `end` alone are ambiguous,
 * such as opposite points on the same diameter. The final point coincides with `end` only when `end` lies on
 * that same circle.
 */
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

