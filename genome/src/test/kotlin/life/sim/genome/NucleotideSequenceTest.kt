package life.sim.genome

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NucleotideSequenceTest {
    @Test
    fun `empty sequence has no nucleotides`() {
        val sequence = NucleotideSequence.empty()

        assertTrue(sequence.isEmpty())
        assertEquals(0, sequence.size)
        assertContentEquals(emptyList(), sequence.toList())
        assertEquals(SequenceDirection.FORWARD, sequence.direction)
        assertEquals(">>", sequence.toString())
    }

    @Test
    fun `empty sequence complement is empty`() {
        val sequence = NucleotideSequence.empty()

        assertEquals(NucleotideSequence.empty(SequenceDirection.BACKWARD), sequence.complement())
    }

    @Test
    fun `empty sequence reversed is empty`() {
        val sequence = NucleotideSequence.empty()

        assertEquals(NucleotideSequence.empty(SequenceDirection.BACKWARD), sequence.reversed())
    }

    @Test
    fun `sequence preserves nucleotide order`() {
        val sequence = NucleotideSequence.of(Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.U)

        assertFalse(sequence.isEmpty())
        assertEquals(4, sequence.size)
        assertEquals(Nucleotide.A, sequence[0])
        assertEquals(Nucleotide.C, sequence[1])
        assertEquals(Nucleotide.G, sequence[2])
        assertEquals(Nucleotide.U, sequence[3])
        assertContentEquals(
            listOf(Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.U),
            sequence.toList(),
        )
        assertEquals(SequenceDirection.FORWARD, sequence.direction)
        assertEquals(">ACGU>", sequence.toString())
    }

    @Test
    fun `parse creates a forward sequence from directional nucleotide text`() {
        val sequence = NucleotideSequence.parse(">ACGU>")

        assertEquals(NucleotideSequence.of(Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.U), sequence)
    }

    @Test
    fun `parse accepts lowercase directional nucleotide text`() {
        val sequence = NucleotideSequence.parse(">acgu>")

        assertEquals(NucleotideSequence.of(Nucleotide.A, Nucleotide.C, Nucleotide.G, Nucleotide.U), sequence)
        assertEquals(">ACGU>", sequence.toString())
    }

    @Test
    fun `parse creates a backward sequence from directional nucleotide text`() {
        val sequence = NucleotideSequence.parse("<ACGU<")

        assertEquals(SequenceDirection.BACKWARD, sequence.direction)
        assertEquals("<ACGU<", sequence.toString())
    }

    @Test
    fun `parse returns an empty forward sequence for empty text`() {
        assertEquals(NucleotideSequence.empty(), NucleotideSequence.parse(""))
    }

    @Test
    fun `sequence parse and to string round trip`() {
        val text = ">AUGCUGAA>"

        assertEquals(text, NucleotideSequence.parse(text).toString())
    }

    @Test
    fun `sequence can be iterated`() {
        val sequence = NucleotideSequence.of(Nucleotide.G, Nucleotide.U, Nucleotide.A)

        assertContentEquals(listOf(Nucleotide.G, Nucleotide.U, Nucleotide.A), sequence.toList())
        assertContentEquals(listOf(Nucleotide.G, Nucleotide.U, Nucleotide.A), sequence.asSequence().toList())
    }

    @Test
    fun `sequence compares by wrapped nucleotide content`() {
        val left = NucleotideSequence.of(Nucleotide.A, Nucleotide.U, Nucleotide.G)
        val right = NucleotideSequence.from(listOf(Nucleotide.A, Nucleotide.U, Nucleotide.G))

        assertEquals(left, right)
    }

    @Test
    fun `sequence complement preserves order while complementing each nucleotide`() {
        val sequence = NucleotideSequence.of(Nucleotide.A, Nucleotide.U, Nucleotide.G, Nucleotide.C)
        assertContentEquals(
            listOf(Nucleotide.U, Nucleotide.A, Nucleotide.C, Nucleotide.G),
            sequence.complement().toList(),
        )
        assertEquals("<UACG<", sequence.complement().toString())
    }

    @Test
    fun `sequence reversed preserves nucleotide values while reversing order`() {
        val sequence = NucleotideSequence.of(Nucleotide.A, Nucleotide.U, Nucleotide.G, Nucleotide.C)
        assertContentEquals(
            listOf(Nucleotide.C, Nucleotide.G, Nucleotide.U, Nucleotide.A),
            sequence.reversed().toList(),
        )
        assertEquals("<CGUA<", sequence.reversed().toString())
    }

    @Test
    fun `sequence complement is reversible`() {
        val sequence = NucleotideSequence.of(Nucleotide.G, Nucleotide.U, Nucleotide.A, Nucleotide.C)

        assertEquals(sequence, sequence.complement().complement())
    }

    @Test
    fun `sequence reversed is reversible`() {
        val sequence = NucleotideSequence.of(Nucleotide.G, Nucleotide.U, Nucleotide.A, Nucleotide.C)

        assertEquals(sequence, sequence.reversed().reversed())
    }

    @Test
    fun `sequence parse and reversed to string match text reversed`() {
        val text = ">AUGCUGAA>"

        assertEquals("<${"AUGCUGAA".reversed()}<", NucleotideSequence.parse(text).reversed().toString())
    }

    @Test
    fun `sequence copies input list on creation`() {
        val source = mutableListOf(Nucleotide.A, Nucleotide.C)
        val sequence = NucleotideSequence.from(source)

        source[0] = Nucleotide.U
        source.add(Nucleotide.G)

        assertContentEquals(listOf(Nucleotide.A, Nucleotide.C), sequence.toList())
    }

    @Test
    fun `to list returns a safe copy`() {
        val sequence = NucleotideSequence.of(Nucleotide.C, Nucleotide.G)
        val copy = sequence.toList().toMutableList()

        copy[0] = Nucleotide.A
        copy.add(Nucleotide.U)

        assertContentEquals(listOf(Nucleotide.C, Nucleotide.G), sequence.toList())
    }

    @Test
    fun `list helper creates an equivalent nucleotide sequence`() {
        val sequence = listOf(Nucleotide.A, Nucleotide.U, Nucleotide.C).toNucleotideSequence()

        assertEquals(NucleotideSequence.of(Nucleotide.A, Nucleotide.U, Nucleotide.C), sequence)
    }

    @Test
    fun `list helper copies the source list`() {
        val source = mutableListOf(Nucleotide.A, Nucleotide.C)
        val sequence = source.toNucleotideSequence()

        source[0] = Nucleotide.G
        source.add(Nucleotide.U)

        assertContentEquals(listOf(Nucleotide.A, Nucleotide.C), sequence.toList())
    }

    @Test
    fun `parse rejects invalid symbols with index information`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            NucleotideSequence.parse(">ACXT>")
        }

        assertEquals(
            "Invalid nucleotide 'X' at index 2. Expected one of A, C, G, or U.",
            exception.message,
        )
    }

    @Test
    fun `parse rejects mismatched direction markers`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            NucleotideSequence.parse(">ACGU<")
        }

        assertEquals(
            "Nucleotide sequence text must be surrounded by matching direction markers or contain no markers, but was '>ACGU<'.",
            exception.message,
        )
    }
}

