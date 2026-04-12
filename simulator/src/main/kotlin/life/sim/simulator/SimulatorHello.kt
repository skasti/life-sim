package life.sim.simulator

import life.sim.biology.primitives.NucleotideSequence

/**
 * Simple placeholder demonstrating simulator module wiring and dependency on the biology module.
 */
fun simulatorGreeting(): String {
    val sequence = NucleotideSequence.of("ACGU")
    return "Simulator ready: $sequence"
}
