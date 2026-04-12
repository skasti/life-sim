package life.sim.biology.primitives

/**
 * The orientation of a nucleotide sequence.
 */
enum class SequenceDirection(val marker: Char) {
    FORWARD('>'),
    BACKWARD('<');

    fun opposite(): SequenceDirection = when (this) {
        FORWARD -> BACKWARD
        BACKWARD -> FORWARD
    }
}


