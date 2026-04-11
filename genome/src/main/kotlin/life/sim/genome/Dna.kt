package life.sim.genome

/**
 * A double-stranded DNA-like sequence composed of two complementary nucleotide strands.
 *
 * Serialization uses two lines: one line per strand.
 */
class Dna private constructor(
    val forward: NucleotideSequence,
    val reverse: NucleotideSequence,
) {
    val size: Int
        get() = forward.size

    fun isEmpty(): Boolean = forward.isEmpty()

    override fun toString(): String = "$forward\n$reverse"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Dna) {
            return false
        }

        return forward == other.forward && reverse == other.reverse
    }

    override fun hashCode(): Int = 31 * forward.hashCode() + reverse.hashCode()

    companion object {
        fun empty(): Dna = Dna(
            NucleotideSequence.empty(SequenceDirection.FORWARD),
            NucleotideSequence.empty(SequenceDirection.BACKWARD),
        )

        fun of(forward: NucleotideSequence, reverse: NucleotideSequence = forward.complement()): Dna {
            val normalizedForward = NucleotideSequence.from(forward.toList(), SequenceDirection.FORWARD)
            val normalizedReverse = NucleotideSequence.from(reverse.toList(), SequenceDirection.BACKWARD)

            require(normalizedForward.size == normalizedReverse.size) {
                "DNA forward and reverse strands must have the same length, but were ${normalizedForward.size} and ${normalizedReverse.size}."
            }

            for (index in 0 until normalizedForward.size) {
                val expected = normalizedForward[index].complement()
                val actual = normalizedReverse[index]
                require(expected == actual) {
                    "DNA forward and reverse strands must be complementary at index $index, but found ${normalizedForward[index].symbol} and ${actual.symbol}."
                }
            }

            return Dna(normalizedForward, normalizedReverse)
        }

        fun of(forward: String, reverse: String): Dna =
            of(NucleotideSequence.parse(forward), NucleotideSequence.parse(reverse))

        fun of(text: String): Dna = parse(text)

        fun parse(text: String): Dna {
            val normalized = text.replace("\r\n", "\n").trimEnd('\n', '\r')
            val lines = if (normalized.isEmpty()) listOf("") else normalized.split('\n')

            require(lines.size <= 2) {
                "DNA text must contain one or two lines, but had ${lines.size}."
            }

            val forward = NucleotideSequence.parse(lines[0])
            return if (lines.size == 1) {
                of(forward)
            } else {
                of(forward, NucleotideSequence.parse(lines[1]))
            }
        }
    }
}


