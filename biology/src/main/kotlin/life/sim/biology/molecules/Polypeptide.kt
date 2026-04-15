package life.sim.biology.molecules

/**
 * A translated amino-acid chain.
 */
class Polypeptide private constructor(
    private val residues: List<AminoAcid>,
) {
    val size: Int
        get() = residues.size

    fun isEmpty(): Boolean = residues.isEmpty()

    operator fun get(index: Int): AminoAcid = residues[index]

    fun toList(): List<AminoAcid> = residues.toList()

    fun subsequence(startInclusive: Int, endExclusive: Int): Polypeptide {
        require(startInclusive in 0..size) {
            "Polypeptide start index must be in 0..$size, but was $startInclusive."
        }
        require(endExclusive in startInclusive..size) {
            "Polypeptide end index must be in $startInclusive..$size, but was $endExclusive."
        }

        return Polypeptide(residues.subList(startInclusive, endExclusive))
    }

    override fun toString(): String = residues.joinToString(separator = "") { it.symbol.toString() }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Polypeptide) {
            return false
        }

        return residues == other.residues
    }

    override fun hashCode(): Int = residues.hashCode()

    companion object {
        fun empty(): Polypeptide = Polypeptide(emptyList())

        fun from(residues: List<AminoAcid>): Polypeptide = Polypeptide(residues.toList())

        fun of(text: String): Polypeptide = parse(text)

        fun parse(text: String): Polypeptide =
            Polypeptide(text.trim().map(AminoAcid::fromChar))
    }
}
