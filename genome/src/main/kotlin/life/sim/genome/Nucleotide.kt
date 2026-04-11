package life.sim.genome

/**
 * A nucleotide encoded as a 2-bit value.
 *
 * The bit mapping is stable and compact for byte-packed genomes:
 * - `A` = `0b00`
 * - `C` = `0b01`
 * - `G` = `0b10`
 * - `U` = `0b11`
 *
 * Complements follow RNA base-pairing rules:
 * - `A` complements `U`
 * - `C` complements `G`
 */
enum class Nucleotide(val bits: Byte, val symbol: Char) {
    A(0b00, 'A'),
    C(0b01, 'C'),
    G(0b10, 'G'),
    U(0b11, 'U');

    fun complement(): Nucleotide = when (this) {
        A -> U
        C -> G
        G -> C
        U -> A
    }

    companion object {
        fun fromChar(symbol: Char): Nucleotide = when (symbol.uppercaseChar()) {
            A.symbol -> A
            C.symbol -> C
            G.symbol -> G
            U.symbol -> U
            else -> throw IllegalArgumentException("Invalid nucleotide '$symbol'. Expected one of A, C, G, or U.")
        }

        fun fromBits(bits: Byte): Nucleotide = when (bits) {
            A.bits -> A
            C.bits -> C
            G.bits -> G
            U.bits -> U
            else -> throw IllegalArgumentException("Nucleotide bits must be a 2-bit value in the range 0..3, but was $bits.")
        }

        fun fromBits(bits: Int): Nucleotide {
            require(bits in 0..3) {
                "Nucleotide bits must be a 2-bit value in the range 0..3, but was $bits."
            }

            return fromBits(bits.toByte())
        }
    }
}

