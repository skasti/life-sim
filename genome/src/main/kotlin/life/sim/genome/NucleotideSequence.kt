package life.sim.genome

/**
 * An immutable sequence of [Nucleotide] values.
 *
 * This is currently a lightweight domain wrapper around a list of nucleotides,
 * giving the genome layer a dedicated sequence type without committing to a
 * packed byte representation yet.
 */
@JvmInline
value class NucleotideSequence private constructor(
    private val nucleotides: List<Nucleotide>,
) : Iterable<Nucleotide> {
    val size: Int
        get() = nucleotides.size

    fun isEmpty(): Boolean = nucleotides.isEmpty()

    operator fun get(index: Int): Nucleotide = nucleotides[index]

    override fun iterator(): Iterator<Nucleotide> = nucleotides.iterator()

    fun complement(): NucleotideSequence =
        NucleotideSequence(nucleotides.map(Nucleotide::complement))

    fun reversed(): NucleotideSequence =
        NucleotideSequence(nucleotides.reversed())

    override fun toString(): String = nucleotides.joinToString(separator = "") { it.symbol.toString() }

    fun toList(): List<Nucleotide> = nucleotides.toList()

    companion object {
        fun empty(): NucleotideSequence = NucleotideSequence(emptyList())

        fun of(vararg nucleotides: Nucleotide): NucleotideSequence =
            NucleotideSequence(nucleotides.toList())

        fun of(text: String): NucleotideSequence = parse(text)

        fun from(nucleotides: List<Nucleotide>): NucleotideSequence =
            NucleotideSequence(nucleotides.toList())

        fun parse(text: String): NucleotideSequence =
            NucleotideSequence(
                text.mapIndexed { index, symbol ->
                    try {
                        Nucleotide.fromChar(symbol)
                    } catch (_: IllegalArgumentException) {
                        throw IllegalArgumentException(
                            "Invalid nucleotide '$symbol' at index $index. Expected one of A, C, G, or U.",
                        )
                    }
                },
            )
    }
}

fun List<Nucleotide>.toNucleotideSequence(): NucleotideSequence = NucleotideSequence.from(this)

