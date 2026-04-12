package life.sim.genome

/**
 * Messenger RNA.
 *
 * This is currently a lightweight domain wrapper around a [NucleotideSequence],
 * allowing genome and biology code to distinguish mRNA from other RNA types.
 */
@JvmInline
value class MRna private constructor(
    private val sequence: NucleotideSequence,
) {
    val size: Int
        get() = sequence.size

    fun isEmpty(): Boolean = sequence.isEmpty()

    fun toNucleotideSequence(): NucleotideSequence = sequence

    override fun toString(): String = sequence.toString()
    fun complement(): MRna = MRna(sequence.complement())

    companion object {
        fun empty(): MRna = MRna(NucleotideSequence.empty())

        fun of(sequence: NucleotideSequence): MRna = MRna(sequence)

        fun of(text: String): MRna = parse(text)

        fun parse(text: String): MRna = MRna(NucleotideSequence.parse(text))
    }
}

