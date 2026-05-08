package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceDirection
import life.sim.simulator.rendering.geometry.rotatePoint
import life.sim.simulator.rendering.geometry.rotatePoints

class NucleotideSequenceRenderer(
    val tileGap: Float = 10f,
    val baseSize: Float = RenderingVisualSpec.NUCLEOTIDE_BASE_SIZE,
) : Renderer<NucleotideSequence> {
    private lateinit var nucleotideRenderer: Renderer<Nucleotide>
    private val nucleotidePosition = Vector2()

    init {
        Renderers.register(NucleotideSequence::class, this)
    }

    override fun init() {
        nucleotideRenderer = Renderers.forType<Nucleotide>()
            ?: error("NucleotideSequenceRenderer requires a registered renderer for Nucleotide.")
    }

    override fun render(value: NucleotideSequence, transform: Matrix3, context: RenderContext) {
        val layout = layout(value, Vector2(0f, 0f)) ?: return
        renderLayout(value, layout, transform, context)
        context.drawCircle(transform.getTranslation(Vector2()), baseSize * 0.1f, Color.GREEN)
    }

    fun sequenceWidth(value: NucleotideSequence): Float {
        if (value.isEmpty()) {
            return 0f
        }

        return value.size * baseSize + (value.size - 1) * tileGap
    }

    internal fun layout(value: NucleotideSequence, position: Vector2): SequenceRenderLayout? {
        if (value.isEmpty()) {
            return null
        }

        val totalWidth = sequenceWidth(value)
        val leftEdgeX = position.x - totalWidth * 0.5f
        val backboneStart = Vector2(leftEdgeX - BACKBONE_OVERHANG, position.y)
        val backboneEnd = Vector2(leftEdgeX + totalWidth + BACKBONE_OVERHANG, position.y)
        val pivot = position.cpy()

        val nucleotideAnchors = buildList(value.size) {
            var x = leftEdgeX + baseSize * 0.5f
            repeat(value.size) {
                add(Vector2(x, position.y))
                x += baseSize + tileGap
            }
        }

        return SequenceRenderLayout(
            pivot = pivot,
            backboneStart = backboneStart,
            backboneEnd = backboneEnd,
            nucleotideAnchors = nucleotideAnchors,
            directionIndicatorVertices = directionIndicatorVertices(value.direction, leftEdgeX, position.y, totalWidth),
        )
    }

    internal fun renderLayout(
        value: NucleotideSequence,
        layout: SequenceRenderLayout,
        transform: Matrix3,
        context: RenderContext,
    ) {
        value.zip(layout.nucleotideAnchors).forEach { (nucleotide, anchor) ->
            nucleotidePosition.set(anchor).mul(transform)
            nucleotideRenderer.render(nucleotide, nucleotidePosition, nucleotideRotation(value.direction, transform.getRotation()), context)
        }

        context.drawLine(
            layout.backboneStart.mul(transform),
            layout.backboneEnd.mul(transform),
            width = BACKBONE_WIDTH,
            color = BACKBONE_COLOR,
        )

        val directionIndicatorVertices = layout.directionIndicatorVertices.map { it.cpy().mul(transform) }
        context.drawFilledTriangle(
            directionIndicatorVertices[0].x,
            directionIndicatorVertices[0].y,
            directionIndicatorVertices[1].x,
            directionIndicatorVertices[1].y,
            directionIndicatorVertices[2].x,
            directionIndicatorVertices[2].y,
            DIRECTION_INDICATOR_COLOR,
        )
    }

    private fun directionIndicatorVertices(
        direction: SequenceDirection,
        x: Float,
        backboneY: Float,
        width: Float,
    ): List<Vector2> {
        val centerY = nucleotideCenterY(backboneY, direction)
        val arrowHeight = DIRECTION_INDICATOR_HEIGHT
        val arrowWidth = DIRECTION_INDICATOR_WIDTH

        if (direction == SequenceDirection.FORWARD) {
            val arrowX = x + width + 12f
            return listOf(
                Vector2(arrowX, centerY),
                Vector2(arrowX - arrowWidth, centerY + arrowHeight),
                Vector2(arrowX - arrowWidth, centerY - arrowHeight),
            )
        }

        val arrowX = x - 12f
        return listOf(
            Vector2(arrowX, centerY),
            Vector2(arrowX + arrowWidth, centerY + arrowHeight),
            Vector2(arrowX + arrowWidth, centerY - arrowHeight),
        )
    }

    private fun nucleotideCenterY(backboneY: Float, direction: SequenceDirection): Float =
        if (direction == SequenceDirection.FORWARD) {
            backboneY - baseSize * 0.5f
        } else {
            backboneY + baseSize * 0.5f
        }

    internal fun nucleotideRotation(direction: SequenceDirection, modelRotation: Float): Float {
        val strandFacingOffset = if (direction == SequenceDirection.FORWARD) {
            FORWARD_STRAND_FACING_OFFSET
        } else {
            BACKWARD_STRAND_FACING_OFFSET
        }
        return modelRotation + strandFacingOffset
    }

    companion object {
        internal const val BACKBONE_OVERHANG = 8f
        private const val BACKBONE_WIDTH = 3f
        private const val DIRECTION_INDICATOR_WIDTH = 12f
        private const val DIRECTION_INDICATOR_HEIGHT = 8f
        private const val FORWARD_STRAND_FACING_OFFSET = 270f
        private const val BACKWARD_STRAND_FACING_OFFSET = 90f
        private val BACKBONE_COLOR = Color(0.2f, 0.6f, 0.95f, 1f)
        private val DIRECTION_INDICATOR_COLOR = Color(0.9f, 0.92f, 1f, 1f)
    }
}

internal data class SequenceRenderLayout(
    val pivot: Vector2,
    val backboneStart: Vector2,
    val backboneEnd: Vector2,
    val nucleotideAnchors: List<Vector2>,
    val directionIndicatorVertices: List<Vector2>,
)

internal fun SequenceRenderLayout.rotated(rotation: Float): SequenceRenderLayout =
    if (rotation % 360f == 0f) {
        this.copy(
            pivot = pivot.cpy(),
            backboneStart = backboneStart.cpy(),
            backboneEnd = backboneEnd.cpy(),
            nucleotideAnchors = nucleotideAnchors.map(Vector2::cpy),
            directionIndicatorVertices = directionIndicatorVertices.map(Vector2::cpy),
        )
    } else {
        this.copy(
            pivot = pivot.cpy(),
            backboneStart = rotatePoint(backboneStart, pivot, rotation),
            backboneEnd = rotatePoint(backboneEnd, pivot, rotation),
            nucleotideAnchors = rotatePoints(nucleotideAnchors, pivot, rotation),
            directionIndicatorVertices = rotatePoints(directionIndicatorVertices, pivot, rotation),
        )
    }

