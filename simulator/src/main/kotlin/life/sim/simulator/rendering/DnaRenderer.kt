package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.geometry.rotatePoint

class DnaRenderer(
    val baseSize: Float = RenderingVisualSpec.NUCLEOTIDE_BASE_SIZE,
    val tileGap: Float = 10f,
    val strandGap: Float = baseSize * 0.75f,
) : Renderer<Dna> {
    private lateinit var sequenceRenderer: Renderer<NucleotideSequence>

    init {
        Renderers.register(Dna::class, this)
    }

    override fun init() {
        sequenceRenderer = Renderers.forType<NucleotideSequence>()
            ?: error("DnaRenderer requires a registered renderer for NucleotideSequences.")
    }

    override fun render(value: Dna, position: Vector2, rotation: Float, context: RenderContext) {
        val layout = layout(value, position, rotation) ?: return

        layout.connectorSegments.forEach { connector ->
            context.drawLine(
                a = connector.a,
                b = connector.b,
                width = baseSize * 0.08f,
                color = PAIR_CONNECTOR_COLOR,
            )
        }

        sequenceRenderer.render(
            value.forward,
            layout.topStrandPosition,
            rotation,
            context,
        )
        sequenceRenderer.render(
            value.reverse,
            layout.bottomStrandPosition,
            rotation,
            context,
        )
    }

    internal fun layout(value: Dna, position: Vector2, rotation: Float = 0f): DnaRenderLayout? {
        if (value.isEmpty()) {
            return null
        }

        val pivot = position.cpy()
        val strandBackboneOffset = (2f * baseSize + strandGap) * 0.5f
        val topStrandBasePosition = Vector2(position.x, position.y + strandBackboneOffset)
        val bottomStrandBasePosition = Vector2(position.x, position.y - strandBackboneOffset)

        val rotatedTopStrandPosition = rotatePoint(topStrandBasePosition, pivot, rotation)
        val rotatedBottomStrandPosition = rotatePoint(bottomStrandBasePosition, pivot, rotation)

        val strandWidth = sequenceWidth(value.forward)
        val leftEdgeX = position.x - strandWidth * 0.5f

        val connectorSegments = buildList(value.size) {
            var connectorX = leftEdgeX + baseSize * 0.47f
            repeat(value.size) {
                val connectorTop = Vector2(connectorX, topStrandBasePosition.y - baseSize)
                val connectorBottom = Vector2(connectorX, bottomStrandBasePosition.y + baseSize)
                add(
                    ConnectorSegment(
                        a = rotatePoint(connectorTop, pivot, rotation),
                        b = rotatePoint(connectorBottom, pivot, rotation),
                    ),
                )
                connectorX += baseSize + tileGap
            }
        }

        return DnaRenderLayout(
            pivot = pivot,
            topStrandPosition = rotatedTopStrandPosition,
            bottomStrandPosition = rotatedBottomStrandPosition,
            connectorSegments = connectorSegments,
        )
    }

    private fun sequenceWidth(value: NucleotideSequence): Float {
        if (value.isEmpty()) {
            return 0f
        }

        return value.size * baseSize + (value.size - 1) * tileGap
    }

    companion object {
        private val PAIR_CONNECTOR_COLOR = Color(0.85f, 0.85f, 0.9f, 1f)
    }
}

internal data class DnaRenderLayout(
    val pivot: Vector2,
    val topStrandPosition: Vector2,
    val bottomStrandPosition: Vector2,
    val connectorSegments: List<ConnectorSegment>,
)

internal data class ConnectorSegment(
    val a: Vector2,
    val b: Vector2,
)


