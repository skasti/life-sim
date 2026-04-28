package life.sim.simulator

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils

/**
 * Simulator shell that owns app lifecycle and delegates update/render work to the current scene.
 */
class SimulatorApplication : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var shapeRenderer: ShapeRenderer
    private val camera = OrthographicCamera()
    private var currentScene: Scene = DemoScene.sample()

    override fun create() {
        batch = SpriteBatch()
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/DejaVuSansMono.ttf"))
        font = try {
            generator.generateFont(
                FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                    size = 22
                },
            )
        } finally {
            generator.dispose()
        }.apply {
            setUseIntegerPositions(true)
        }
        shapeRenderer = ShapeRenderer()
        updateProjectionMatrices(Gdx.graphics.width, Gdx.graphics.height)
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

    override fun resize(width: Int, height: Int) {
        updateProjectionMatrices(width, height)
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        shapeRenderer.dispose()
    }

    private fun updateProjectionMatrices(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
    }
}
