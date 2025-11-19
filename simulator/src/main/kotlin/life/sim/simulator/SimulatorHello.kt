package life.sim.simulator

import life.sim.genome.genomeGreeting

/**
 * Simple placeholder demonstrating simulator module wiring and dependency on the genome module.
 */
fun simulatorGreeting(): String = "Simulator ready: ${genomeGreeting()}"
