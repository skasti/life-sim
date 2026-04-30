package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color

import com.badlogic.gdx.math.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PolygonFactoryTest {
    @Test
    fun `rect creates a closed four-corner polygon`() {
        val polygon = Polygon.rect(10f, 20f, 30f, 40f, color = Color.WHITE)

        assertEquals(Vector2(10f, 20f), polygon.vertices.first())
        assertEquals(Vector2(10f, 20f), polygon.vertices.last())
        assertEquals(5, polygon.vertices.size)
    }

    @Test
    fun `triangle creates a closed triangle polygon`() {
        val polygon = Polygon.triangle(Vector2(0f, 0f), Vector2(10f, 0f), Vector2(5f, 8f), color = Color.WHITE)

        assertEquals(4, polygon.vertices.size)
        assertEquals(polygon.vertices.first(), polygon.vertices.last())
    }

    @Test
    fun `circle creates closed approximation with requested segments around provided center`() {
        val center = Vector2(3f, -4f)
        val polygon = Polygon.circle(center, radius = 5f, color = Color.WHITE, segments = 12)

        assertEquals(13, polygon.vertices.size)
        assertEquals(polygon.vertices.first(), polygon.vertices.last())
        assertTrue(polygon.vertices.dropLast(1).all { point -> kotlin.math.abs(point.dst(center) - 5f) < 0.0001f })
    }

    @Test
    fun `circle rejects non-positive radius`() {
        assertFailsWith<IllegalArgumentException> {
            Polygon.circle(Vector2(0f, 0f), radius = 0f, color = Color.WHITE, segments = 12)
        }
    }
}
