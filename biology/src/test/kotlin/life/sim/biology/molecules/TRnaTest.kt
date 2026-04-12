package life.sim.biology.molecules

import life.sim.biology.interactions.BindingMatcher
import life.sim.biology.primitives.NucleotideSequence

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
        assertEquals(">>", trna.toString())
        assertEquals(NucleotideSequence.empty(), trna.toNucleotideSequence())
    }

    @Test
    fun `tRNA can be created from a nucleotide sequence`() {
        val sequence = NucleotideSequence.of("CGAU")
        val trna = TRna.of(sequence)

        assertEquals(sequence, trna.toNucleotideSequence())
        assertEquals(">CGAU>", trna.toString())
    }

    @Test
    fun `tRNA can be parsed from text`() {
        val trna = TRna.parse(">cgau>")

        assertEquals(NucleotideSequence.of("CGAU"), trna.toNucleotideSequence())
        assertEquals(">CGAU>", trna.toString())
    }

    @Test
    fun `tRNA compares by wrapped sequence content`() {
        assertEquals(TRna.of("CGAU"), TRna.of(NucleotideSequence.of("CGAU")))
    }

    @Test
    fun `scan returns zero for an empty tRNA`() {
        assertEquals(0, TRna.empty().scan(NucleotideSequence.of("AUGC")))
    }

    @Test
    fun `scan returns the start position of a complementary match`() {
        val trna = TRna.of("CGAU")

        assertEquals(0, trna.scan(NucleotideSequence.of("GCUA")))
    }

    @Test
    fun `scan returns the first complementary matching position`() {
        val trna = TRna.of("AUG")

        assertEquals(2, trna.scan(NucleotideSequence.of("CCUACUAC")))
    }

    @Test
    fun `scan uses the same complementary matching rules as the shared binding matcher`() {
        val trna = TRna.of("AUG")
        val target = NucleotideSequence.of("CCUACUAC")

        assertEquals(
            BindingMatcher.complementaryMatchStart(trna.toNucleotideSequence(), target),
            trna.scan(target),
        )
    }

    @Test
    fun `scan returns minus one when no complementary match exists`() {
        val trna = TRna.of("ACG")

        assertEquals(-1, trna.scan(NucleotideSequence.of("CGUAAA")))
    }

    @Test
    fun `scan returns minus one when tRNA is longer than the scanned sequence`() {
        val trna = TRna.of("AUGCU")

        assertEquals(-1, trna.scan(NucleotideSequence.of("AUG")))
    }

    @Test
    fun `scan does not treat exact equality as a match without complementarity`() {
        val trna = TRna.of("AUG")

        assertEquals(-1, trna.scan(NucleotideSequence.of("CCAUGAUG")))
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


