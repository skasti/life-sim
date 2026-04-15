package life.sim.biology.molecules

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PolypeptideTest {
    @Test
    fun `parse and stringify preserve residue order`() {
        val peptide = Polypeptide.parse("MKRGLY")

        assertEquals(6, peptide.size)
        assertEquals(AminoAcid.M, peptide[0])
        assertEquals(AminoAcid.Y, peptide[5])
        assertEquals("MKRGLY", peptide.toString())
    }

    @Test
    fun `subsequence returns requested slice`() {
        val peptide = Polypeptide.of("MKRGLY")

        assertEquals("RGL", peptide.subsequence(2, 5).toString())
    }

    @Test
    fun `subsequence is not affected by mutations to original source list`() {
        val residues = mutableListOf(
            AminoAcid.M,
            AminoAcid.K,
            AminoAcid.R,
            AminoAcid.G,
            AminoAcid.L,
            AminoAcid.Y,
        )
        val peptide = Polypeptide.from(residues)
        val subsequence = peptide.subsequence(1, 4)

        residues[2] = AminoAcid.A

        assertEquals("MKRGLY", peptide.toString())
        assertEquals("KRG", subsequence.toString())
    }

    @Test
    fun `parse rejects invalid amino acids`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Polypeptide.parse("MKZ")
        }

        assertEquals(
            "Invalid amino-acid 'Z' at index 2. Invalid amino-acid 'Z'. Expected one of A, R, N, D, C, E, Q, G, H, I, L, K, M, F, P, S, T, W, Y, V.",
            exception.message,
        )
    }

    @Test
    fun `parse rejects trailing whitespace instead of trimming`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Polypeptide.parse("MKR ")
        }

        assertEquals(
            "Invalid amino-acid ' ' at index 3. Invalid amino-acid ' '. Expected one of A, R, N, D, C, E, Q, G, H, I, L, K, M, F, P, S, T, W, Y, V.",
            exception.message,
        )
    }
}
