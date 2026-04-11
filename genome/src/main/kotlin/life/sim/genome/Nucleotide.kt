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
enum class Nucleotide(val bits: Byte) {
    A(0b00),
    C(0b01),
    G(0b10),
    U(0b11);

    fun complement(): Nucleotide = when (this) {
        A -> U
        C -> G
        G -> C
        U -> A
    }

    companion object {
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

