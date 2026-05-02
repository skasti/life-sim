package life.sim.simulator.rendering.geometry

import life.sim.simulator.rendering.RenderContext

internal interface GeometryElement {
    fun render(context: RenderContext)
}
