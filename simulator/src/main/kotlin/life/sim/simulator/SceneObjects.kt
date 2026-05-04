package life.sim.simulator

import com.badlogic.gdx.Input
import life.sim.simulator.rendering.RenderContext

/** Marker interface for objects that can participate in simulator scene lifecycle management. */
interface SimObject

/** Optional behavior for scene objects that update over simulation ticks. */
interface Updateable {
    fun update(deltaSeconds: Float, input: Input)
}

/** Optional behavior for scene objects that render through the simulator render context. */
interface Renderable {
    fun render(context: RenderContext)
}

