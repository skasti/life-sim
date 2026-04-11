package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TRnaTest {
    @Test
    fun `empty tRNA has no nucleotides`() {
        val trna = TRna.empty()

        assertTrue(trna.isEmpty())
        assertEquals(0, trna.size)
        assertEquals("", trna.toString())
        assertEquals(NucleotideSequence.empty(), trna.toNucleotideSequence())
    }

    @Test
    fun `tRNA can be created from a nucleotide sequence`() {
        val sequence = NucleotideSequence.of("CGAU")
        val trna = TRna.of(sequence)

        assertEquals(sequence, trna.toNucleotideSequence())
        assertEquals("CGAU", trna.toString())
    }

    @Test
    fun `tRNA can be parsed from text`() {
        val trna = TRna.parse("cgau")

        assertEquals(NucleotideSequence.of("CGAU"), trna.toNucleotideSequence())
        assertEquals("CGAU", trna.toString())
    }

    @Test
    fun `tRNA compares by wrapped sequence content`() {
        assertEquals(TRna.of("CGAU"), TRna.of(NucleotideSequence.of("CGAU")))
    }

    @Test
    fun `tRNA parse rejects invalid symbols`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            TRna.parse("CGTX")
        }

        assertEquals(
            "Invalid nucleotide 'T' at index 2. Expected one of A, C, G, or U.",
            exception.message,
        )
    }
}

