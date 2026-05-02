package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import life.sim.simulator.rendering.geometry.*
import kotlin.math.cbrt
import kotlin.math.max

interface Renderer<T : Any> {
    fun render(value: T, position: Vector2, context: RenderContext)
    fun init()

    fun spriteKey(value: T): SpriteKey? = null

    fun renderToSprite(value: T, context: RenderContext): TextureRegion? = null
}

data class RenderContext(
    val batch: SpriteBatch,
    val font: BitmapFont,
    val shapeRenderer: ShapeRenderer,
    var viewportWidth: Float,
    var viewportHeight: Float,
    val immediateModeRenderer: ImmediateModeRenderer20,
    val sprites: Sprites,
) {
    private enum class DrawMode {
        NONE,
        SHAPE,
        BATCH,
    }

    private val glyphLayout = GlyphLayout()
    private var mode = DrawMode.NONE
    private var shapeType = ShapeType.Point

    fun drawFilledRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        ensureShapeMode(ShapeType.Filled)
        shapeRenderer.color = color
        shapeRenderer.rect(x, y, width, height)
    }

    fun drawLine(a: Vector2, b: Vector2, width: Float, color: Color) {
        ensureShapeMode()
        shapeRenderer.color = color
        shapeRenderer.rectLine(a, b, width)
    }

    fun drawTriangle(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        color: Color,
    ) {
        ensureShapeMode(ShapeType.Line)
        shapeRenderer.color = color
        shapeRenderer.triangle(x1, y1, x2, y2, x3, y3)
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
        ensureShapeMode(ShapeType.Filled)
        shapeRenderer.color = color
        shapeRenderer.triangle(x1, y1, x2, y2, x3, y3)
    }

    fun drawFilledArc(
        x: Float,
        y: Float,
        radius: Float,
        startDegrees: Float,
        degrees: Float,
        color: Color,
    ) {
        ensureShapeMode(ShapeType.Filled)
        shapeRenderer.color = color
        shapeRenderer.arc(x, y, radius, startDegrees, degrees)
    }

    fun drawArc(
        x: Float,
        y: Float,
        radius: Float,
        startDegrees: Float,
        degrees: Float,
        color: Color,
        lineWidth: Float,
    ) {
        ensureShapeMode(ShapeType.Filled)
        shapeRenderer.color = color

        val segments = max(1, (6 * cbrt(radius.toDouble()).toFloat() * (degrees / 360.0f)).toInt())
        require(segments > 0) { "segments must be > 0." }
        val theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments
        val cos = MathUtils.cos(theta)
        val sin = MathUtils.sin(theta)
        var cx = radius * MathUtils.cos(startDegrees * MathUtils.degreesToRadians)
        var cy = radius * MathUtils.sin(startDegrees * MathUtils.degreesToRadians)

        val a = Vector2(x + cx, y + cy)
        val b = Vector2(x + cx, y + cy)
        repeat(segments) {
            a.x = x + cx
            a.y = y + cy

            val temp = cx
            cx = cos * cx - sin * cy
            cy = sin * temp + cos * cy
            b.x = x + cx
            b.y = y + cy

            shapeRenderer.rectLine(a, b, lineWidth)
        }
    }


    internal fun drawPolygon(vertices: List<Vector2>, drawMode: PolygonDrawMode, color: Color) {
        if (vertices.size < 3) return
        finish()
        val renderVertices = if (drawMode == PolygonDrawMode.FILLED) {
            triangulatePolygon(vertices).flatMap { triangle ->
                listOf(
                    Vector2(triangle.x1, triangle.y1),
                    Vector2(triangle.x2, triangle.y2),
                    Vector2(triangle.x3, triangle.y3),
                )
            }
        } else {
            vertices
        }
        if (renderVertices.isEmpty()) return

        val glMode = if (drawMode == PolygonDrawMode.FILLED) GL20.GL_TRIANGLES else GL20.GL_LINE_STRIP
        immediateModeRenderer.begin(shapeRenderer.projectionMatrix, glMode)
        renderVertices.forEach { vertex ->
            immediateModeRenderer.color(color.r, color.g, color.b, color.a)
            immediateModeRenderer.vertex(vertex.x, vertex.y, 0f)
        }
        immediateModeRenderer.end()
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

    fun drawSprite(
        key: SpriteKey,
        position: Vector2,
        width: Float,
        height: Float,
        rotationDegrees: Float = 0f,
    ) {
        ensureBatchMode()
        sprites.draw(key, batch, position, width, height, rotationDegrees)
    }

    fun finish() {
        when (mode) {
            DrawMode.SHAPE -> shapeRenderer.end()
            DrawMode.BATCH -> batch.end()
            DrawMode.NONE -> Unit
        }
        mode = DrawMode.NONE
    }

    private fun ensureShapeMode(type: ShapeType = ShapeType.Filled) {
        if (mode == DrawMode.SHAPE && shapeType == type) {
            return
        }

        if (mode == DrawMode.BATCH) {
            batch.end()
        } else if (mode == DrawMode.SHAPE && shapeType != type) {
            shapeRenderer.end()
        }

        shapeRenderer.begin(type)
        shapeType = type
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
