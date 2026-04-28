package life.sim.simulator

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/**
 * Scene/state contract for simulator runtime behavior.
 */
interface Scene {
    fun update(deltaSeconds: Float)

    fun render(
        batch: SpriteBatch,
        font: BitmapFont,
        shapeRenderer: ShapeRenderer,
        viewportWidth: Float,
        viewportHeight: Float,
    )
}
