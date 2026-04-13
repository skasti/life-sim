package life.sim.biology.primitives

/**
 * A half-open range over a nucleotide sequence.
 *
 * The [start] index is inclusive and [endExclusive] is exclusive.
 * Empty ranges are valid, but never overlap any range.
 */
data class SequenceRange(
    val start: Int,
    val endExclusive: Int,
) {
    init {
        require(start >= 0) {
            "Sequence range start must be greater than or equal to zero, but was $start."
        }
        require(endExclusive >= start) {
            "Sequence range endExclusive must be greater than or equal to start, but was $endExclusive for start $start."
        }
    }

    val length: Int
        get() = endExclusive - start

    fun contains(index: Int): Boolean = index in start until endExclusive

    fun overlaps(other: SequenceRange): Boolean =
        maxOf(start, other.start) < minOf(endExclusive, other.endExclusive)
}

