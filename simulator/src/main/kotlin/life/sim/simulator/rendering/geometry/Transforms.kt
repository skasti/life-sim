package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

private const val FULL_ROTATION = 360f

internal fun rotatePoint(point: Vector2, pivot: Vector2, rotationDegrees: Float): Vector2 {
    if (rotationDegrees % FULL_ROTATION == 0f) {
        return point.cpy()
    }

    val translatedX = point.x - pivot.x
    val translatedY = point.y - pivot.y
    val cos = MathUtils.cosDeg(rotationDegrees)
    val sin = MathUtils.sinDeg(rotationDegrees)

    return Vector2(
        pivot.x + translatedX * cos - translatedY * sin,
        pivot.y + translatedX * sin + translatedY * cos,
    )
}

internal fun rotatePoints(points: Iterable<Vector2>, pivot: Vector2, rotationDegrees: Float): List<Vector2> =
    points.map { point -> rotatePoint(point, pivot, rotationDegrees) }
