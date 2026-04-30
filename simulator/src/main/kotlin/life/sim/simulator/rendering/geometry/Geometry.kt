package life.sim.simulator.rendering.geometry

import life.sim.simulator.rendering.RenderContext

internal data class Geometry(
    val elements: List<GeometryElement>,
) {
    constructor(vararg elements: GeometryElement) : this(elements.toList())
}

internal fun Geometry.render(context: RenderContext) {
    this.elements.forEach { element -> element.render(context) }
}
