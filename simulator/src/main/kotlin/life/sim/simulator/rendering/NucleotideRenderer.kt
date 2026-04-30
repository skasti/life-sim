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
        geometryFor(value, position, orientation, nucleotideColor(value)).render(context)
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
        color: Color = nucleotideColor(nucleotide),
    ): Geometry {
        val profile = connectorProfile(nucleotide)
        val elements = mutableListOf<GeometryElement>()

        when (profile.family) {
            ConnectorFamily.ANGLED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    elements += Polygon.rect(position.x, position.y, baseSize, baseSize, color = color)
                    elements += triangleOnSide(position, orientation.pairingSide, color)
                } else {
                    elements += Polygon.rect(position.x, position.y, baseSize, baseSize, color = color)
                    elements += inverseTriangleOnSide(position, orientation.pairingSide, color)
                }
            }

            ConnectorFamily.ROUNDED -> {
                if (profile.polarity == ConnectorPolarity.PROTRUSION) {
                    elements += Polygon.rect(position.x, position.y, baseSize, baseSize, color = color)
                    elements += roundedOnSide(position, orientation.pairingSide, color)
                } else {
                    elements += roundedSocketPolygonOnSide(position, orientation.pairingSide, color)
                }
            }
        }

        return Geometry(elements)
    }

    private fun inverseTriangleOnSide(position: Vector2, side: PairingSide, color: Color): List<Polygon> {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.TOP -> listOf(
                Polygon.triangle(
                    Vector2(x, y + baseSize),
                    Vector2(x, y + baseSize + pairingBandSize),
                    Vector2(x + baseSize * 0.5f, y + baseSize),
                    color = color
                ),
                Polygon.triangle(
                    Vector2(x + baseSize * 0.5f, y + baseSize),
                    Vector2(x + baseSize, y + baseSize + pairingBandSize),
                    Vector2(x + baseSize, y + baseSize),
                    color = color
                ),
            )

            PairingSide.BOTTOM -> listOf(
                Polygon.triangle(
                    Vector2(x, y),
                    Vector2(x + baseSize * 0.5f, y),
                    Vector2(x, y - pairingBandSize),
                    color = color
                ),
                Polygon.triangle(
                    Vector2(x + baseSize * 0.5f, y),
                    Vector2(x + baseSize, y),
                    Vector2(x + baseSize, y - pairingBandSize),
                    color = color
                ),
            )

            PairingSide.LEFT -> listOf(
                Polygon.triangle(
                    Vector2(x, y + baseSize),
                    Vector2(x, y + baseSize * 0.5f),
                    Vector2(x - pairingBandSize, y + baseSize),
                    color = color
                ),
                Polygon.triangle(
                    Vector2(x, y + baseSize * 0.5f),
                    Vector2(x, y),
                    Vector2(x - pairingBandSize, y),
                    color = color
                ),
            )

            PairingSide.RIGHT -> listOf(
                Polygon.triangle(
                    Vector2(x + baseSize, y + baseSize),
                    Vector2(x + baseSize + pairingBandSize, y + baseSize),
                    Vector2(x + baseSize, y + baseSize * 0.5f),
                    color = color
                ),
                Polygon.triangle(
                    Vector2(x + baseSize, y + baseSize * 0.5f),
                    Vector2(x + baseSize + pairingBandSize, y),
                    Vector2(x + baseSize, y),
                    color = color
                ),
            )
        }
    }

    private fun triangleOnSide(position: Vector2, side: PairingSide, color: Color): Polygon {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.LEFT -> Polygon.triangle(
                Vector2(x, y),
                Vector2(x, y + baseSize),
                Vector2(x - pairingBandSize, y + baseSize * 0.5f),
                color = color
            )

            PairingSide.RIGHT -> Polygon.triangle(
                Vector2(x + baseSize, y),
                Vector2(x + baseSize + pairingBandSize, y + baseSize * 0.5f),
                Vector2(x + baseSize, y + baseSize),
                color = color
            )

            PairingSide.TOP -> Polygon.triangle(
                Vector2(x, y + baseSize),
                Vector2(x + baseSize * 0.5f, y + baseSize + pairingBandSize),
                Vector2(x + baseSize, y + baseSize),
                color = color
            )

            PairingSide.BOTTOM -> Polygon.triangle(
                Vector2(x, y),
                Vector2(x + baseSize, y),
                Vector2(x + baseSize * 0.5f, y - pairingBandSize),
                color = color
            )
        }
    }

    private fun roundedOnSide(position: Vector2, side: PairingSide, color: Color): Arc {
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
                color,
            )

            PairingSide.RIGHT -> Arc(
                x + baseSize,
                y + baseSize * 0.5f,
                capRadius,
                -90f,
                180f,
                color,
            )

            PairingSide.TOP -> Arc(
                x + baseSize * 0.5f,
                y + baseSize,
                capRadius,
                0f,
                180f,
                color,
            )

            PairingSide.BOTTOM -> Arc(
                x + baseSize * 0.5f,
                y,
                capRadius,
                -180f,
                180f,
                color,
            )
        }
    }

    private fun roundedSocketPolygonOnSide(position: Vector2, side: PairingSide, color: Color): Polygon {
        val x = position.x
        val y = position.y
        return when (side) {
            PairingSide.LEFT -> Polygon.of(
                Vector2(x + baseSize, y + baseSize),
                Vector2(x, y + baseSize),
                Vector2(x - pairingBandSize, y + baseSize),
                color = color,
            )
                .add(
                    arc(
                        start = Vector2(x - pairingBandSize, y + baseSize),
                        center = Vector2(x - pairingBandSize, y + baseSize * 0.5f),
                        end = Vector2(x - pairingBandSize, y),
                        segments = 10,
                        sweepDirection = ArcSweepDirection.CLOCKWISE,
                    ),
                )
                .add(
                    Vector2(x, y),
                    Vector2(x + baseSize, y),
                )
                .close()

            PairingSide.RIGHT -> Polygon.of(
                Vector2(x, y + baseSize),
                Vector2(x + baseSize, y + baseSize),
                Vector2(x + baseSize + pairingBandSize, y + baseSize),
                color = color,
            )
                .add(
                    arc(
                        start = Vector2(x + baseSize + pairingBandSize, y + baseSize),
                        center = Vector2(x + baseSize + pairingBandSize, y + baseSize * 0.5f),
                        end = Vector2(x + baseSize + pairingBandSize, y),
                        segments = 10,
                        sweepDirection = ArcSweepDirection.COUNTERCLOCKWISE,
                    ),
                )
                .add(
                    Vector2(x + baseSize, y),
                    Vector2(x, y),
                )
                .close()

            PairingSide.TOP -> Polygon.of(
                Vector2(x, y),
                Vector2(x + baseSize, y),
                Vector2(x + baseSize, y + baseSize),
                Vector2(x + baseSize, y + baseSize + pairingBandSize),
                color = color,
            )
                .add(
                    arc(
                        start =Vector2(x + baseSize, y + baseSize + pairingBandSize),
                        center = Vector2(x + baseSize * 0.5f, y + baseSize + pairingBandSize),
                        end = Vector2(x, y + baseSize + pairingBandSize),
                        segments = 10,
                        sweepDirection = ArcSweepDirection.CLOCKWISE,
                    ),
                )
                .add(Vector2(x, y + baseSize))
                .close()

            PairingSide.BOTTOM -> Polygon
                .of(
                    Vector2(x + baseSize, y + baseSize),
                    Vector2(x, y + baseSize),
                    Vector2(x, y),
                    Vector2(x, y - pairingBandSize),
                    color = color
                )
                .add(
                    arc(
                        start = Vector2(x, y - pairingBandSize),
                        center = Vector2(x + baseSize * 0.5f, y - pairingBandSize),
                        end = Vector2(x + baseSize, y - pairingBandSize),
                        segments = 10,
                        sweepDirection = ArcSweepDirection.CLOCKWISE,
                    )
                )
                .add(Vector2(x + baseSize, y))
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
