package life.sim.genome

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

    fun scan(sequence: NucleotideSequence): Int {
        if (isEmpty()) {
            return 0
        }

        if (size > sequence.size) {
            return -1
        }

        val lastStartIndex = sequence.size - size
        for (startIndex in 0..lastStartIndex) {
            var matches = true

            for (offset in 0 until size) {
                if (this.sequence[offset] != sequence[startIndex + offset].complement()) {
                    matches = false
                    break
                }
            }

            if (matches) {
                return startIndex
            }
        }

        return -1
    }

    fun toNucleotideSequence(): NucleotideSequence = sequence

    override fun toString(): String = sequence.toString()

    companion object {
        fun empty(): TRna = TRna(NucleotideSequence.empty())

        fun of(sequence: NucleotideSequence): TRna = TRna(sequence)

        fun of(text: String): TRna = parse(text)

        fun parse(text: String): TRna = TRna(NucleotideSequence.parse(text))
    }
}

