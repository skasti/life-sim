package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

interface Renderer<T : Any> {
    fun render(value: T, position: Vector2, context: RenderContext)
    fun init()
}

data class RenderContext(
    val batch: SpriteBatch,
    val font: BitmapFont,
    val shapeRenderer: ShapeRenderer,
) {
    private enum class DrawMode {
        NONE,
        SHAPE,
        BATCH,
    }

    private val glyphLayout = GlyphLayout()
    private var mode = DrawMode.NONE

    fun drawFilledRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        ensureShapeMode()
        shapeRenderer.color = color
        shapeRenderer.rect(x, y, width, height)
    }

    fun drawLine(x: Float, y: Float, width: Float, height: Float, color: Color) {
        ensureShapeMode()
        shapeRenderer.color = color
        shapeRenderer.rect(x, y, width, height)
    }

    fun drawFilledTriangle(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        color: Color,
    ) {
        ensureShapeMode()
        shapeRenderer.color = color
        shapeRenderer.triangle(x1, y1, x2, y2, x3, y3)
    }

    fun drawText(text: String, x: Float, y: Float, color: Color = Color.WHITE) {
        ensureBatchMode()
        font.color = color
        font.draw(batch, text, x, y)
    }

    fun drawCenteredText(text: String, centerX: Float, centerY: Float, color: Color = Color.WHITE) {
        ensureBatchMode()
        font.color = color
        glyphLayout.setText(font, text)
        val x = centerX - glyphLayout.width * 0.5f
        val y = centerY + glyphLayout.height * 0.5f
        font.draw(batch, glyphLayout, x, y)
    }

    fun finish() {
        when (mode) {
            DrawMode.SHAPE -> shapeRenderer.end()
            DrawMode.BATCH -> batch.end()
            DrawMode.NONE -> Unit
        }
        mode = DrawMode.NONE
    }

    private fun ensureShapeMode() {
        if (mode == DrawMode.SHAPE) {
            return
        }

        if (mode == DrawMode.BATCH) {
            batch.end()
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        mode = DrawMode.SHAPE
    }

    private fun ensureBatchMode() {
        if (mode == DrawMode.BATCH) {
            return
        }

        if (mode == DrawMode.SHAPE) {
            shapeRenderer.end()
        }

        batch.begin()
        mode = DrawMode.BATCH
    }
}
