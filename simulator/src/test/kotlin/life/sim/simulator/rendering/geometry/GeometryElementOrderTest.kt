package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.test.*

class GeometryElementOrderTest {
    @Test
    fun `constructor preserves provided element order`() {
        val first = Polygon.rect(0f, 0f, 1f, 1f, color = Color.RED)
        val second = Arc(1f, 1f, 1f, 0f, 180f, color = Color.BLUE)
        val third = Line(Vector2(0f, 0f), Vector2(1f, 1f), 1f, Color.GREEN)

        val geometry = Geometry(first, second, third)

        assertEquals(listOf(first, second, third), geometry.elements)
    }
}
