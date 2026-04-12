package life.sim.simulator

import kotlin.test.Test
import kotlin.test.assertEquals

class SimulatorHelloTest {
    @Test
    fun `simulator greeting renders a nucleotide sequence`() {
        assertEquals("Simulator ready: >ACGU>", simulatorGreeting())
    }
}
