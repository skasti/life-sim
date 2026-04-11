package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NucleotideTest {
    @Test
    fun `nucleotides expose the expected 2-bit encoding`() {
        assertEquals(0b00.toByte(), Nucleotide.A.bits)
        assertEquals(0b01.toByte(), Nucleotide.C.bits)
        assertEquals(0b10.toByte(), Nucleotide.G.bits)
        assertEquals(0b11.toByte(), Nucleotide.U.bits)
    }

    @Test
    fun `from byte bits resolves each nucleotide`() {
        Nucleotide.entries.forEach { nucleotide ->
            assertEquals(nucleotide, Nucleotide.fromBits(nucleotide.bits))
        }
    }

    @Test
    fun `from int bits resolves each nucleotide`() {
        Nucleotide.entries.forEach { nucleotide ->
            assertEquals(nucleotide, Nucleotide.fromBits(nucleotide.bits.toInt()))
        }
    }

    @Test
    fun `complement returns the expected binding partner`() {
        assertEquals(Nucleotide.U, Nucleotide.A.complement())
        assertEquals(Nucleotide.G, Nucleotide.C.complement())
        assertEquals(Nucleotide.C, Nucleotide.G.complement())
        assertEquals(Nucleotide.A, Nucleotide.U.complement())
    }

    @Test
    fun `complement is reversible`() {
        Nucleotide.entries.forEach { nucleotide ->
            assertEquals(nucleotide, nucleotide.complement().complement())
        }
    }

    @Test
    fun `from bits rejects values outside the 2-bit range`() {
        assertFailsWith<IllegalArgumentException> {
            Nucleotide.fromBits((-1).toByte())
        }

        assertFailsWith<IllegalArgumentException> {
            Nucleotide.fromBits(4.toByte())
        }

        assertFailsWith<IllegalArgumentException> {
            Nucleotide.fromBits(-1)
        }

        assertFailsWith<IllegalArgumentException> {
            Nucleotide.fromBits(4)
        }
    }
}

