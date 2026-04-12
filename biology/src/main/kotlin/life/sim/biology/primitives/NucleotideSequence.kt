package life.sim.biology.primitives

/**
 * An immutable sequence of [Nucleotide] values.
 *
 * This is currently a lightweight domain wrapper around a list of nucleotides,
 * giving the biology layer a dedicated sequence type without committing to a
 * packed byte representation yet.
 */
class NucleotideSequence private constructor(
    private val nucleotides: List<Nucleotide>,
    val direction: SequenceDirection,
) : Iterable<Nucleotide> {
    val size: Int
        get() = nucleotides.size

    fun isEmpty(): Boolean = nucleotides.isEmpty()

    operator fun get(index: Int): Nucleotide = nucleotides[index]

    override fun iterator(): Iterator<Nucleotide> = nucleotides.iterator()

    fun slice(range: SequenceRange): NucleotideSequence = slice(range.start, range.endExclusive)

    fun slice(start: Int, endExclusive: Int): NucleotideSequence {
        require(start >= 0) {
            "Slice start must be greater than or equal to zero, but was $start."
        }
        require(endExclusive >= start) {
            "Slice endExclusive must be greater than or equal to start, but was $endExclusive for start $start."
        }
        require(endExclusive <= size) {
            "Slice endExclusive must be less than or equal to sequence size $size, but was $endExclusive."
        }

        return NucleotideSequence(nucleotides.subList(start, endExclusive).toList(), direction)
    }

    fun complement(): NucleotideSequence =
        NucleotideSequence(nucleotides.map(Nucleotide::complement), direction.opposite())

    fun reversed(): NucleotideSequence =
        NucleotideSequence(nucleotides.reversed(), direction.opposite())

    override fun toString(): String {
        val content = nucleotides.joinToString(separator = "") { it.symbol.toString() }
        return "${direction.marker}$content${direction.marker}"
    }

    fun toList(): List<Nucleotide> = nucleotides.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is NucleotideSequence) {
            return false
        }

        return nucleotides == other.nucleotides && direction == other.direction
    }

    override fun hashCode(): Int = 31 * nucleotides.hashCode() + direction.hashCode()

    companion object {
        fun empty(direction: SequenceDirection = SequenceDirection.FORWARD): NucleotideSequence =
            NucleotideSequence(emptyList(), direction)

        fun of(vararg nucleotides: Nucleotide, direction: SequenceDirection = SequenceDirection.FORWARD): NucleotideSequence =
            NucleotideSequence(nucleotides.toList(), direction)

        fun of(text: String): NucleotideSequence = parse(text)

        fun from(
            nucleotides: List<Nucleotide>,
            direction: SequenceDirection = SequenceDirection.FORWARD,
        ): NucleotideSequence = NucleotideSequence(nucleotides.toList(), direction)

        fun parse(text: String): NucleotideSequence {
            val direction = when {
                text.length >= 2 && text.startsWith(">") && text.endsWith(">") -> SequenceDirection.FORWARD
                text.length >= 2 && text.startsWith("<") && text.endsWith("<") -> SequenceDirection.BACKWARD
                text.startsWith(">") || text.endsWith(">") || text.startsWith("<") || text.endsWith("<") -> {
                    throw IllegalArgumentException(
                        "Nucleotide sequence text must be surrounded by matching direction markers or contain no markers, but was '$text'.",
                    )
                }

                else -> SequenceDirection.FORWARD
            }

            val content = when {
                text.length >= 2 && text.startsWith(">") && text.endsWith(">") -> text.substring(1, text.length - 1)
                text.length >= 2 && text.startsWith("<") && text.endsWith("<") -> text.substring(1, text.length - 1)
                text.contains('>') || text.contains('<') -> {
                    throw IllegalArgumentException(
                        "Nucleotide sequence text must be surrounded by matching direction markers or contain no markers, but was '$text'.",
                    )
                }

                else -> text
            }

            return NucleotideSequence(
                content.mapIndexed { index, symbol ->
                    try {
                        Nucleotide.fromChar(symbol)
                    } catch (_: IllegalArgumentException) {
                        throw IllegalArgumentException(
                            "Invalid nucleotide '$symbol' at index $index. Expected one of A, C, G, or U.",
                        )
                    }
                },
                direction,
            )
        }
    }
}

fun List<Nucleotide>.toNucleotideSequence(): NucleotideSequence = NucleotideSequence.from(this)


