package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertEquals

class GenomeHelloTest {
    @Test
    fun `genome greeting returns expected message`() {
        assertEquals("Hello from the genome module!", genomeGreeting())
    }
}
