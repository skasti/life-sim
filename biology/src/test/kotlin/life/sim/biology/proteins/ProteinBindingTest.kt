package life.sim.biology.proteins

import life.sim.biology.interactions.BindingMatcher
import life.sim.biology.interactions.Bond
import life.sim.biology.interactions.BondRegistry
import life.sim.biology.interactions.MoleculeId
import life.sim.biology.interactions.SiteEndpoint
import life.sim.biology.interactions.WholeMoleculeEndpoint
import life.sim.biology.interactions.bindingSurface
import life.sim.biology.molecules.MRna
import life.sim.biology.molecules.Polypeptide
import life.sim.biology.primitives.NucleotideSequence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProteinBindingTest {
    @Test
    fun `interpreted binder can create a runtime bond and register it`() {
        val binder = interpretedBinderFrom("AAKRGKAA")
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(20))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(99),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNotNull(bond)
        assertEquals(1, registry.size)
        assertEquals(bond, registry.toList().single())
        assertTrue(bond.strength in 0.0..1.0)
        assertEquals(0.05, bond.decayPerTick, absoluteTolerance = 1.0e-9)
        assertEquals(MoleculeId(99), bond.left.moleculeId)
        assertEquals(MoleculeId(20), bond.right.moleculeId)
    }

    @Test
    fun `tryBind rejects and leaves registry unchanged when no complementary site exists`() {
        val binder = interpretedBinderFrom("AAKRGKAA")
        val target = MRna.of("AAAAAAA").bindingSurface(MoleculeId(30))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(101),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertTrue(registry.isEmpty())
    }

    @Test
    fun `tryBind rejects when affinity normalizes to inactive bond strength`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.0)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(32))
        val registry = BondRegistry()

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(203),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertTrue(registry.isEmpty())
    }

    @Test
    fun `different amino acid context yields different binder target and different match site`() {
        val firstBinder = interpretedBinderFrom("AAKRGKAA")
        val secondBinder = interpretedBinderFrom("AAKRGKDA")
        val target = MRna.of(buildTargetWithOnlySecondMatch(secondBinder.bindingPattern)).bindingSurface(MoleculeId(31))
        val registry = BondRegistry()

        val firstBond = ProteinBinding.tryBind(
            proteinId = MoleculeId(201),
            binder = firstBinder,
            target = target,
            registry = registry,
        )
        val secondBond = ProteinBinding.tryBind(
            proteinId = MoleculeId(202),
            binder = secondBinder,
            target = target,
            registry = registry,
        )

        assertNull(firstBond)
        assertNotNull(secondBond)
        assertEquals(1, registry.size)
    }

    @Test
    fun `weaker binder does not displace stronger overlapping bond`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.4)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(40))
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)!!
        val strongerOccupant = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(500)),
            right = SiteEndpoint(targetSite),
            strength = 0.9,
            decayPerTick = 0.05,
        )
        val registry = BondRegistry(listOf(strongerOccupant))

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(501),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertEquals(listOf(strongerOccupant), registry.toList())
    }

    @Test
    fun `stronger binder displaces weaker overlapping bond`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.9)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(41))
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)!!
        val weakerOccupant = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(510)),
            right = SiteEndpoint(targetSite),
            strength = 0.3,
            decayPerTick = 0.05,
        )
        val registry = BondRegistry(listOf(weakerOccupant))

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(511),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNotNull(bond)
        assertEquals(listOf(bond), registry.toList())
    }

    @Test
    fun `non-overlapping bonds remain when overlapping weaker bond is displaced`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.8)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CCAA").bindingSurface(MoleculeId(42))
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)!!
        val overlappingWeak = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(520)),
            right = SiteEndpoint(targetSite),
            strength = 0.4,
            decayPerTick = 0.05,
        )
        val nonOverlapping = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(521)),
            right = SiteEndpoint(target.site(targetSite.range.endExclusive, target.length)),
            strength = 0.9,
            decayPerTick = 0.05,
        )
        val registry = BondRegistry(listOf(overlappingWeak, nonOverlapping))

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(522),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNotNull(bond)
        assertEquals(2, registry.size)
        assertTrue(registry.toList().contains(nonOverlapping))
        assertTrue(registry.toList().contains(bond))
    }

    @Test
    fun `equal strength overlap keeps existing bond deterministically`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.6)
        val target = MRna.of("UUG${asText(binder.bindingPattern.complement())}CC").bindingSurface(MoleculeId(43))
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)!!
        val existing = Bond(
            left = WholeMoleculeEndpoint(MoleculeId(530)),
            right = SiteEndpoint(targetSite),
            strength = 0.6,
            decayPerTick = 0.05,
        )
        val registry = BondRegistry(listOf(existing))

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(531),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNull(bond)
        assertEquals(listOf(existing), registry.toList())
    }

    @Test
    fun `multiple overlapping weaker occupants are displaced together`() {
        val binder = interpretedBinderFrom("AAKRGKAA").copy(affinity = 0.95)
        val target = MRna.of("UU${asText(binder.bindingPattern.complement())}GG").bindingSurface(MoleculeId(44))
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)!!

        val overlappingFirst = Bond(
            left = SiteEndpoint(target.site(targetSite.range.start, targetSite.range.start + 2)),
            right = WholeMoleculeEndpoint(MoleculeId(540)),
            strength = 0.3,
            decayPerTick = 0.05,
        )
        val overlappingSecond = Bond(
            left = SiteEndpoint(target.site(targetSite.range.endExclusive - 2, targetSite.range.endExclusive)),
            right = WholeMoleculeEndpoint(MoleculeId(541)),
            strength = 0.4,
            decayPerTick = 0.05,
        )
        val registry = BondRegistry(listOf(overlappingFirst, overlappingSecond))

        val bond = ProteinBinding.tryBind(
            proteinId = MoleculeId(542),
            binder = binder,
            target = target,
            registry = registry,
        )

        assertNotNull(bond)
        assertEquals(listOf(bond), registry.toList())
    }

    private fun interpretedBinderFrom(sequence: String): SequenceBinder {
        val domain = ProteinInterpreter.interpret(Polypeptide.of(sequence)).single()
        return domain.capabilities.single() as SequenceBinder
    }

    private fun buildTargetWithOnlySecondMatch(pattern: NucleotideSequence): String {
        return "AAAA${asText(pattern.complement())}AAAA"
    }

    private fun asText(sequence: NucleotideSequence): String =
        sequence.toList().joinToString(separator = "") { it.symbol.toString() }
}
