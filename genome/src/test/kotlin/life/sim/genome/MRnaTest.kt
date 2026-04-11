package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MRnaTest {
    @Test
    fun `empty mRNA has no nucleotides`() {
        val mrna = MRna.empty()

        assertTrue(mrna.isEmpty())
        assertEquals(0, mrna.size)
        assertEquals("", mrna.toString())
        assertEquals(NucleotideSequence.empty(), mrna.toNucleotideSequence())
    }

    @Test
    fun `mRNA can be created from a nucleotide sequence`() {
        val sequence = NucleotideSequence.of("AUGC")
        val mrna = MRna.of(sequence)

        assertEquals(sequence, mrna.toNucleotideSequence())
        assertEquals("AUGC", mrna.toString())
    }

    @Test
    fun `mRNA can be parsed from text`() {
        val mrna = MRna.parse("augc")

        assertEquals(NucleotideSequence.of("AUGC"), mrna.toNucleotideSequence())
        assertEquals("AUGC", mrna.toString())
    }

    @Test
    fun `mRNA compares by wrapped sequence content`() {
        assertEquals(MRna.of("AUGC"), MRna.of(NucleotideSequence.of("AUGC")))
    }

    @Test
    fun `mRNA parse rejects invalid symbols`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            MRna.parse("AUTG")
        }

        assertEquals(
            "Invalid nucleotide 'T' at index 2. Expected one of A, C, G, or U.",
            exception.message,
        )
    }
}

