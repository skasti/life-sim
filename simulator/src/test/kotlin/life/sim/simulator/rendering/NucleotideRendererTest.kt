package life.sim.simulator.rendering

import com.badlogic.gdx.math.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import life.sim.biology.primitives.Nucleotide

class NucleotideRendererTest {
    private val renderer = NucleotideRenderer()

    @Test
    fun `connectorProfile maps A and U to the angled family with opposite polarity`() {
        val adenineProfile = renderer.connectorProfile(Nucleotide.A)
        val uracilProfile = renderer.connectorProfile(Nucleotide.U)

        assertEquals(ConnectorFamily.ANGLED, adenineProfile.family)
        assertEquals(ConnectorFamily.ANGLED, uracilProfile.family)
        assertNotEquals(adenineProfile.polarity, uracilProfile.polarity)
    }

    @Test
    fun `connectorProfile maps C and G to the rounded family with opposite polarity`() {
        val cytosineProfile = renderer.connectorProfile(Nucleotide.C)
        val guanineProfile = renderer.connectorProfile(Nucleotide.G)

        assertEquals(ConnectorFamily.ROUNDED, cytosineProfile.family)
        assertEquals(ConnectorFamily.ROUNDED, guanineProfile.family)
        assertNotEquals(cytosineProfile.polarity, guanineProfile.polarity)
    }

    @Test
    fun `connectorProfile uses different families for AU and CG pairs`() {
        assertNotEquals(
            renderer.connectorProfile(Nucleotide.A).family,
            renderer.connectorProfile(Nucleotide.C).family,
        )
    }

    @Test
    fun `geometryFor keeps every nucleotide shape inside tile bounds`() {
        val origin = Vector2(10f, 20f)
        val nucleotides = listOf(Nucleotide.A, Nucleotide.U, Nucleotide.C, Nucleotide.G)
        val pairingSides = listOf(PairingSide.LEFT, PairingSide.RIGHT, PairingSide.TOP, PairingSide.BOTTOM)

        nucleotides.forEach { nucleotide ->
            pairingSides.forEach { pairingSide ->
                val geometry = renderer.geometryFor(nucleotide, origin, NucleotideOrientation(pairingSide))
                assertTrue(renderer.boundsWithinTile(geometry, origin), "Expected $nucleotide on $pairingSide to stay inside tile bounds")
            }
        }
    }
}
