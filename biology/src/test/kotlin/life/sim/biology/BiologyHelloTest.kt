package life.sim.biology

import kotlin.test.Test
import kotlin.test.assertEquals

class BiologyHelloTest {
    @Test
    fun `biology greeting returns expected message`() {
        assertEquals("Hello from the biology module!", biologyGreeting())
    }
}

