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
    private val connectorDepth = tileSize * 0.18f
    private val connectorHalfHeight = tileSize * 0.2f

    init {
        Renderers.register(Nucleotide::class, this)
    }

    override fun init() {
        // Nothing to do here since this renderer has no dependencies on other renderers.
    }

    override fun render(value: Nucleotide, position: Vector2, context: RenderContext) {
        val profile = compatibilityProfile(value)
        val color = nucleotideColor(value)
        val bodyX = position.x + connectorDepth
        val bodyWidth = tileSize - connectorDepth
        val bodyCenterY = position.y + tileSize * 0.5f
        val rightEdgeX = bodyX + bodyWidth

        context.drawFilledRect(bodyX, position.y, bodyWidth, tileSize, color)

        when (profile.rightConnector) {
            ConnectorStyle.POINT -> context.drawFilledTriangle(
                rightEdgeX + connectorDepth,
                bodyCenterY,
                rightEdgeX,
                bodyCenterY + connectorHalfHeight,
                rightEdgeX,
                bodyCenterY - connectorHalfHeight,
                color,
            )

            ConnectorStyle.DOUBLE -> {
                val tipX = rightEdgeX + connectorDepth
                context.drawFilledTriangle(
                    tipX,
                    position.y + tileSize * 0.72f,
                    rightEdgeX,
                    position.y + tileSize * 0.92f,
                    rightEdgeX,
                    position.y + tileSize * 0.52f,
                    color,
                )
                context.drawFilledTriangle(
                    tipX,
                    position.y + tileSize * 0.28f,
                    rightEdgeX,
                    position.y + tileSize * 0.48f,
                    rightEdgeX,
                    position.y + tileSize * 0.08f,
                    color,
                )
            }
        }

        drawSocketHint(profile.leftSocket, bodyX, position.y, context)
        context.drawCenteredText(value.symbol.toString(), position.x + tileSize * 0.5f, position.y + tileSize * 0.5f)
    }

    private fun drawSocketHint(style: ConnectorStyle, x: Float, y: Float, context: RenderContext) {
        val hintColor = SOCKET_HINT_COLOR
        val centerY = y + tileSize * 0.5f
        when (style) {
            ConnectorStyle.POINT -> context.drawFilledTriangle(
                x + connectorDepth * 0.45f,
                centerY,
                x,
                centerY + connectorHalfHeight * 0.85f,
                x,
                centerY - connectorHalfHeight * 0.85f,
                hintColor,
            )

            ConnectorStyle.DOUBLE -> {
                context.drawFilledTriangle(
                    x + connectorDepth * 0.45f,
                    y + tileSize * 0.7f,
                    x,
                    y + tileSize * 0.9f,
                    x,
                    y + tileSize * 0.5f,
                    hintColor,
                )
                context.drawFilledTriangle(
                    x + connectorDepth * 0.45f,
                    y + tileSize * 0.3f,
                    x,
                    y + tileSize * 0.5f,
                    x,
                    y + tileSize * 0.1f,
                    hintColor,
                )
            }
        }
    }

    internal fun compatibilityProfile(nucleotide: Nucleotide): CompatibilityProfile = when (nucleotide) {
        Nucleotide.A -> CompatibilityProfile(leftSocket = ConnectorStyle.POINT, rightConnector = ConnectorStyle.POINT)
        Nucleotide.U -> CompatibilityProfile(leftSocket = ConnectorStyle.POINT, rightConnector = ConnectorStyle.POINT)
        Nucleotide.C -> CompatibilityProfile(leftSocket = ConnectorStyle.DOUBLE, rightConnector = ConnectorStyle.DOUBLE)
        Nucleotide.G -> CompatibilityProfile(leftSocket = ConnectorStyle.DOUBLE, rightConnector = ConnectorStyle.DOUBLE)
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
        private val SOCKET_HINT_COLOR = Color(0.08f, 0.1f, 0.16f, 0.35f)
    }
}

internal data class CompatibilityProfile(
    val leftSocket: ConnectorStyle,
    val rightConnector: ConnectorStyle,
)

internal enum class ConnectorStyle {
    POINT,
    DOUBLE,
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
                Vector2(position.x - 8f, position.y + tileSize * 0.5f),
                Vector2(position.x + totalWidth + 8f, position.y + tileSize * 0.5f),
                width = 3f,
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

        val connectorA = Vector2(position.x + tileSize * 0.47f, topY)
        val connectorB = Vector2(position.x + tileSize * 0.47f, bottomY + tileSize)
        repeat(value.size) {
            context.drawLine(
                a = connectorA,
                b = connectorB,
                width = tileSize * 0.08f,
                color = PAIR_CONNECTOR_COLOR,
            )
            connectorA.x += tileSize + tileGap
            connectorB.x += tileSize + tileGap
        }
    }

    companion object {
        private val PAIR_CONNECTOR_COLOR = Color(0.85f, 0.85f, 0.9f, 1f)
    }
}
