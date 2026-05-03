package life.sim.simulator

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.RenderingVisualSpec

/**
 * Static, hand-authored rendering inputs used as a visual baseline for simulator work.
 */
data class DemoScene(
    val nucleotide: Nucleotide,
    val sequence: NucleotideSequence,
    val dna: Dna,
) : Scene {
    override val objectManager = ObjectManager()
    private var initialized = false

    val sequenceText: String = sequence.toString()
    val dnaForwardText: String = dna.forward.toString()
    val dnaReverseText: String = dna.reverse.toString()

    override fun init() {
        if (initialized) {
            return
        }

        objectManager.add(
            SimWrapper(Vector2(NUCLEOTIDE_X, NUCLEOTIDE_Y), 0f, nucleotide),
            SimWrapper(Vector2(SEQUENCE_X, SEQUENCE_Y), 0f, sequence),
            SimWrapper(Vector2(DNA_X, DNA_Y), 0f, dna),
        )

        objectManager.processQueues()
        initialized = true
    }

    override fun render(
        context: RenderContext,
    ) {
        val titleY = context.viewportHeight * 0.95f

        context.drawText("Life-Sim Rendering Demo (static scene)", LABEL_X, titleY, Color.WHITE)
        context.drawText("Nucleotide", LABEL_X, NUCLEOTIDE_LABEL_Y, Color.WHITE)
        context.drawText("Nucleotide sequence", LABEL_X, SEQUENCE_LABEL_Y, Color.WHITE)
        context.drawText("DNA duplex", LABEL_X, DNA_LABEL_Y, Color.WHITE)

        super.render(context)
    }

    companion object {
        private const val LABEL_X = 90f
        private const val LEGACY_LEFT_X = 370f

        private const val BASE_SIZE = RenderingVisualSpec.NUCLEOTIDE_BASE_SIZE
        private const val TILE_GAP = 10f
        private const val STRAND_GAP = BASE_SIZE * 0.75f
        private const val SEQUENCE_SAMPLE_SIZE = 12f
        private const val DNA_SAMPLE_SIZE = 10f

        private const val SEQUENCE_WIDTH = SEQUENCE_SAMPLE_SIZE * BASE_SIZE + (SEQUENCE_SAMPLE_SIZE - 1f) * TILE_GAP
        private const val DNA_WIDTH = DNA_SAMPLE_SIZE * BASE_SIZE + (DNA_SAMPLE_SIZE - 1f) * TILE_GAP

        // Preserve previous demo visual placement after moving to center-based renderer coordinates.
        private const val NUCLEOTIDE_X = LEGACY_LEFT_X + BASE_SIZE * 0.5f
        private const val SEQUENCE_X = LEGACY_LEFT_X + SEQUENCE_WIDTH * 0.5f
        private const val DNA_X = LEGACY_LEFT_X + DNA_WIDTH * 0.5f

        private const val NUCLEOTIDE_LABEL_Y = 590f
        private const val SEQUENCE_LABEL_Y = 475f
        private const val DNA_LABEL_Y = 355f

        private const val NUCLEOTIDE_Y = NUCLEOTIDE_LABEL_Y - 26f + BASE_SIZE * 0.5f
        private const val SEQUENCE_Y = SEQUENCE_LABEL_Y - 26f
        private const val DNA_Y = DNA_LABEL_Y - 26f - (BASE_SIZE + STRAND_GAP) * 0.5f

        fun sample(): DemoScene = DemoScene(
            nucleotide = Nucleotide.A,
            sequence = NucleotideSequence.of(">AUGCGAUCGUAA>"),
            dna = Dna.of(">ACGUACGUAC>"),
        )
    }
}
