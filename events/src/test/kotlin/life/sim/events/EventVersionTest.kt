package life.sim.events

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventVersionTest {
    @Test
    fun `toString renders major minor revision format`() {
        assertEquals("1.2.3", EventVersion(1, 2, 3).toString())
    }

    @Test
    fun `parse accepts major minor revision format`() {
        val parsed = EventVersion.parse("2.10.7")

        assertEquals(EventVersion(2, 10, 7), parsed)
    }

    @Test
    fun `parse rejects invalid version format`() {
        assertFailsWith<IllegalArgumentException> {
            EventVersion.parse("1.0")
        }
    }

    @Test
    fun `constructor rejects negative version components`() {
        assertFailsWith<IllegalArgumentException> {
            EventVersion(-1, 0, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            EventVersion(0, -1, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            EventVersion(0, 0, -1)
        }
    }
}
