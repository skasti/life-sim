package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.NucleotideSequence

class DnaRenderer(
    val baseSize: Float = RenderingVisualSpec.NUCLEOTIDE_BASE_SIZE,
    val tileGap: Float = 10f,
    val strandGap: Float = baseSize * 0.75f,
) : Renderer<Dna> {
    private lateinit var sequenceRenderer: Renderer<NucleotideSequence>
    private val topStrandTransform = Matrix3()
    private val bottomStrandTransform = Matrix3()

    init {
        Renderers.register(Dna::class, this)
    }

    override fun init() {
        sequenceRenderer = Renderers.forType<NucleotideSequence>()
            ?: error("DnaRenderer requires a registered renderer for NucleotideSequences.")
    }

    override fun render(value: Dna, transform: Matrix3, context: RenderContext) {
        val layout = layout(value, Vector2(0f, 0f)) ?: return

        layout.connectorSegments.forEach { connector ->
            context.drawLine(
                a = connector.a.mul(transform),
                b = connector.b.mul(transform),
                width = baseSize * 0.08f,
                color = PAIR_CONNECTOR_COLOR,
            )
        }

        topStrandTransform.set(transform).translate(layout.topStrandPosition)
        sequenceRenderer.render(value.forward, topStrandTransform, context)

        bottomStrandTransform.set(transform).translate(layout.bottomStrandPosition)
        sequenceRenderer.render(value.reverse, bottomStrandTransform, context)
        context.drawCircle(transform.getTranslation(Vector2()), baseSize * 0.1f, Color.BLUE)
    }

    internal fun layout(value: Dna, position: Vector2): DnaRenderLayout? {
        if (value.isEmpty()) {
            return null
        }

        val pivot = position.cpy()
        val strandBackboneOffset = (2f * baseSize + strandGap) * 0.5f
        val topStrandBasePosition = Vector2(position.x, position.y + strandBackboneOffset)
        val bottomStrandBasePosition = Vector2(position.x, position.y - strandBackboneOffset)

        val strandWidth = sequenceWidth(value.forward)
        val leftEdgeX = position.x - strandWidth * 0.5f

        val connectorSegments = buildList(value.size) {
            var connectorX = leftEdgeX + baseSize * 0.47f
            repeat(value.size) {
                val connectorTop = Vector2(connectorX, topStrandBasePosition.y - baseSize)
                val connectorBottom = Vector2(connectorX, bottomStrandBasePosition.y + baseSize)
                add(
                    ConnectorSegment(
                        a = connectorTop,
                        b = connectorBottom,
                    ),
                )
                connectorX += baseSize + tileGap
            }
        }

        return DnaRenderLayout(
            pivot = pivot,
            topStrandPosition = topStrandBasePosition,
            bottomStrandPosition = bottomStrandBasePosition,
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


