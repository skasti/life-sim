package life.sim.biology.interactions

import life.sim.biology.molecules.MRna
import life.sim.biology.primitives.NucleotideSequence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BindingMatcherTest {
    @Test
    fun `complementary match start returns first matching index`() {
        val pattern = NucleotideSequence.of("AUG")
        val target = NucleotideSequence.of("CCUACUAC")

        assertEquals(2, BindingMatcher.complementaryMatchStart(pattern, target))
    }

    @Test
    fun `complementary match start returns minus one when no match exists`() {
        assertEquals(-1, BindingMatcher.complementaryMatchStart(NucleotideSequence.of("ACG"), NucleotideSequence.of("CGUAAA")))
    }

    @Test
    fun `complementary match site returns a binding site on the target surface`() {
        val surface = MRna.of("CCUACUAC").bindingSurface(MoleculeId(7))

        val site = BindingMatcher.complementaryMatchSite(NucleotideSequence.of("AUG"), surface)

        assertNotNull(site)
        assertEquals(MoleculeId(7), site.moleculeId)
        assertEquals(2, site.range.start)
        assertEquals(5, site.range.endExclusive)
        assertEquals(NucleotideSequence.of("UAC"), site.sequence)
    }

    @Test
    fun `complementary match site returns null when no match exists`() {
        val surface = MRna.of("CGUAAA").bindingSurface(MoleculeId(8))

        assertNull(BindingMatcher.complementaryMatchSite(NucleotideSequence.of("ACG"), surface))
    }
}

