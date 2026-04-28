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
    override fun render(value: Nucleotide, position: Vector2, context: RenderContext) {
        context.drawFilledRect(position.x, position.y, tileSize, tileSize, nucleotideColor(value))
        context.drawCenteredText(value.symbol.toString(), position.x + tileSize * 0.5f, position.y + tileSize * 0.5f)
    }

    private fun nucleotideColor(nucleotide: Nucleotide): Color = when (nucleotide) {
        Nucleotide.A -> Color(0.95f, 0.65f, 0.3f, 1f)
        Nucleotide.U -> Color(0.36f, 0.78f, 0.95f, 1f)
        Nucleotide.C -> Color(0.55f, 0.88f, 0.42f, 1f)
        Nucleotide.G -> Color(0.9f, 0.42f, 0.76f, 1f)
    }
}

data class SequenceRenderStyle(
    val showBackbone: Boolean = true,
    val showDirectionIndicator: Boolean = true,
)

class NucleotideSequenceRenderer(
    val nucleotideRenderer: NucleotideRenderer,
    val tileGap: Float = 10f,
) : Renderer<NucleotideSequence> {
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
                y = position.y + nucleotideRenderer.tileSize * 0.5f,
                width = totalWidth + 16f,
                height = 3f,
                color = Color(0.2f, 0.6f, 0.95f, 1f),
            )
        }

        var x = position.x
        value.forEach { nucleotide ->
            nucleotideRenderer.render(nucleotide, Vector2(x, position.y), context)
            x += nucleotideRenderer.tileSize + tileGap
        }

        if (style.showDirectionIndicator) {
            drawDirectionIndicator(value.direction, position.x, position.y, totalWidth, context)
        }
    }

    fun sequenceWidth(value: NucleotideSequence): Float {
        if (value.isEmpty()) {
            return 0f
        }

        return value.size * nucleotideRenderer.tileSize + (value.size - 1) * tileGap
    }

    private fun drawDirectionIndicator(
        direction: SequenceDirection,
        x: Float,
        y: Float,
        width: Float,
        context: RenderContext,
    ) {
        val centerY = y + nucleotideRenderer.tileSize * 0.5f
        val arrowHeight = 8f
        val arrowWidth = 12f
        val color = Color(0.9f, 0.92f, 1f, 1f)

        if (direction == SequenceDirection.FORWARD) {
            val arrowX = x + width + 12f
            context.drawFilledTriangle(
                arrowX,
                centerY,
                arrowX - arrowWidth,
                centerY + arrowHeight,
                arrowX - arrowWidth,
                centerY - arrowHeight,
                color,
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
            color,
        )
    }
}

class DnaRenderer(
    private val sequenceRenderer: NucleotideSequenceRenderer,
    val strandGap: Float = 14f,
) : Renderer<Dna> {
    override fun render(value: Dna, position: Vector2, context: RenderContext) {
        val topY = position.y
        val bottomY = topY - sequenceRenderer.nucleotideRenderer.tileSize - strandGap

        sequenceRenderer.render(
            value.forward,
            Vector2(position.x, topY),
            context,
            style = SequenceRenderStyle(showBackbone = true, showDirectionIndicator = true),
        )
        sequenceRenderer.render(
            value.reverse,
            Vector2(position.x, bottomY),
            context,
            style = SequenceRenderStyle(showBackbone = true, showDirectionIndicator = true),
        )

        val pairConnectorColor = Color(0.85f, 0.85f, 0.9f, 1f)
        var connectorX = position.x + sequenceRenderer.nucleotideRenderer.tileSize * 0.47f
        repeat(value.size) {
            context.drawLine(
                x = connectorX,
                y = bottomY + sequenceRenderer.nucleotideRenderer.tileSize,
                width = sequenceRenderer.nucleotideRenderer.tileSize * 0.08f,
                height = strandGap,
                color = pairConnectorColor,
            )
            connectorX += sequenceRenderer.nucleotideRenderer.tileSize + sequenceRenderer.tileGap
        }
    }
}
