package life.sim.simulator

import life.sim.biology.molecules.Dna
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence

/**
 * Static, hand-authored rendering inputs used as a visual baseline for simulator work.
 *
 * Keeping this scene deterministic makes it easy to manually verify renderer changes
 * before dynamic simulation behavior is introduced.
 */
data class DemoScene(
    val nucleotide: Nucleotide,
    val sequence: NucleotideSequence,
    val dna: Dna,
) {
    companion object {
        fun sample(): DemoScene = DemoScene(
            nucleotide = Nucleotide.G,
            sequence = NucleotideSequence.of(">AUGCGAUCGUAA>"),
            dna = Dna.of(">ACGUACGUAC>"),
        )
    }
}
