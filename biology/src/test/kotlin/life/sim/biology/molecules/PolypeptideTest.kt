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
    fun `parse rejects invalid amino acids`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Polypeptide.parse("MKZ")
        }

        assertEquals(
            "Invalid amino-acid 'Z'. Expected one of A, R, N, D, C, E, Q, G, H, I, L, K, M, F, P, S, T, W, Y, V.",
            exception.message,
        )
    }
}
