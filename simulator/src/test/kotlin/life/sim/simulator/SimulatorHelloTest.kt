package life.sim.simulator

import kotlin.test.Test
import kotlin.test.assertEquals

class SimulatorHelloTest {
    @Test
    fun `simulator greeting references genome`() {
        assertEquals("Simulator ready: Hello from the genome module!", simulatorGreeting())
    }
}
