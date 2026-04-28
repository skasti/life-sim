package life.sim.simulator

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import life.sim.simulator.rendering.DnaRenderer
import life.sim.simulator.rendering.NucleotideRenderer
import life.sim.simulator.rendering.NucleotideSequenceRenderer
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderers

/**
 * Simulator shell that owns app lifecycle and delegates update/render work to the current scene.
 */
class SimulatorApplication : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var renderContext: RenderContext
    private val camera = OrthographicCamera()
    private lateinit var currentScene: Scene

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
        initializeRenderers()
        renderContext = RenderContext(
            batch = batch,
            font = font,
            shapeRenderer = shapeRenderer,
            viewportWidth = Gdx.graphics.width.toFloat(),
            viewportHeight = Gdx.graphics.height.toFloat(),
        )
        currentScene = DemoScene.sample()
    }



    override fun render() {
        currentScene.update(Gdx.graphics.deltaTime)

        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f)
        renderContext.viewportWidth = Gdx.graphics.width.toFloat()
        renderContext.viewportHeight = Gdx.graphics.height.toFloat()
        currentScene.render(renderContext)
        drawFpsCounter(Gdx.graphics.height.toFloat())
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

    private fun drawFpsCounter(viewportHeight: Float) {
        // Keep shell-level diagnostics outside scene implementations so overlays stay consistent
        // as the simulator grows beyond the demo scene.
        val y = fpsCounterBaselineY(viewportHeight = viewportHeight, lineHeight = font.lineHeight)

        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, formatFpsCounterText(Gdx.graphics.framesPerSecond), FPS_COUNTER_PADDING, y)
        batch.end()
    }

    companion object {
        internal const val FPS_COUNTER_PADDING = 12f

        internal fun formatFpsCounterText(framesPerSecond: Int): String = "FPS: $framesPerSecond"

        internal fun fpsCounterBaselineY(
            viewportHeight: Float,
            lineHeight: Float,
            padding: Float = FPS_COUNTER_PADDING,
        ): Float {
            val preferredBaseline = padding + lineHeight
            val maxVisibleBaseline = viewportHeight - padding

            return if (maxVisibleBaseline >= lineHeight) {
                minOf(preferredBaseline, maxVisibleBaseline)
            } else {
                lineHeight
            }
        }

        internal fun initializeRenderers() {
            NucleotideRenderer()
            NucleotideSequenceRenderer()
            DnaRenderer()
            Renderers.init()
        }
    }
}
