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

    @Test
    fun `arcBounds uses swept extrema for a counterclockwise semicircle`() {
        val bounds = renderer.arcBounds(
            Arc(
                x = 0f,
                y = 0f,
                radius = 10f,
                startDegrees = 0f,
                degrees = 180f,
            ),
        )

        assertEquals(-10f, bounds.minX, 0.0001f)
        assertEquals(10f, bounds.maxX, 0.0001f)
        assertEquals(0f, bounds.minY, 0.0001f)
        assertEquals(10f, bounds.maxY, 0.0001f)
    }

    @Test
    fun `arcBounds uses swept extrema for a counterclockwise quadrant`() {
        val bounds = renderer.arcBounds(
            Arc(
                x = 0f,
                y = 0f,
                radius = 10f,
                startDegrees = 90f,
                degrees = 90f,
            ),
        )

        assertEquals(-10f, bounds.minX, 0.0001f)
        assertEquals(0f, bounds.maxX, 0.0001f)
        assertEquals(0f, bounds.minY, 0.0001f)
        assertEquals(10f, bounds.maxY, 0.0001f)
    }

    @Test
    fun `boundsWithinTile accepts outline arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = NucleotideGeometry(
            filledTriangles = emptyList(),
            filledRects = emptyList(),
            filledArcs = emptyList(),
            arcs = listOf(
                Arc(
                    x = origin.x - renderer.tileSize * 0.75f,
                    y = origin.y,
                    radius = 10f,
                    startDegrees = -90f,
                    degrees = 180f,
                ),
            ),
            triangles = emptyList(),
            lines = emptyList(),
        )

        assertTrue(renderer.boundsWithinTile(geometry, origin))
    }

    @Test
    fun `boundsWithinTile accepts filled arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = NucleotideGeometry(
            filledTriangles = emptyList(),
            filledRects = emptyList(),
            filledArcs = listOf(
                Arc(
                    x = origin.x,
                    y = origin.y - renderer.tileSize * 0.75f,
                    radius = 10f,
                    startDegrees = 0f,
                    degrees = 180f,
                ),
            ),
            arcs = emptyList(),
            triangles = emptyList(),
            lines = emptyList(),
        )

        assertTrue(renderer.boundsWithinTile(geometry, origin))
    }
}
