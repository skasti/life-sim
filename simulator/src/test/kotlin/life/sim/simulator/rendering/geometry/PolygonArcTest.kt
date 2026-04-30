package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.math.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolygonArcTest {
    @Test
    fun `arc defaults to the counterclockwise sweep for opposite points on a diameter`() {
        val points = arc(
            start = Vector2(0f, 0f),
            center = Vector2(0f, 10f),
            end = Vector2(0f, 20f),
            segments = 4,
        )

        assertEquals(5, points.size)
        assertTrue(points[2].x > 0f, "Expected the default counterclockwise sweep to pass on the positive-x side of the circle")
    }

    @Test
    fun `arc can sweep clockwise for opposite points on a diameter`() {
        val points = arc(
            start = Vector2(0f, 0f),
            center = Vector2(0f, 10f),
            end = Vector2(0f, 20f),
            segments = 4,
            sweepDirection = ArcSweepDirection.CLOCKWISE,
        )

        assertEquals(5, points.size)
        assertTrue(points[2].x < 0f, "Expected the clockwise sweep to pass on the negative-x side of the circle")
    }
}

