package life.sim.simulator

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.RenderContext

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
            SimWrapper(Vector2(MOLECULE_X, NUCLEOTIDE_Y), nucleotide),
            SimWrapper(Vector2(MOLECULE_X, SEQUENCE_Y), sequence),
            SimWrapper(Vector2(MOLECULE_X, DNA_Y), dna),
        )
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
        context.finish()
    }

    companion object {
        private const val LABEL_X = 90f
        private const val MOLECULE_X = 370f

        private const val NUCLEOTIDE_LABEL_Y = 590f
        private const val SEQUENCE_LABEL_Y = 475f
        private const val DNA_LABEL_Y = 355f

        private const val NUCLEOTIDE_Y = NUCLEOTIDE_LABEL_Y - 26f
        private const val SEQUENCE_Y = SEQUENCE_LABEL_Y - 26f
        private const val DNA_Y = DNA_LABEL_Y - 26f

        fun sample(): DemoScene = DemoScene(
            nucleotide = Nucleotide.G,
            sequence = NucleotideSequence.of(">AUGCGAUCGUAA>"),
            dna = Dna.of(">ACGUACGUAC>"),
        )
    }
}
