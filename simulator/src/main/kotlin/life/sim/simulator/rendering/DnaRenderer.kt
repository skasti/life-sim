package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.NucleotideSequence

class DnaRenderer(
    val baseSize: Float = 34f,
    val tileGap: Float = 10f,
    val strandGap: Float = baseSize * 0.75f,
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
        val bottomY = topY - baseSize - strandGap

        topStrandPosition.x = position.x
        topStrandPosition.y = topY
        bottomStrandPosition.x = position.x
        bottomStrandPosition.y = bottomY

        val connectorA = Vector2(position.x + baseSize * 0.47f, topY + baseSize)
        val connectorB = Vector2(position.x + baseSize * 0.47f, bottomY)
        repeat(value.size) {
            context.drawLine(
                a = connectorA,
                b = connectorB,
                width = baseSize * 0.08f,
                color = PAIR_CONNECTOR_COLOR,
            )
            connectorA.x += baseSize + tileGap
            connectorB.x += baseSize + tileGap
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

