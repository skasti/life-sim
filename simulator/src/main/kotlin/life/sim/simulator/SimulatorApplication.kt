package life.sim.simulator

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils

/**
 * Simulator shell that owns app lifecycle and delegates update/render work to the current scene.
 */
class SimulatorApplication : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var shapeRenderer: ShapeRenderer
    private var currentScene: Scene = DemoScene.sample()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont().apply {
            data.setScale(1.4f)
        }
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f)

        currentScene.update(Gdx.graphics.deltaTime)
        currentScene.render(
            batch = batch,
            font = font,
            shapeRenderer = shapeRenderer,
            viewportWidth = Gdx.graphics.width.toFloat(),
            viewportHeight = Gdx.graphics.height.toFloat(),
        )
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
    }
}
