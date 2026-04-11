package life.sim.genome

/**
 * Transfer RNA.
 *
 * This is currently a lightweight domain wrapper around a [NucleotideSequence],
 * allowing genome and biology code to distinguish tRNA from other RNA types.
 */
@JvmInline
value class TRna private constructor(
    private val sequence: NucleotideSequence,
) {
    val size: Int
        get() = sequence.size

    fun isEmpty(): Boolean = sequence.isEmpty()

    fun toNucleotideSequence(): NucleotideSequence = sequence

    override fun toString(): String = sequence.toString()

    companion object {
        fun empty(): TRna = TRna(NucleotideSequence.empty())

        fun of(sequence: NucleotideSequence): TRna = TRna(sequence)

        fun of(text: String): TRna = parse(text)

        fun parse(text: String): TRna = TRna(NucleotideSequence.parse(text))
    }
}

