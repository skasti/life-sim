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

    private lateinit var nucleotideWrapper: SimWrapper
    private lateinit var sequenceWrapper: SimWrapper
    private lateinit var dnaWrapper: SimWrapper
    private var initialized = false

    val sequenceText: String = sequence.toString()
    val dnaForwardText: String = dna.forward.toString()
    val dnaReverseText: String = dna.reverse.toString()

    override fun init() {
        if (initialized) {
            return
        }

        nucleotideWrapper = SimWrapper(Vector2(), nucleotide)
        sequenceWrapper = SimWrapper(Vector2(), sequence)
        dnaWrapper = SimWrapper(Vector2(), dna)

        objectManager.add(nucleotideWrapper)
        objectManager.add(sequenceWrapper)
        objectManager.add(dnaWrapper)
        initialized = true
    }

    override fun render(
        context: RenderContext,
    ) {
        val leftMargin = context.viewportWidth * 0.07f

        val titleY = context.viewportHeight * 0.95f
        val nucleotideLabelY = context.viewportHeight * 0.82f
        val sequenceLabelY = context.viewportHeight * 0.66f
        val dnaLabelY = context.viewportHeight * 0.49f

        val moleculeX = leftMargin + context.viewportWidth * 0.22f
        val nucleotideTileY = nucleotideLabelY - 26f
        val sequenceTileY = sequenceLabelY - 26f
        val dnaTileY = dnaLabelY - 26f

        if (initialized) {
            nucleotideWrapper.position.set(moleculeX, nucleotideTileY)
            sequenceWrapper.position.set(moleculeX, sequenceTileY)
            dnaWrapper.position.set(moleculeX, dnaTileY)
        }

        context.drawText("Life-Sim Rendering Demo (static scene)", leftMargin, titleY, Color.WHITE)
        context.drawText("Nucleotide", leftMargin, nucleotideLabelY, Color.WHITE)
        context.drawText("Nucleotide sequence", leftMargin, sequenceLabelY, Color.WHITE)
        context.drawText("DNA duplex", leftMargin, dnaLabelY, Color.WHITE)

        super.render(context)
        context.finish()
    }

    companion object {
        fun sample(): DemoScene = DemoScene(
            nucleotide = Nucleotide.G,
            sequence = NucleotideSequence.of(">AUGCGAUCGUAA>"),
            dna = Dna.of(">ACGUACGUAC>"),
        )
    }
}
