package life.sim.simulator.rendering

internal data class ShapeBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
) {
    fun isWithin(minX: Float, maxX: Float, minY: Float, maxY: Float): Boolean =
        this.minX >= minX &&
            this.maxX <= maxX &&
            this.minY >= minY &&
            this.maxY <= maxY
}

