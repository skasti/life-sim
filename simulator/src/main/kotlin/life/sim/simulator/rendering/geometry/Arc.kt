package life.sim.simulator.rendering.geometry

import life.sim.simulator.rendering.NucleotideRenderer.Companion.ANGLE_EPSILON
import life.sim.simulator.rendering.NucleotideRenderer.Companion.CARDINAL_ARC_ANGLES
import life.sim.simulator.rendering.NucleotideRenderer.Companion.FULL_ROTATION_DEGREES
import life.sim.simulator.rendering.ShapeBounds
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal data class Arc(
    val x: Float,
    val y: Float,
    val radius: Float,
    val startDegrees: Float,
    val degrees: Float,
    val lineWidth: Float,
)

internal fun Arc.bounds(includeCenter: Boolean = false): ShapeBounds {
    if (this.radius <= 0f) {
        return ShapeBounds(this.x, this.x, this.y, this.y)
    }

    if (abs(this.degrees) >= FULL_ROTATION_DEGREES) {
        return ShapeBounds(
            minX = this.x - this.radius,
            maxX = this.x + this.radius,
            minY = this.y - this.radius,
            maxY = this.y + this.radius,
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
        minX = xValues.minOrNull() ?: this.x,
        maxX = xValues.maxOrNull() ?: this.x,
        minY = yValues.minOrNull() ?: this.y,
        maxY = yValues.maxOrNull() ?: this.y,
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

