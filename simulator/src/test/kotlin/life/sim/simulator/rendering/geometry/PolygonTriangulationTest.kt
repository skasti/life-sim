package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class PolygonTriangulationTest {
    @Test
    fun `polygonOutline drops a duplicated closing vertex`() {
        val outline = polygonOutline(
            listOf(
                Vector2(0f, 0f),
                Vector2(2f, 0f),
                Vector2(1f, 1f),
                Vector2(0f, 0f),
            ),
        )

        assertEquals(3, outline.size)
        assertEquals(Vector2(0f, 0f), outline.first())
        assertEquals(Vector2(1f, 1f), outline.last())
    }

    @Test
    fun `triangulatePolygon preserves area for a concave closed outline`() {
        val vertices = listOf(
            Vector2(0f, 0f),
            Vector2(4f, 0f),
            Vector2(4f, 4f),
            Vector2(2f, 2f),
            Vector2(0f, 4f),
            Vector2(0f, 0f),
        )

        val triangles = triangulatePolygon(vertices)

        assertEquals(3, triangles.size)
        assertEquals(polygonArea(vertices), triangles.sumOf(::triangleArea).toFloat(), 0.0001f)
    }

    private fun polygonArea(vertices: List<Vector2>): Float {
        val outline = polygonOutline(vertices)
        var doubledArea = 0f
        for (index in outline.indices) {
            val current = outline[index]
            val next = outline[(index + 1) % outline.size]
            doubledArea += current.x * next.y - next.x * current.y
        }
        return abs(doubledArea) * 0.5f
    }

    private fun triangleArea(triangle: Triangle): Double =
        abs(
            triangle.x1 * (triangle.y2 - triangle.y3) +
                triangle.x2 * (triangle.y3 - triangle.y1) +
                triangle.x3 * (triangle.y1 - triangle.y2),
        ).toDouble() * 0.5
}


