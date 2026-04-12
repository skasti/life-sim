package life.sim.biology.primitives

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
    fun `nucleotides expose the expected symbols`() {
        assertEquals('A', Nucleotide.A.symbol)
        assertEquals('C', Nucleotide.C.symbol)
        assertEquals('G', Nucleotide.G.symbol)
        assertEquals('U', Nucleotide.U.symbol)
    }

    @Test
    fun `from char resolves each nucleotide`() {
        assertEquals(Nucleotide.A, Nucleotide.fromChar('A'))
        assertEquals(Nucleotide.C, Nucleotide.fromChar('c'))
        assertEquals(Nucleotide.G, Nucleotide.fromChar('G'))
        assertEquals(Nucleotide.U, Nucleotide.fromChar('u'))
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

    @Test
    fun `from char rejects invalid symbols`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Nucleotide.fromChar('T')
        }

        assertEquals("Invalid nucleotide 'T'. Expected one of A, C, G, or U.", exception.message)
    }
}


