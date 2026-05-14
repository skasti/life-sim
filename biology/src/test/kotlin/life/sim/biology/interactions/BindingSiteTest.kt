package life.sim.biology.interactions

import life.sim.biology.molecules.Dna
import life.sim.biology.molecules.MRna
import life.sim.biology.molecules.TRna
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceRange

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BindingSiteTest {
    @Test
    fun `mrna binding surface creates single strand sites`() {
        val surface = MRna.of("AUGCUA").bindingSurface(EntityId(1))
        val site = surface.site(1, 4)

        assertEquals(EntityId(1), site.moleculeId)
        assertEquals(BindingStrand.SINGLE, site.strand)
        assertEquals(NucleotideSequence.of("UGC"), site.sequence)
    }

    @Test
    fun `dna exposes forward and reverse binding surfaces`() {
        val dna = Dna.of("AUGC", "UACG")

        assertEquals(NucleotideSequence.parse(">AUGC>"), dna.forwardBindingSurface(EntityId(2)).sequence)
        assertEquals(NucleotideSequence.parse("<UACG<"), dna.reverseBindingSurface(EntityId(2)).sequence)
    }

    @Test
    fun `trna binding surface uses a single strand`() {
        val surface = TRna.of("CGAU").bindingSurface(EntityId(3))

        assertEquals(BindingStrand.SINGLE, surface.strand)
        assertEquals(NucleotideSequence.of("CGAU"), surface.sequence)
    }

    @Test
    fun `sites overlap only on the same molecule and strand`() {
        val mrna = MRna.of("AUGCUA")
        val first = mrna.bindingSurface(EntityId(4)).site(1, 4)
        val overlapping = mrna.bindingSurface(EntityId(4)).site(3, 6)
        val sameRangeDifferentMolecule = mrna.bindingSurface(EntityId(5)).site(3, 6)
        val reverseDnaSite = Dna.of("AUGCUA", "UACGAU").reverseBindingSurface(EntityId(4)).site(3, 6)

        assertTrue(first.overlaps(overlapping))
        assertFalse(first.overlaps(sameRangeDifferentMolecule))
        assertFalse(first.overlaps(reverseDnaSite))
    }

    @Test
    fun `empty sites never overlap`() {
        val surface = MRna.of("AUGCUA").bindingSurface(EntityId(7))
        val empty = surface.site(5, 5)
        val occupied = surface.site(4, 6)

        assertFalse(empty.overlaps(occupied))
        assertFalse(occupied.overlaps(empty))
        assertFalse(empty.overlaps(empty))
    }

    @Test
    fun `site rejects ranges beyond the surface length`() {
        val surface = MRna.of("AUGC").bindingSurface(EntityId(6))

        val exception = assertFailsWith<IllegalArgumentException> {
            BindingSite(surface, SequenceRange(1, 5))
        }

        assertEquals("Binding site range SequenceRange(start=1, endExclusive=5) exceeds surface length 4.", exception.message)
    }
}

