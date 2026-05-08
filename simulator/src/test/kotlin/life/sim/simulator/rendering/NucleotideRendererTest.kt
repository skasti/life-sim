package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import life.sim.biology.primitives.Nucleotide
import life.sim.simulator.rendering.geometry.*

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

        nucleotides.forEach { nucleotide ->
            val geometry = renderer.geometryFor(nucleotide, origin)
            assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin), "Expected $nucleotide to stay inside the geometry test window")
        }
    }


    @Test
    fun `isWithinNucleotideGeometryTestWindow accepts outline arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = Geometry(
            Arc(
                x = origin.x - renderer.baseSize * 0.75f + 1.5f,
                y = origin.y,
                radius = 10f,
                startDegrees = -90f,
                degrees = 180f,
                color = Color.WHITE,
                lineWidth = 3f,
            ),
        )

        assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin))
    }

    @Test
    fun `isWithinNucleotideGeometryTestWindow accepts filled arcs that only sweep inside the tile`() {
        val origin = Vector2(0f, 0f)
        val geometry = Geometry(
            Arc(
                x = origin.x,
                y = origin.y - renderer.baseSize * 0.75f,
                radius = 10f,
                startDegrees = 0f,
                degrees = 180f,
                color = Color.WHITE,
                lineWidth = 0f,
            ),
        )

        assertTrue(isWithinNucleotideGeometryTestWindow(geometry, origin))
    }


    @Test
    fun `geometryFor renders rounded protrusions as polygon geometry that extends beyond the right edge of the base tile`() {
        val origin = Vector2(10f, 20f)
        val geometry = renderer.geometryFor(Nucleotide.C, origin)

        assertTrue(geometry.elements.none { it is Arc }, "Expected rounded protrusion for C to avoid arc primitives")

        val vertices = geometry.elements.filterIsInstance<Polygon>().flatMap { it.vertices }

        assertTrue(
            vertices.any { it.x > origin.x + renderer.baseSize },
            "Expected the rounded protrusion to extend beyond the right edge",
        )
        assertTrue(
            vertices.none { it.x < origin.x },
            "Expected the rounded protrusion to keep the left edge aligned with the base tile",
        )
    }

    @Test
    fun `geometryFor renders rounded indentations as a single polygon with an outward concave socket on the right edge`() {
        val origin = Vector2(10f, 20f)
        val baseCorners = listOf(
            Vector2(origin.x, origin.y),
            Vector2(origin.x + renderer.baseSize, origin.y),
            Vector2(origin.x, origin.y + renderer.baseSize),
            Vector2(origin.x + renderer.baseSize, origin.y + renderer.baseSize),
        )
        val geometry = renderer.geometryFor(Nucleotide.G, origin)
        val polygon = geometry.elements.filterIsInstance<Polygon>().single()

        assertTrue(geometry.elements.none { it is Arc || it is Line })
        assertTrue(baseCorners.all(polygon.vertices::contains), "Expected the rounded indentation polygon to keep the rectangular body corners")
        assertTrue(polygon.vertices.first() == polygon.vertices.last(), "Expected the rounded indentation polygon to be closed")
        assertTrue(polygonArea(polygon.vertices) > renderer.baseSize * renderer.baseSize, "Expected the rounded indentation polygon to cover more area than the base square")
        assertTrue(polygon.vertices.any { it.x > origin.x + renderer.baseSize }, "Expected the rounded socket to extend beyond the right edge")
        assertTrue(
            polygon.vertices.any { approximatelyEqual(it.x, origin.x + renderer.baseSize) && approximatelyEqual(it.y, origin.y + renderer.baseSize * 0.5f) },
            "Expected the rounded socket to curve back inward to the right body edge",
        )
    }



    @Test
    fun `spriteKey uses canonical nucleotide identity`() {
        assertEquals(SpriteKey("Nucleotide_A"), renderer.spriteKey(Nucleotide.A))
        assertEquals(SpriteKey("Nucleotide_U"), renderer.spriteKey(Nucleotide.U))
        assertEquals(SpriteKey("Nucleotide_C"), renderer.spriteKey(Nucleotide.C))
        assertEquals(SpriteKey("Nucleotide_G"), renderer.spriteKey(Nucleotide.G))
    }

    @Test
    fun `renderToSpriteCached uses a half-height origin on the left edge of the sprite`() {
        val renderer = NucleotideRenderer(baseSize = 40f)
        val baseSize = renderer.baseSize
        val pairingBandSize = baseSize * 0.5f
        val spriteWidth = kotlin.math.ceil(baseSize * 1.5f)
        val spriteHeight = kotlin.math.ceil(baseSize)
        val originX = 0f
        val originY = baseSize * 0.5f

        assertEquals(baseSize * 1.5f, spriteWidth)
        assertEquals(baseSize, spriteHeight)
        assertEquals(0f, originX)
        assertEquals(baseSize * 0.5f, originY)
        assertEquals(baseSize * 0.5f, pairingBandSize)
    }

    @Test
    fun `lowerLeftFromCenter maps center coordinates to the nucleotide tile anchor`() {
        val renderer = NucleotideRenderer(baseSize = 40f)

        assertEquals(Vector2(80f, 180f), renderer.lowerLeftFromCenter(Vector2(100f, 200f)))
    }

    private fun isWithinNucleotideGeometryTestWindow(geometry: Geometry, position: Vector2): Boolean {
        val minX = position.x - renderer.baseSize * 0.75f
        val maxX = position.x + renderer.baseSize * 1.75f
        val minY = position.y - renderer.baseSize * 0.75f
        val maxY = position.y + renderer.baseSize * 1.75f

        return geometry.elements.all { element ->
            when (element) {
                is Polygon -> element.vertices.all { point -> point.x in minX..maxX && point.y in minY..maxY }
                is Arc -> {
                    val bounds = if (element.lineWidth <= 0f) element.bounds(includeCenter = true) else element.bounds(includeStroke = true)
                    bounds.isWithin(minX, maxX, minY, maxY)
                }
                is Line -> listOf(element.a, element.b).all { point -> point.x in minX..maxX && point.y in minY..maxY }
                else -> error("Unhandled geometry element type in nucleotide bounds test: ${element::class.qualifiedName}")
            }
        }
    }

    private fun polygonArea(vertices: List<Vector2>): Float {
        val outline = polygonOutline(vertices)
        var doubledArea = 0f
        for (index in outline.indices) {
            val current = outline[index]
            val next = outline[(index + 1) % outline.size]
            doubledArea += current.x * next.y - next.x * current.y
        }
        return kotlin.math.abs(doubledArea) * 0.5f
    }

    private fun approximatelyEqual(a: Float, b: Float, tolerance: Float = 0.0001f): Boolean =
        kotlin.math.abs(a - b) <= tolerance
}
