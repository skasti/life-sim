package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DnaTest {
    @Test
    fun `dna created from one strand infers the complementary reverse strand`() {
        val dna = Dna.of(NucleotideSequence.of("AUGGGCCAUGAACCGG"))

        assertEquals(NucleotideSequence.of("AUGGGCCAUGAACCGG"), dna.forward)
        assertEquals(NucleotideSequence.of("UACCCGGUACUUGGCC"), dna.reverse)
        assertEquals("AUGGGCCAUGAACCGG\nUACCCGGUACUUGGCC", dna.toString())
    }

    @Test
    fun `dna can be created from two strand strings`() {
        val dna = Dna.of("AUGC", "UACG")

        assertEquals(NucleotideSequence.of("AUGC"), dna.forward)
        assertEquals(NucleotideSequence.of("UACG"), dna.reverse)
        assertEquals("AUGC\nUACG", dna.toString())
    }

    @Test
    fun `dna can be parsed from two lines`() {
        val dna = Dna.parse("AUGGGCCAUGAACCGG\nUACCCGGUACUUGGCC")

        assertEquals(NucleotideSequence.of("AUGGGCCAUGAACCGG"), dna.forward)
        assertEquals(NucleotideSequence.of("UACCCGGUACUUGGCC"), dna.reverse)
        assertEquals("AUGGGCCAUGAACCGG\nUACCCGGUACUUGGCC", dna.toString())
    }

    @Test
    fun `dna parse infers the complement when only one line is present`() {
        val dna = Dna.parse("AUGGGCCAUGAACCGG")

        assertEquals("AUGGGCCAUGAACCGG\nUACCCGGUACUUGGCC", dna.toString())
    }

    @Test
    fun `dna parse supports crlf line endings`() {
        val dna = Dna.parse("AUGG\r\nUACC")

        assertEquals(NucleotideSequence.of("AUGG"), dna.forward)
        assertEquals(NucleotideSequence.of("UACC"), dna.reverse)
    }

    @Test
    fun `dna exposes size and emptiness based on strand length`() {
        val dna = Dna.of(NucleotideSequence.of("AUGG"))

        assertFalse(dna.isEmpty())
        assertEquals(4, dna.size)
        assertTrue(Dna.empty().isEmpty())
        assertEquals(0, Dna.empty().size)
    }

    @Test
    fun `dna parse rejects more than two lines`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Dna.parse("A\nU\nC")
        }

        assertEquals("DNA text must contain one or two lines, but had 3.", exception.message)
    }

    @Test
    fun `dna rejects strand pairs with different lengths`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Dna.of(NucleotideSequence.of("AUG"), NucleotideSequence.of("UA"))
        }

        assertEquals("DNA forward and reverse strands must have the same length, but were 3 and 2.", exception.message)
    }

    @Test
    fun `dna rejects strand pairs that are not complementary`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Dna.of(NucleotideSequence.of("AUGG"), NucleotideSequence.of("UACG"))
        }

        assertEquals("DNA forward and reverse strands must be complementary at index 3, but found G and G.", exception.message)
    }

    @Test
    fun `dna string overload rejects strand pairs with different lengths`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Dna.of("AUG", "UA")
        }

        assertEquals("DNA forward and reverse strands must have the same length, but were 3 and 2.", exception.message)
    }
}

