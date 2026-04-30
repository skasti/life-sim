package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color

import kotlin.test.Test
import kotlin.test.assertEquals

class ArcBoundsTest {
    @Test
    fun `bounds uses swept extrema for a counterclockwise semicircle`() {
        val bounds = Arc(
            x = 0f,
            y = 0f,
            radius = 10f,
            startDegrees = 0f,
            degrees = 180f,
            color = Color.WHITE,
            lineWidth = 3f,
        ).bounds()

        assertEquals(-10f, bounds.minX, 0.0001f)
        assertEquals(10f, bounds.maxX, 0.0001f)
        assertEquals(0f, bounds.minY, 0.0001f)
        assertEquals(10f, bounds.maxY, 0.0001f)
    }

    @Test
    fun `bounds can include stroke width for outline arcs`() {
        val bounds = Arc(
            x = 0f,
            y = 0f,
            radius = 10f,
            startDegrees = 0f,
            degrees = 180f,
            color = Color.WHITE,
            lineWidth = 4f,
        ).bounds(includeStroke = true)

        assertEquals(-12f, bounds.minX, 0.0001f)
        assertEquals(12f, bounds.maxX, 0.0001f)
        assertEquals(-2f, bounds.minY, 0.0001f)
        assertEquals(12f, bounds.maxY, 0.0001f)
    }

    @Test
    fun `bounds uses swept extrema for a counterclockwise quadrant`() {
        val bounds = Arc(
            x = 0f,
            y = 0f,
            radius = 10f,
            startDegrees = 90f,
            degrees = 90f,
            color = Color.WHITE,
            lineWidth = 3f,
        ).bounds()

        assertEquals(-10f, bounds.minX, 0.0001f)
        assertEquals(0f, bounds.maxX, 0.0001f)
        assertEquals(0f, bounds.minY, 0.0001f)
        assertEquals(10f, bounds.maxY, 0.0001f)
    }
}
