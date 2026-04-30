package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.primitives.Nucleotide
import life.sim.simulator.rendering.geometry.*

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
        geometryFor(value, position, orientation).render(context, nucleotideColor(value))
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
    ): Geometry {
        val profile = connectorProfile(nucleotide)
        val filledTriangles = mutableListOf<Triangle>()
        val filledRects = mutableListOf<Rect>()
        val filledArcs = mutableListOf<Arc>()
        val arcs = mutableListOf<Arc>()
        val triangles = mutableListOf<Triangle>()
        val lines = mutableListOf<Line>()
        val polygons = mutableListOf<Polygon>()

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
                    polygons += roundedSocketPolygonOnSide(position, orientation.pairingSide)
                }
            }
        }

        return Geometry(
            filledTriangles = filledTriangles,
            filledRects = filledRects,
            filledArcs = filledArcs,
            arcs = arcs,
            triangles = triangles,
            lines = lines,
            polygons = polygons,
        )
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
                baseSize * 0.08f,
            )
            PairingSide.RIGHT -> Arc(
                x + baseSize,
                y + baseSize * 0.5f,
                capRadius,
                -90f,
                180f,
                baseSize * 0.08f,
            )
            PairingSide.TOP -> Arc(
                x + baseSize * 0.5f,
                y + baseSize,
                capRadius,
                0f,
                180f,
                baseSize * 0.08f,
            )
            PairingSide.BOTTOM -> Arc(
                x + baseSize * 0.5f,
                y,
                capRadius,
                -180f,
                180f,
                baseSize * 0.08f,
            )
        }
    }

    private fun roundedSocketPolygonOnSide(position: Vector2, side: PairingSide): Polygon {
        val x = position.x
        val y = position.y
        val capRadius = baseSize * 0.35f
        return when (side) {
            PairingSide.LEFT -> polygon.of(Vector2(x, y + baseSize), Vector2(x, y))
                .add(arc(Vector2(x, y), Vector2(x - capRadius, y + baseSize * 0.5f), Vector2(x, y + baseSize), segments = 10))
                .close()
            PairingSide.RIGHT -> polygon.of(Vector2(x + baseSize, y), Vector2(x + baseSize, y + baseSize))
                .add(arc(Vector2(x + baseSize, y + baseSize), Vector2(x + baseSize + capRadius, y + baseSize * 0.5f), Vector2(x + baseSize, y), segments = 10))
                .close()
            PairingSide.TOP -> polygon.of(Vector2(x, y + baseSize), Vector2(x + baseSize, y + baseSize))
                .add(arc(Vector2(x + baseSize, y + baseSize), Vector2(x + baseSize * 0.5f, y + baseSize), Vector2(x, y + baseSize), segments = 10))
                .close()
            PairingSide.BOTTOM -> polygon.of(Vector2(x + baseSize, y), Vector2(x, y))
                .add(arc(Vector2(x, y), Vector2(x + baseSize * 0.5f, y), Vector2(x + baseSize, y), segments = 10))
                .close()
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

