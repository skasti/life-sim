package life.sim.simulator.rendering.geometry

import life.sim.simulator.rendering.ShapeBounds
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val FULL_ROTATION_DEGREES = 360f
private const val ANGLE_EPSILON = 0.0001f
private val CARDINAL_ARC_ANGLES = listOf(0f, 90f, 180f, 270f)

internal data class Arc(
    val x: Float,
    val y: Float,
    val radius: Float,
    val startDegrees: Float,
    val degrees: Float,
    val lineWidth: Float,
)

internal fun Arc.bounds(includeCenter: Boolean = false, includeStroke: Boolean = false): ShapeBounds {
    val strokePadding = if (includeStroke) {
        this.lineWidth * 0.5f
    } else {
        0f
    }

    if (this.radius <= 0f) {
        return ShapeBounds(
            minX = this.x - strokePadding,
            maxX = this.x + strokePadding,
            minY = this.y - strokePadding,
            maxY = this.y + strokePadding,
        )
    }

    if (abs(this.degrees) >= FULL_ROTATION_DEGREES) {
        return ShapeBounds(
            minX = this.x - this.radius - strokePadding,
            maxX = this.x + this.radius + strokePadding,
            minY = this.y - this.radius - strokePadding,
            maxY = this.y + this.radius + strokePadding,
        )
    }

    val xValues = mutableListOf<Float>()
    val yValues = mutableListOf<Float>()

    fun addPointAtAngle(angle: Float) {
        val radians = angle * PI / 180.0
        xValues += (this.x + this.radius * cos(radians)).toFloat()
        yValues += (this.y + this.radius * sin(radians)).toFloat()
    }

    addPointAtAngle(this.startDegrees)
    addPointAtAngle(this.startDegrees + this.degrees)

    CARDINAL_ARC_ANGLES
        .filter { angle -> angleInSweep(angle, this.startDegrees, this.degrees) }
        .forEach(::addPointAtAngle)

    if (includeCenter) {
        xValues += this.x
        yValues += this.y
    }

    return ShapeBounds(
        minX = (xValues.minOrNull() ?: this.x) - strokePadding,
        maxX = (xValues.maxOrNull() ?: this.x) + strokePadding,
        minY = (yValues.minOrNull() ?: this.y) - strokePadding,
        maxY = (yValues.maxOrNull() ?: this.y) + strokePadding,
    )
}

private fun angleInSweep(angle: Float, startDegrees: Float, degrees: Float): Boolean {
    if (abs(degrees) >= FULL_ROTATION_DEGREES - ANGLE_EPSILON) {
        return true
    }

    if (abs(degrees) <= ANGLE_EPSILON) {
        return abs(normalizeAngle(angle) - normalizeAngle(startDegrees)) <= ANGLE_EPSILON
    }

    return if (degrees > 0f) {
        angleInPositiveSweep(angle, startDegrees, degrees)
    } else {
        angleInPositiveSweep(angle, startDegrees + degrees, -degrees)
    }
}

private fun angleInPositiveSweep(angle: Float, startDegrees: Float, degrees: Float): Boolean {
    val normalizedStart = normalizeAngle(startDegrees)
    val normalizedEnd = normalizeAngle(startDegrees + degrees)
    val normalizedAngle = normalizeAngle(angle)

    return if (normalizedStart <= normalizedEnd) {
        normalizedAngle >= normalizedStart - ANGLE_EPSILON && normalizedAngle <= normalizedEnd + ANGLE_EPSILON
    } else {
        normalizedAngle >= normalizedStart - ANGLE_EPSILON || normalizedAngle <= normalizedEnd + ANGLE_EPSILON
    }
}

private fun normalizeAngle(angle: Float): Float {
    val normalized = angle % FULL_ROTATION_DEGREES
    return if (normalized < 0f) {
        normalized + FULL_ROTATION_DEGREES
    } else {
        normalized
    }
}

