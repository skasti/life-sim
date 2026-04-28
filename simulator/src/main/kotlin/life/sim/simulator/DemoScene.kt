package life.sim.simulator

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence
import life.sim.simulator.rendering.RenderContext
import life.sim.simulator.rendering.Renderers

/**
 * Static, hand-authored rendering inputs used as a visual baseline for simulator work.
 */
data class DemoScene(
    val nucleotide: Nucleotide,
    val sequence: NucleotideSequence,
    val dna: Dna,
) : Scene {
    val sequenceText: String = sequence.toString()
    val dnaForwardText: String = dna.forward.toString()
    val dnaReverseText: String = dna.reverse.toString()

    override fun update(deltaSeconds: Float) = Unit

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

        context.drawText("Life-Sim Rendering Demo (static scene)", leftMargin, titleY, Color.WHITE)

        context.drawText("Nucleotide", leftMargin, nucleotideLabelY, Color.WHITE)
        Renderers.render(nucleotide, Vector2(moleculeX, nucleotideTileY), context)

        context.drawText("Nucleotide sequence", leftMargin, sequenceLabelY, Color.WHITE)
        Renderers.render(sequence, Vector2(moleculeX, sequenceTileY), context)

        context.drawText("DNA duplex", leftMargin, dnaLabelY, Color.WHITE)
        Renderers.render(dna, Vector2(moleculeX, dnaTileY), context)

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
