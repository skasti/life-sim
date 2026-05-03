package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceDirection

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

    override fun render(value: NucleotideSequence, position: Vector2, rotation: Float, context: RenderContext) {
        val totalWidth = sequenceWidth(value)

        val backBoneY = position.y
        context.drawLine(
            Vector2(position.x - 8f, backBoneY),
            Vector2(position.x + totalWidth + 8f, backBoneY),
            width = 3f,
            color = BACKBONE_COLOR,
        )

        var x = position.x
        value.forEach { nucleotide ->
            nucleotidePosition.x = x
            nucleotidePosition.y = position.y
            nucleotideRenderer.render(
                nucleotide,
                nucleotidePosition,
                rotation + 90,
                context,
            )
            x += baseSize + tileGap
        }

        drawDirectionIndicator(value.direction, position.x, position.y, totalWidth, context)
    }

    fun sequenceWidth(value: NucleotideSequence): Float {
        if (value.isEmpty()) {
            return 0f
        }

        return value.size * baseSize + (value.size - 1) * tileGap
    }

    private fun drawDirectionIndicator(
        direction: SequenceDirection,
        x: Float,
        y: Float,
        width: Float,
        context: RenderContext,
    ) {
        val centerY = y + baseSize * 0.5f
        val arrowHeight = 8f
        val arrowWidth = 12f

        if (direction == SequenceDirection.FORWARD) {
            val arrowX = x + width + 12f
            context.drawFilledTriangle(
                arrowX,
                centerY,
                arrowX - arrowWidth,
                centerY + arrowHeight,
                arrowX - arrowWidth,
                centerY - arrowHeight,
                DIRECTION_INDICATOR_COLOR,
            )
            return
        }

        val arrowX = x - 12f
        context.drawFilledTriangle(
            arrowX,
            centerY,
            arrowX + arrowWidth,
            centerY + arrowHeight,
            arrowX + arrowWidth,
            centerY - arrowHeight,
            DIRECTION_INDICATOR_COLOR,
        )
    }

    companion object {
        private val BACKBONE_COLOR = Color(0.2f, 0.6f, 0.95f, 1f)
        private val DIRECTION_INDICATOR_COLOR = Color(0.9f, 0.92f, 1f, 1f)
    }
}

