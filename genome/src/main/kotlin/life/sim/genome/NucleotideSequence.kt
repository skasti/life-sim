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

    fun toList(): List<Nucleotide> = nucleotides.toList()

    companion object {
        fun empty(): NucleotideSequence = NucleotideSequence(emptyList())

        fun of(vararg nucleotides: Nucleotide): NucleotideSequence =
            NucleotideSequence(nucleotides.toList())

        fun from(nucleotides: List<Nucleotide>): NucleotideSequence =
            NucleotideSequence(nucleotides.toList())
    }
}

fun List<Nucleotide>.toNucleotideSequence(): NucleotideSequence = NucleotideSequence.from(this)

