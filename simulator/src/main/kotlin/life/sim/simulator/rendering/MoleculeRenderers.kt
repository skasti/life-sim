package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceDirection

class NucleotideRenderer(
    val tileSize: Float = 34f,
) : Renderer<Nucleotide> {

    init {
        Renderers.register(Nucleotide::class, this)
    }

    override fun init() {
        // Nothing to do here since this renderer has no dependencies on other renderers.
    }

    override fun render(value: Nucleotide, position: Vector2, context: RenderContext) {
        context.drawFilledRect(position.x, position.y, tileSize, tileSize, nucleotideColor(value))
        context.drawCenteredText(value.symbol.toString(), position.x + tileSize * 0.5f, position.y + tileSize * 0.5f)
    }

    private fun nucleotideColor(nucleotide: Nucleotide): Color = when (nucleotide) {
        Nucleotide.A -> ADENINE_COLOR
        Nucleotide.U -> URACIL_COLOR
        Nucleotide.C -> CYTOSINE_COLOR
        Nucleotide.G -> GUANINE_COLOR
    }

    companion object {
        private val ADENINE_COLOR = Color(0.95f, 0.65f, 0.3f, 1f)
        private val URACIL_COLOR = Color(0.36f, 0.78f, 0.95f, 1f)
        private val CYTOSINE_COLOR = Color(0.55f, 0.88f, 0.42f, 1f)
        private val GUANINE_COLOR = Color(0.9f, 0.42f, 0.76f, 1f)
    }
}

data class SequenceRenderStyle(
    val showBackbone: Boolean = true,
    val showDirectionIndicator: Boolean = true,
)

class NucleotideSequenceRenderer(
    val tileGap: Float = 10f,
    val tileSize: Float = 34f,
) : Renderer<NucleotideSequence> {
    private lateinit var nucleotideRenderer: Renderer<Nucleotide>
    private val nucleotidePosition = Vector2()

    init {
        Renderers.register(NucleotideSequence::class, this)
    }

    override fun init() {
        nucleotideRenderer = Renderers.forType<Nucleotide>() ?: error("NucleotideSequenceRenderer requires a registered renderer for Nucleotide.")
    }

    override fun render(value: NucleotideSequence, position: Vector2, context: RenderContext) {
        render(value, position, context, SequenceRenderStyle())
    }

    fun render(
        value: NucleotideSequence,
        position: Vector2,
        context: RenderContext,
        style: SequenceRenderStyle,
    ) {
        val totalWidth = sequenceWidth(value)

        if (style.showBackbone) {
            context.drawLine(
                x = position.x - 8f,
                y = position.y + tileSize * 0.5f,
                width = totalWidth + 16f,
                height = 3f,
                color = BACKBONE_COLOR,
            )
        }

        var x = position.x
        value.forEach { nucleotide ->
            nucleotidePosition.x = x
            nucleotidePosition.y = position.y
            nucleotideRenderer.render(nucleotide, nucleotidePosition, context)
            x += tileSize + tileGap
        }

        if (style.showDirectionIndicator) {
            drawDirectionIndicator(value.direction, position.x, position.y, totalWidth, context)
        }
    }

    fun sequenceWidth(value: NucleotideSequence): Float {
        if (value.isEmpty()) {
            return 0f
        }

        return value.size * tileSize + (value.size - 1) * tileGap
    }

    private fun drawDirectionIndicator(
        direction: SequenceDirection,
        x: Float,
        y: Float,
        width: Float,
        context: RenderContext,
    ) {
        val centerY = y + tileSize * 0.5f
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

class DnaRenderer(
    val tileSize: Float = 34f,
    val tileGap: Float = 10f,
    val strandGap: Float = 14f,
) : Renderer<Dna> {
    private lateinit var sequenceRenderer: Renderer<NucleotideSequence>
    private val topStrandPosition = Vector2()
    private val bottomStrandPosition = Vector2()

    init {
        Renderers.register(Dna::class, this)
    }

    override fun init() {
        sequenceRenderer = Renderers.forType<NucleotideSequence>() ?: error("DnaRenderer requires a registered renderer for NucleotideSequences.")
    }

    override fun render(value: Dna, position: Vector2, context: RenderContext) {
        val topY = position.y
        val bottomY = topY - tileSize - strandGap

        topStrandPosition.x = position.x
        topStrandPosition.y = topY
        bottomStrandPosition.x = position.x
        bottomStrandPosition.y = bottomY

        sequenceRenderer.render(
            value.forward,
            topStrandPosition,
            context
        )
        sequenceRenderer.render(
            value.reverse,
            bottomStrandPosition,
            context
        )

        var connectorX = position.x + tileSize * 0.47f
        repeat(value.size) {
            context.drawLine(
                x = connectorX,
                y = bottomY + tileSize,
                width = tileSize * 0.08f,
                height = strandGap,
                color = PAIR_CONNECTOR_COLOR,
            )
            connectorX += tileSize + tileGap
        }
    }

    companion object {
        private val PAIR_CONNECTOR_COLOR = Color(0.85f, 0.85f, 0.9f, 1f)
    }
}
