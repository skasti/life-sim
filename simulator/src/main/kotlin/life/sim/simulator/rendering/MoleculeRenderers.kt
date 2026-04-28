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
    private val pairingBandSize = tileSize * 0.28f
    private val angledInsetSize = pairingBandSize * 0.7f
    private val roundedInsetSize = pairingBandSize * 0.58f
    private val socketFlankRatio = 0.28f

    init {
        Renderers.register(Nucleotide::class, this)
    }

    override fun init() {
        // Nothing to do here since this renderer has no dependencies on other renderers.
    }

    override fun render(value: Nucleotide, position: Vector2, context: RenderContext) {
        render(value, position, context, NucleotideOrientation())
    }

    fun render(value: Nucleotide, position: Vector2, context: RenderContext, orientation: NucleotideOrientation) {
        val color = nucleotideColor(value)
        val geometry = geometryFor(value, position, orientation)
        geometry.filledRects.forEach { rect ->
            context.drawFilledRect(rect.x, rect.y, rect.width, rect.height, color)
        }
        geometry.filledTriangles.forEach { triangle ->
            context.drawFilledTriangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, triangle.x3, triangle.y3, color)
        }
        geometry.filledArcs.forEach { arc ->
            context.drawFilledArc(arc.x, arc.y, arc.radius, arc.startDegrees, arc.degrees, color)
        }

        context.drawCenteredText(value.symbol.toString(), position.x + tileSize * 0.5f, position.y + tileSize * 0.5f)
    }

    internal fun connectorProfile(nucleotide: Nucleotide): ConnectorProfile = when (nucleotide) {
        Nucleotide.A -> ANGLED_PROTRUSION
        Nucleotide.U -> ANGLED_INDENTATION
        Nucleotide.C -> ROUNDED_PROTRUSION
        Nucleotide.G -> ROUNDED_INDENTATION
    }

    private fun nucleotideColor(nucleotide: Nucleotide): Color = when (nucleotide) {
        Nucleotide.A -> ADENINE_COLOR
        Nucleotide.U -> URACIL_COLOR
        Nucleotide.C -> CYTOSINE_COLOR
        Nucleotide.G -> GUANINE_COLOR
    }

    internal fun geometryFor(
        nucleotide: Nucleotide,
        position: Vector2,
        orientation: NucleotideOrientation,
    ): NucleotideGeometry {
        val profile = connectorProfile(nucleotide)
        val inset = when (profile.family) {
            ConnectorFamily.ANGLED -> angledInsetSize
            ConnectorFamily.ROUNDED -> roundedInsetSize
        }
        val filledTriangles = mutableListOf<Triangle>()
        val filledRects = mutableListOf<Rect>()
        val filledArcs = mutableListOf<Arc>()

        when (profile.family) {
            ConnectorFamily.ANGLED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    filledRects += bodyWithoutPairingBand(position, orientation.pairingSide)
                    filledTriangles += triangleOnSide(position, orientation.pairingSide, inset)
                } else {
                    filledRects += bodyWithoutPairingBand(position, orientation.pairingSide)
                    filledRects += socketFlankRects(position, orientation.pairingSide)
                    filledTriangles += angledSocketFlankTriangles(position, orientation.pairingSide)
                }
            }

            ConnectorFamily.ROUNDED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    val roundedShape = roundedOnSide(position, orientation.pairingSide, inset)
                    filledRects += bodyWithoutPairingBand(position, orientation.pairingSide)
                    filledRects += roundedShape.rect
                    filledArcs += roundedShape.cap
                } else {
                    filledRects += bodyWithoutPairingBand(position, orientation.pairingSide)
                    filledRects += socketFlankRects(position, orientation.pairingSide)
                }
            }
        }

        return NucleotideGeometry(
            filledTriangles = filledTriangles,
            filledRects = filledRects,
            filledArcs = filledArcs,
        )
    }

    internal fun boundsWithinTile(geometry: NucleotideGeometry, position: Vector2): Boolean {
        val minX = position.x
        val maxX = position.x + tileSize
        val minY = position.y
        val maxY = position.y + tileSize

        val rectsInBounds = geometry.filledRects.all { rect ->
            rect.x >= minX &&
                rect.y >= minY &&
                rect.x + rect.width <= maxX &&
                rect.y + rect.height <= maxY
        }

        val trianglesInBounds = geometry.filledTriangles.all { triangle ->
            val xs = listOf(triangle.x1, triangle.x2, triangle.x3)
            val ys = listOf(triangle.y1, triangle.y2, triangle.y3)
            xs.all { it in minX..maxX } && ys.all { it in minY..maxY }
        }

        val arcsInBounds = geometry.filledArcs.all { arc ->
            arc.x - arc.radius >= minX &&
                arc.x + arc.radius <= maxX &&
                arc.y - arc.radius >= minY &&
                arc.y + arc.radius <= maxY
        }

        return rectsInBounds && trianglesInBounds && arcsInBounds
    }

    private fun bodyWithoutPairingBand(position: Vector2, side: PairingSide): Rect = when (side) {
        PairingSide.LEFT -> Rect(position.x + pairingBandSize, position.y, tileSize - pairingBandSize, tileSize)
        PairingSide.RIGHT -> Rect(position.x, position.y, tileSize - pairingBandSize, tileSize)
        PairingSide.TOP -> Rect(position.x, position.y, tileSize, tileSize - pairingBandSize)
        PairingSide.BOTTOM -> Rect(position.x, position.y + pairingBandSize, tileSize, tileSize - pairingBandSize)
    }

    private fun socketFlankRects(position: Vector2, side: PairingSide): List<Rect> {
        val x = position.x
        val y = position.y
        val flank = tileSize * socketFlankRatio
        return when (side) {
            PairingSide.TOP -> listOf(
                Rect(x, y + tileSize - pairingBandSize, flank, pairingBandSize),
                Rect(x + tileSize - flank, y + tileSize - pairingBandSize, flank, pairingBandSize),
            )
            PairingSide.BOTTOM -> listOf(
                Rect(x, y, flank, pairingBandSize),
                Rect(x + tileSize - flank, y, flank, pairingBandSize),
            )
            PairingSide.LEFT -> listOf(
                Rect(x, y, pairingBandSize, flank),
                Rect(x, y + tileSize - flank, pairingBandSize, flank),
            )
            PairingSide.RIGHT -> listOf(
                Rect(x + tileSize - pairingBandSize, y, pairingBandSize, flank),
                Rect(x + tileSize - pairingBandSize, y + tileSize - flank, pairingBandSize, flank),
            )
        }
    }

    private fun angledSocketFlankTriangles(position: Vector2, side: PairingSide): List<Triangle> {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.TOP -> listOf(
                Triangle(
                    x + tileSize * 0.22f,
                    y + tileSize - pairingBandSize,
                    x + tileSize * 0.5f,
                    y + tileSize - pairingBandSize * 0.35f,
                    x + tileSize * 0.22f,
                    y + tileSize,
                ),
                Triangle(
                    x + tileSize * 0.78f,
                    y + tileSize - pairingBandSize,
                    x + tileSize * 0.5f,
                    y + tileSize - pairingBandSize * 0.35f,
                    x + tileSize * 0.78f,
                    y + tileSize,
                ),
            )
            PairingSide.BOTTOM -> listOf(
                Triangle(
                    x + tileSize * 0.22f,
                    y + pairingBandSize,
                    x + tileSize * 0.5f,
                    y + pairingBandSize * 0.35f,
                    x + tileSize * 0.22f,
                    y,
                ),
                Triangle(
                    x + tileSize * 0.78f,
                    y + pairingBandSize,
                    x + tileSize * 0.5f,
                    y + pairingBandSize * 0.35f,
                    x + tileSize * 0.78f,
                    y,
                ),
            )
            PairingSide.LEFT -> listOf(
                Triangle(
                    x + pairingBandSize,
                    y + tileSize * 0.22f,
                    x + pairingBandSize * 0.35f,
                    y + tileSize * 0.5f,
                    x,
                    y + tileSize * 0.22f,
                ),
                Triangle(
                    x + pairingBandSize,
                    y + tileSize * 0.78f,
                    x + pairingBandSize * 0.35f,
                    y + tileSize * 0.5f,
                    x,
                    y + tileSize * 0.78f,
                ),
            )
            PairingSide.RIGHT -> listOf(
                Triangle(
                    x + tileSize - pairingBandSize,
                    y + tileSize * 0.22f,
                    x + tileSize - pairingBandSize * 0.35f,
                    y + tileSize * 0.5f,
                    x + tileSize,
                    y + tileSize * 0.22f,
                ),
                Triangle(
                    x + tileSize - pairingBandSize,
                    y + tileSize * 0.78f,
                    x + tileSize - pairingBandSize * 0.35f,
                    y + tileSize * 0.5f,
                    x + tileSize,
                    y + tileSize * 0.78f,
                ),
            )
        }
    }

    private fun triangleOnSide(position: Vector2, side: PairingSide, inset: Float): Triangle {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.LEFT -> Triangle(
                x + pairingBandSize - inset,
                y + tileSize * 0.5f,
                x + pairingBandSize,
                y + tileSize * 0.78f,
                x + pairingBandSize,
                y + tileSize * 0.22f,
            )
            PairingSide.RIGHT -> Triangle(
                x + tileSize - pairingBandSize + inset,
                y + tileSize * 0.5f,
                x + tileSize - pairingBandSize,
                y + tileSize * 0.78f,
                x + tileSize - pairingBandSize,
                y + tileSize * 0.22f,
            )
            PairingSide.TOP -> Triangle(
                x + tileSize * 0.5f,
                y + tileSize - pairingBandSize + inset,
                x + tileSize * 0.78f,
                y + tileSize - pairingBandSize,
                x + tileSize * 0.22f,
                y + tileSize - pairingBandSize,
            )
            PairingSide.BOTTOM -> Triangle(
                x + tileSize * 0.5f,
                y + pairingBandSize - inset,
                x + tileSize * 0.78f,
                y + pairingBandSize,
                x + tileSize * 0.22f,
                y + pairingBandSize,
            )
        }
    }

    private fun roundedOnSide(position: Vector2, side: PairingSide, inset: Float): RoundedShape {
        val x = position.x
        val y = position.y
        val capRadius = inset * 0.4f
        val capCenterOffset = inset * 0.5f
        return when (side) {
            PairingSide.LEFT -> RoundedShape(
                rect = Rect(x + pairingBandSize - inset * 0.9f, y + tileSize * 0.33f, inset * 0.6f, tileSize * 0.34f),
                cap = Arc(
                    x + pairingBandSize - capCenterOffset,
                    y + tileSize * 0.5f,
                    capRadius,
                    90f,
                    180f,
                ),
            )
            PairingSide.RIGHT -> RoundedShape(
                rect = Rect(x + tileSize - pairingBandSize, y + tileSize * 0.33f, inset * 0.6f, tileSize * 0.34f),
                cap = Arc(
                    x + tileSize - pairingBandSize + capCenterOffset,
                    y + tileSize * 0.5f,
                    capRadius,
                    -90f,
                    180f,
                ),
            )
            PairingSide.TOP -> RoundedShape(
                rect = Rect(x + tileSize * 0.33f, y + tileSize - pairingBandSize, tileSize * 0.34f, inset * 0.6f),
                cap = Arc(
                    x + tileSize * 0.5f,
                    y + tileSize - pairingBandSize + capCenterOffset,
                    capRadius,
                    0f,
                    180f,
                ),
            )
            PairingSide.BOTTOM -> RoundedShape(
                rect = Rect(x + tileSize * 0.33f, y + pairingBandSize - inset * 0.6f, tileSize * 0.34f, inset * 0.6f),
                cap = Arc(
                    x + tileSize * 0.5f,
                    y + pairingBandSize - capCenterOffset,
                    capRadius,
                    180f,
                    180f,
                ),
            )
        }
    }

    companion object {
        private val ADENINE_COLOR = Color(0.95f, 0.65f, 0.3f, 1f)
        private val URACIL_COLOR = Color(0.36f, 0.78f, 0.95f, 1f)
        private val CYTOSINE_COLOR = Color(0.55f, 0.88f, 0.42f, 1f)
        private val GUANINE_COLOR = Color(0.9f, 0.42f, 0.76f, 1f)

        private val ANGLED_PROTRUSION = ConnectorProfile(ConnectorFamily.ANGLED, ConnectorPolarity.PROTRUSION)
        private val ANGLED_INDENTATION = ConnectorProfile(ConnectorFamily.ANGLED, ConnectorPolarity.INDENTATION)
        private val ROUNDED_PROTRUSION = ConnectorProfile(ConnectorFamily.ROUNDED, ConnectorPolarity.PROTRUSION)
        private val ROUNDED_INDENTATION = ConnectorProfile(ConnectorFamily.ROUNDED, ConnectorPolarity.INDENTATION)
    }
}

internal data class ConnectorProfile(
    val family: ConnectorFamily,
    val polarity: ConnectorPolarity,
)

internal enum class ConnectorFamily {
    ANGLED,
    ROUNDED,
}

internal enum class ConnectorPolarity {
    PROTRUSION,
    INDENTATION,
}

data class NucleotideOrientation(
    val pairingSide: PairingSide = PairingSide.RIGHT,
)

enum class PairingSide {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
}

internal data class NucleotideGeometry(
    val filledTriangles: List<Triangle>,
    val filledRects: List<Rect>,
    val filledArcs: List<Arc>,
)

internal data class Rect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

internal data class Triangle(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val x3: Float,
    val y3: Float,
)

private data class RoundedShape(
    val rect: Rect,
    val cap: Arc,
)

internal data class Arc(
    val x: Float,
    val y: Float,
    val radius: Float,
    val startDegrees: Float,
    val degrees: Float,
)

data class SequenceRenderStyle(
    val showBackbone: Boolean = true,
    val showDirectionIndicator: Boolean = true,
    val pairingSide: PairingSide = PairingSide.RIGHT,
)

class NucleotideSequenceRenderer(
    val tileGap: Float = 10f,
    val tileSize: Float = 34f,
) : Renderer<NucleotideSequence> {
    private lateinit var nucleotideRenderer: NucleotideRenderer
    private val nucleotidePosition = Vector2()

    init {
        Renderers.register(NucleotideSequence::class, this)
    }

    override fun init() {
        nucleotideRenderer = Renderers.forType<Nucleotide>() as? NucleotideRenderer
            ?: error("NucleotideSequenceRenderer requires a registered NucleotideRenderer for Nucleotide.")
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
            nucleotideRenderer.render(
                nucleotide,
                nucleotidePosition,
                context,
                NucleotideOrientation(pairingSide = style.pairingSide),
            )
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
    private lateinit var sequenceRenderer: NucleotideSequenceRenderer
    private val topStrandPosition = Vector2()
    private val bottomStrandPosition = Vector2()

    init {
        Renderers.register(Dna::class, this)
    }

    override fun init() {
        sequenceRenderer = Renderers.forType<NucleotideSequence>() as? NucleotideSequenceRenderer
            ?: error("DnaRenderer requires a registered NucleotideSequenceRenderer for NucleotideSequences.")
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
            context,
            SequenceRenderStyle(pairingSide = PairingSide.BOTTOM),
        )
        sequenceRenderer.render(
            value.reverse,
            bottomStrandPosition,
            context,
            SequenceRenderStyle(pairingSide = PairingSide.TOP),
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
