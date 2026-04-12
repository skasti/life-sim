package life.sim.simulator

import life.sim.genome.NucleotideSequence

/**
 * Simple placeholder demonstrating simulator module wiring and dependency on the genome module.
 */
fun simulatorGreeting(): String {
    val sequence = NucleotideSequence.of("ACGU")
    return "Simulator ready: $sequence"
}
