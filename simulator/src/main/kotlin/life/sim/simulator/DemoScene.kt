package life.sim.simulator

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence

/**
 * Static, hand-authored rendering inputs used as a visual baseline for simulator work.
 *
 * Keeping this scene deterministic makes it easy to manually verify renderer changes
 * before dynamic simulation behavior is introduced.
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
        batch: SpriteBatch,
        font: BitmapFont,
        shapeRenderer: ShapeRenderer,
        viewportWidth: Float,
        viewportHeight: Float,
    ) {
        val leftMargin = viewportWidth * 0.07f
        val contentWidth = viewportWidth * 0.86f

        val titleY = viewportHeight * 0.95f
        val nucleotideY = viewportHeight * 0.82f
        val sequenceY = viewportHeight * 0.66f
        val dnaTopY = viewportHeight * 0.49f
        val dnaBottomY = viewportHeight * 0.41f

        val tileSize = 34f
        val tileGap = 10f
        val sequenceTileX = leftMargin + 210f

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        drawNucleotideTile(shapeRenderer, leftMargin + 210f, nucleotideY - tileSize + 8f, tileSize, nucleotide)
        drawSequenceTiles(shapeRenderer, sequenceTileX, sequenceY - tileSize + 8f, tileSize, tileGap)
        drawDnaDuplex(shapeRenderer, sequenceTileX, dnaTopY - tileSize + 8f, tileSize, tileGap, contentWidth)

        shapeRenderer.end()

        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, "Life-Sim Rendering Demo (static scene)", leftMargin, titleY)

        font.draw(batch, "Nucleotide", leftMargin, nucleotideY)
        font.draw(batch, nucleotide.symbol.toString(), leftMargin + 280f, nucleotideY)

        font.draw(batch, "Nucleotide sequence", leftMargin, sequenceY)
        font.draw(batch, sequenceText, leftMargin + 280f, sequenceY)

        font.draw(batch, "DNA duplex", leftMargin, dnaTopY)
        font.draw(batch, dnaForwardText, leftMargin + 280f, dnaTopY)
        font.draw(batch, dnaReverseText, leftMargin + 280f, dnaBottomY)
        batch.end()
    }

    private fun drawSequenceTiles(
        shapeRenderer: ShapeRenderer,
        startX: Float,
        startY: Float,
        tileSize: Float,
        tileGap: Float,
    ) {
        var x = startX
        for (nucleotide in sequenceText.filter { it.isLetter() }) {
            drawNucleotideTile(shapeRenderer, x, startY, tileSize, Nucleotide.valueOf(nucleotide.toString()))
            x += tileSize + tileGap
        }
    }

    private fun drawDnaDuplex(
        shapeRenderer: ShapeRenderer,
        startX: Float,
        topY: Float,
        tileSize: Float,
        tileGap: Float,
        contentWidth: Float,
    ) {
        val topStrand = dnaForwardText.filter { it.isLetter() }.map { Nucleotide.valueOf(it.toString()) }
        val bottomStrand = dnaReverseText.filter { it.isLetter() }.map { Nucleotide.valueOf(it.toString()) }
        val bottomY = topY - tileSize - 14f

        shapeRenderer.color = Color(0.2f, 0.6f, 0.95f, 1f)
        shapeRenderer.rect(startX - 8f, topY + tileSize * 0.5f, contentWidth * 0.55f, 3f)
        shapeRenderer.rect(startX - 8f, bottomY + tileSize * 0.5f, contentWidth * 0.55f, 3f)

        var x = startX
        topStrand.zip(bottomStrand).forEach { (top, bottom) ->
            drawNucleotideTile(shapeRenderer, x, topY, tileSize, top)
            drawNucleotideTile(shapeRenderer, x, bottomY, tileSize, bottom)
            shapeRenderer.color = Color(0.85f, 0.85f, 0.9f, 1f)
            shapeRenderer.rect(x + tileSize * 0.47f, bottomY + tileSize, tileSize * 0.08f, 12f)
            x += tileSize + tileGap
        }
    }

    private fun drawNucleotideTile(
        shapeRenderer: ShapeRenderer,
        x: Float,
        y: Float,
        size: Float,
        nucleotide: Nucleotide,
    ) {
        shapeRenderer.color = nucleotideColor(nucleotide)
        shapeRenderer.rect(x, y, size, size)
    }

    private fun nucleotideColor(nucleotide: Nucleotide): Color = when (nucleotide) {
        Nucleotide.A -> Color(0.95f, 0.65f, 0.3f, 1f)
        Nucleotide.U -> Color(0.36f, 0.78f, 0.95f, 1f)
        Nucleotide.C -> Color(0.55f, 0.88f, 0.42f, 1f)
        Nucleotide.G -> Color(0.9f, 0.42f, 0.76f, 1f)
    }

    companion object {
        fun sample(): DemoScene = DemoScene(
            nucleotide = Nucleotide.G,
            sequence = NucleotideSequence.of(">AUGCGAUCGUAA>"),
            dna = Dna.of(">ACGUACGUAC>"),
        )
    }
}
