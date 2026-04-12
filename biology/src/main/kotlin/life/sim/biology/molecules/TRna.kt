package life.sim.biology.molecules

import life.sim.biology.interactions.BindingMatcher
import life.sim.biology.primitives.NucleotideSequence

/**
 * Transfer RNA.
 *
 * This is currently a lightweight domain wrapper around a [NucleotideSequence],
 * allowing genome and biology code to distinguish tRNA from other RNA types.
 *
 * Scanning uses complementary RNA binding rules rather than exact nucleotide equality.
 */
@JvmInline
value class TRna private constructor(
    private val sequence: NucleotideSequence,
) {
    val size: Int
        get() = sequence.size

    fun isEmpty(): Boolean = sequence.isEmpty()

    fun scan(target: NucleotideSequence): Int = BindingMatcher.complementaryMatchStart(sequence, target)

    fun toNucleotideSequence(): NucleotideSequence = sequence

    override fun toString(): String = sequence.toString()

    companion object {
        fun empty(): TRna = TRna(NucleotideSequence.empty())

        fun of(sequence: NucleotideSequence): TRna = TRna(sequence)

        fun of(text: String): TRna = parse(text)

        fun parse(text: String): TRna = TRna(NucleotideSequence.parse(text))
    }
}


