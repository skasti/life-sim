package life.sim.simulator

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils

/**
 * Static simulator demo scene that renders one nucleotide, one sequence, and one DNA object.
 */
class SimulatorApplication : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private val demoScene = DemoScene.sample()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont().apply {
            data.setScale(1.4f)
        }
    }

    override fun render() {
        ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f)

        batch.begin()
        font.draw(batch, "Life-Sim Rendering Demo (static scene)", 40f, 680f)
        font.draw(batch, "Nucleotide", 40f, 600f)
        font.draw(batch, demoScene.nucleotide.symbol.toString(), 300f, 600f)

        font.draw(batch, "Nucleotide sequence", 40f, 500f)
        font.draw(batch, demoScene.sequence.toString(), 300f, 500f)

        font.draw(batch, "DNA duplex", 40f, 400f)
        font.draw(batch, demoScene.dna.forward.toString(), 300f, 430f)
        font.draw(batch, demoScene.dna.reverse.toString(), 300f, 390f)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
