package life.sim.biology.interactions

import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceRange

/**
 * An addressable strand of a molecule that can host bonds.
 */
data class BindingSurface(
    val moleculeId: EntityId,
    val strand: BindingStrand,
    val sequence: NucleotideSequence,
) {
    val length: Int
        get() = sequence.size

    fun site(range: SequenceRange): BindingSite = BindingSite(this, range)

    fun site(start: Int, endExclusive: Int): BindingSite = site(SequenceRange(start, endExclusive))
}

