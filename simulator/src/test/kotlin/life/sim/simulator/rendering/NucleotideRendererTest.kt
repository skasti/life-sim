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
    fun `geometryFor keeps every nucleotide shape inside the geometry test window`() {
        val origin = Vector2(10f, 20f)
        val nucleotides = listOf(Nucleotide.A, Nucleotide.U, Nucleotide.C, Nucleotide.G)
        val pairingSides = listOf(PairingSide.LEFT, PairingSide.RIGHT, PairingSide.TOP, PairingSide.BOTTOM)

        nucleotides.forEach { nucleotide ->
            pairingSides.forEach { pairingSide ->
                val geometry = renderer.geometryFor(nucleotide, origin, NucleotideOrientation(pairingSide))
                assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin), "Expected $nucleotide on $pairingSide to stay inside the geometry test window")
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
    fun `isWithinNucleotideGeometryTestWindow accepts outline arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = NucleotideGeometry(
            filledTriangles = emptyList(),
            filledRects = emptyList(),
            filledArcs = emptyList(),
            arcs = listOf(
                Arc(
                    x = origin.x - renderer.baseSize * 0.75f,
                    y = origin.y,
                    radius = 10f,
                    startDegrees = -90f,
                    degrees = 180f,
                ),
            ),
            triangles = emptyList(),
            lines = emptyList(),
        )

        assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin))
    }

    @Test
    fun `isWithinNucleotideGeometryTestWindow accepts filled arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = NucleotideGeometry(
            filledTriangles = emptyList(),
            filledRects = emptyList(),
            filledArcs = listOf(
                Arc(
                    x = origin.x,
                    y = origin.y - renderer.baseSize * 0.75f,
                    radius = 10f,
                    startDegrees = 0f,
                    degrees = 180f,
                ),
            ),
            arcs = emptyList(),
            triangles = emptyList(),
            lines = emptyList(),
        )

        assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin))
    }


    private fun isWithinNucleotideGeometryTestWindow(geometry: NucleotideGeometry, position: Vector2): Boolean {
        val minX = position.x - renderer.baseSize * 0.75f
        val maxX = position.x + renderer.baseSize * 1.75f
        val minY = position.y - renderer.baseSize * 0.75f
        val maxY = position.y + renderer.baseSize * 1.75f

        val rectsInBounds = geometry.filledRects.all { rect ->
            rect.x >= minX &&
                rect.y >= minY &&
                rect.x + rect.width <= maxX &&
                rect.y + rect.height <= maxY
        }

        if (!rectsInBounds) return false

        val filledTrianglesInBounds = geometry.filledTriangles.all { triangle ->
            val xs = listOf(triangle.x1, triangle.x2, triangle.x3)
            val ys = listOf(triangle.y1, triangle.y2, triangle.y3)
            xs.all { it in minX..maxX } && ys.all { it in minY..maxY }
        }

        if (!filledTrianglesInBounds) return false

        val trianglesInBounds = geometry.triangles.all { triangle ->
            val xs = listOf(triangle.x1, triangle.x2, triangle.x3)
            val ys = listOf(triangle.y1, triangle.y2, triangle.y3)
            xs.all { it in minX..maxX } && ys.all { it in minY..maxY }
        }

        if (!trianglesInBounds) return false

        val filledArcsInBounds = geometry.filledArcs.all { arc ->
            renderer.arcBounds(arc, includeCenter = true).isWithin(minX, maxX, minY, maxY)
        }

        if (!filledArcsInBounds) return false

        val arcsInBounds = geometry.arcs.all { arc ->
            renderer.arcBounds(arc).isWithin(minX, maxX, minY, maxY)
        }

        if (!arcsInBounds) return false

        return geometry.lines.all { line ->
            listOf(line.a, line.b).all { point ->
                point.x in minX..maxX && point.y in minY..maxY
            }
        }
    }
}
