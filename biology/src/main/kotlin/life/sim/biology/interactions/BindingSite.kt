package life.sim.biology.interactions

import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceRange

/**
 * A concrete binding window on a molecule strand.
 */
data class BindingSite(
    val surface: BindingSurface,
    val range: SequenceRange,
) {
    init {
        require(range.endExclusive <= surface.length) {
            "Binding site range $range exceeds surface length ${surface.length}."
        }
    }

    val moleculeId: MoleculeId
        get() = surface.moleculeId

    val strand: BindingStrand
        get() = surface.strand

    val sequence: NucleotideSequence
        get() = surface.sequence.slice(range)

    fun sameSurfaceAs(other: BindingSite): Boolean =
        moleculeId == other.moleculeId && strand == other.strand

    fun overlaps(other: BindingSite): Boolean = sameSurfaceAs(other) && range.overlaps(other.range)
}

