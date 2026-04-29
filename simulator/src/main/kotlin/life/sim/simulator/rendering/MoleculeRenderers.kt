package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceDirection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class NucleotideRenderer(
    val baseSize: Float = 34f,
) : Renderer<Nucleotide> {
    private val pairingBandSize = baseSize * 0.5f

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
        geometry.arcs.forEach { arc ->
            context.drawArc(arc.x, arc.y, arc.radius, arc.startDegrees, arc.degrees, color)
        }
        geometry.triangles.forEach { triangle ->
            context.drawTriangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, triangle.x3, triangle.y3, color)
        }
        geometry.lines.forEach { line ->
            context.drawLine(line.a, line.b, line.width, color)
        }

        context.drawCenteredText(value.symbol.toString(), position.x + baseSize * 0.5f, position.y + baseSize * 0.5f)
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
        val filledTriangles = mutableListOf<Triangle>()
        val filledRects = mutableListOf<Rect>()
        val filledArcs = mutableListOf<Arc>()
        val arcs = mutableListOf<Arc>()
        val triangles = mutableListOf<Triangle>()
        val lines = mutableListOf<Line>()

        when (profile.family) {
            ConnectorFamily.ANGLED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    filledRects += Rect(position.x, position.y, baseSize, baseSize)
                    filledTriangles += triangleOnSide(position, orientation.pairingSide)
                } else {
                    filledRects += Rect(position.x, position.y, baseSize, baseSize)
                    filledTriangles += inverseTriangleOnSide(position, orientation.pairingSide)
                }
            }

            ConnectorFamily.ROUNDED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    filledRects += Rect(position.x, position.y, baseSize, baseSize)
                    filledArcs += roundedOnSide(position, orientation.pairingSide)
                } else {
                    filledRects += Rect(position.x, position.y, baseSize, baseSize)
                    arcs += roundedSocketOnSide(position, orientation.pairingSide)
                }
            }
        }

        return NucleotideGeometry(
            filledTriangles = filledTriangles,
            filledRects = filledRects,
            filledArcs = filledArcs,
            arcs = arcs,
            triangles = triangles,
            lines = lines,
        )
    }

    internal fun arcBounds(arc: Arc, includeCenter: Boolean = false): ShapeBounds {
        if (arc.radius <= 0f) {
            return ShapeBounds(arc.x, arc.x, arc.y, arc.y)
        }

        if (abs(arc.degrees) >= FULL_ROTATION_DEGREES) {
            return ShapeBounds(
                minX = arc.x - arc.radius,
                maxX = arc.x + arc.radius,
                minY = arc.y - arc.radius,
                maxY = arc.y + arc.radius,
            )
        }

        val xValues = mutableListOf<Float>()
        val yValues = mutableListOf<Float>()

        fun addPointAtAngle(angle: Float) {
            val radians = angle * PI / 180.0
            xValues += (arc.x + arc.radius * cos(radians)).toFloat()
            yValues += (arc.y + arc.radius * sin(radians)).toFloat()
        }

        addPointAtAngle(arc.startDegrees)
        addPointAtAngle(arc.startDegrees + arc.degrees)

        CARDINAL_ARC_ANGLES
            .filter { angle -> angleInSweep(angle, arc.startDegrees, arc.degrees) }
            .forEach(::addPointAtAngle)

        if (includeCenter) {
            xValues += arc.x
            yValues += arc.y
        }

        return ShapeBounds(
            minX = xValues.minOrNull() ?: arc.x,
            maxX = xValues.maxOrNull() ?: arc.x,
            minY = yValues.minOrNull() ?: arc.y,
            maxY = yValues.maxOrNull() ?: arc.y,
        )
    }

    private fun angleInSweep(angle: Float, startDegrees: Float, degrees: Float): Boolean {
        if (abs(degrees) >= FULL_ROTATION_DEGREES - ANGLE_EPSILON) {
            return true
        }

        if (abs(degrees) <= ANGLE_EPSILON) {
            return abs(normalizeAngle(angle) - normalizeAngle(startDegrees)) <= ANGLE_EPSILON
        }

        return if (degrees > 0f) {
            angleInPositiveSweep(angle, startDegrees, degrees)
        } else {
            angleInPositiveSweep(angle, startDegrees + degrees, -degrees)
        }
    }

    private fun angleInPositiveSweep(angle: Float, startDegrees: Float, degrees: Float): Boolean {
        val normalizedStart = normalizeAngle(startDegrees)
        val normalizedEnd = normalizeAngle(startDegrees + degrees)
        val normalizedAngle = normalizeAngle(angle)

        return if (normalizedStart <= normalizedEnd) {
            normalizedAngle >= normalizedStart - ANGLE_EPSILON && normalizedAngle <= normalizedEnd + ANGLE_EPSILON
        } else {
            normalizedAngle >= normalizedStart - ANGLE_EPSILON || normalizedAngle <= normalizedEnd + ANGLE_EPSILON
        }
    }

    private fun normalizeAngle(angle: Float): Float {
        val normalized = angle % FULL_ROTATION_DEGREES
        return if (normalized < 0f) {
            normalized + FULL_ROTATION_DEGREES
        } else {
            normalized
        }
    }

    private fun inverseTriangleOnSide(position: Vector2, side: PairingSide): List<Triangle> {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.TOP -> listOf(
                Triangle(
                    x,
                    y + baseSize,
                    x,
                    y + baseSize + pairingBandSize,
                    x + baseSize * 0.5f,
                    y + baseSize,
                ),
                Triangle(
                    x + baseSize * 0.5f,
                    y + baseSize,
                    x + baseSize,
                    y + baseSize + pairingBandSize,
                    x + baseSize,
                    y + baseSize,
                ),
            )
            PairingSide.BOTTOM -> listOf(
                Triangle(
                    x,
                    y,
                    x + baseSize * 0.5f,
                    y,
                    x,
                    y - pairingBandSize,
                ),
                Triangle(
                    x + baseSize * 0.5f,
                    y,
                    x + baseSize,
                    y,
                    x + baseSize,
                    y - pairingBandSize,
                ),
            )
            PairingSide.LEFT -> listOf(
                Triangle(
                    x,
                    y + baseSize,
                    x,
                    y + baseSize * 0.5f,
                    x - pairingBandSize,
                    y + baseSize,
                ),
                Triangle(
                    x,
                    y + baseSize * 0.5f,
                    x,
                    y,
                    x - pairingBandSize,
                    y,
                ),
            )
            PairingSide.RIGHT -> listOf(
                Triangle(
                    x + baseSize,
                    y + baseSize,
                    x + baseSize + pairingBandSize,
                    y + baseSize,
                    x + baseSize,
                    y + baseSize * 0.5f,
                ),
                Triangle(
                    x + baseSize,
                    y + baseSize * 0.5f,
                    x + baseSize + pairingBandSize,
                    y,
                    x + baseSize,
                    y,
                ),
            )
        }
    }

    private fun triangleOnSide(position: Vector2, side: PairingSide): Triangle {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.LEFT -> Triangle(
                x,
                y,
                x,
                y + baseSize,
                x - pairingBandSize,
                y + baseSize * 0.5f,
            )
            PairingSide.RIGHT -> Triangle(
                x + baseSize,
                y,
                x + baseSize + pairingBandSize,
                y + baseSize * 0.5f,
                x + baseSize,
                y + baseSize,
            )
            PairingSide.TOP -> Triangle(
                x,
                y + baseSize,
                x + baseSize * 0.5f,
                y + baseSize + pairingBandSize,
                x + baseSize,
                y + baseSize,
            )
            PairingSide.BOTTOM -> Triangle(
                x,
                y,
                x + baseSize,
                y,
                x + baseSize * 0.5f,
                y - pairingBandSize,
            )
        }
    }

    private fun roundedOnSide(position: Vector2, side: PairingSide): Arc {
        val x = position.x
        val y = position.y
        val capRadius = baseSize * 0.5f
        return when (side) {
            PairingSide.LEFT -> Arc(
                    x,
                    y + baseSize * 0.5f,
                    capRadius,
                    90f,
                    180f,
                )
            PairingSide.RIGHT -> Arc(
                    x + baseSize,
                    y + baseSize * 0.5f,
                    capRadius,
                    -90f,
                    180f,
                )
            PairingSide.TOP -> Arc(
                    x + baseSize * 0.5f,
                    y + baseSize,
                    capRadius,
                    0f,
                    180f,
                )
            PairingSide.BOTTOM -> Arc(
                    x + baseSize * 0.5f,
                    y,
                    capRadius,
                    -180f,
                    180f,
                )
        }
    }

    private fun roundedSocketOnSide(position: Vector2, side: PairingSide): Arc {
        val x = position.x
        val y = position.y
        val capRadius = baseSize * 0.5f
        return when (side) {
            PairingSide.LEFT -> Arc(
                x - capRadius,
                y + baseSize * 0.5f,
                capRadius,
                -90f,
                180f,
            )
            PairingSide.RIGHT -> Arc(
                x + baseSize + capRadius,
                y + baseSize * 0.5f,
                capRadius,
                90f,
                180f,
            )
            PairingSide.TOP -> Arc(
                x + baseSize * 0.5f,
                y + baseSize + capRadius,
                capRadius,
                -180f,
                180f,
            )
            PairingSide.BOTTOM -> Arc(
                x + baseSize * 0.5f,
                y - capRadius,
                capRadius,
                0f,
                180f,
            )
        }
    }

    companion object {
        private const val FULL_ROTATION_DEGREES = 360f
        private const val ANGLE_EPSILON = 0.0001f
        private val CARDINAL_ARC_ANGLES = listOf(0f, 90f, 180f, 270f)

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
    val pairingSide: PairingSide = PairingSide.LEFT,
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
    val arcs: List<Arc>,
    val triangles: List<Triangle>,
    val lines: List<Line>,
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

internal data class Line(
    val a: Vector2,
    val b: Vector2,
    val width: Float,
)

internal data class Arc(
    val x: Float,
    val y: Float,
    val radius: Float,
    val startDegrees: Float,
    val degrees: Float,
)

internal data class ShapeBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
) {
    fun isWithin(minX: Float, maxX: Float, minY: Float, maxY: Float): Boolean =
        this.minX >= minX &&
            this.maxX <= maxX &&
            this.minY >= minY &&
            this.maxY <= maxY
}

data class SequenceRenderStyle(
    val showBackbone: Boolean = true,
    val showDirectionIndicator: Boolean = true,
    val pairingSide: PairingSide = PairingSide.TOP,
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
            val backBoneY = when (style.pairingSide) {
                PairingSide.TOP -> position.y
                PairingSide.BOTTOM -> position.y + tileSize
                else -> position.y + tileSize * 0.5f
            }
            context.drawLine(
                Vector2(position.x - 8f, backBoneY),
                Vector2(position.x + totalWidth + 8f, backBoneY),
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
    val strandGap: Float = tileSize * 0.75f,
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

        val connectorA = Vector2(position.x + tileSize * 0.47f, topY + tileSize)
        val connectorB = Vector2(position.x + tileSize * 0.47f, bottomY)
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
    }

    companion object {
        private val PAIR_CONNECTOR_COLOR = Color(0.85f, 0.85f, 0.9f, 1f)
    }
}
