package life.sim.biology.proteins

import life.sim.biology.interactions.MoleculeId
import life.sim.biology.molecules.Polypeptide
import life.sim.biology.primitives.NucleotideSequence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame

class ActiveProteinTest {
    @Test
    fun `fromDomains keeps molecule id source and interpreted domains while flattening capabilities`() {
        val source = Polypeptide.of("AAKRGKTTHEMH")
        val domains = listOf(
            ProteinDomain(
                name = "BinderDomain",
                startInclusive = 2,
                endExclusive = 6,
                motif = "KRGK",
                capabilities = listOf(
                    SequenceBinder(
                        bindingPattern = NucleotideSequence.of("ACGUAC"),
                        affinity = 0.7,
                        specificity = 0.4,
                    ),
                ),
            ),
            ProteinDomain(
                name = "CutterDomain",
                startInclusive = 8,
                endExclusive = 12,
                motif = "HEMH",
                capabilities = listOf(Cutter(catalyticStrength = 0.8)),
            ),
        )

        val activeProtein = ActiveProtein.fromDomains(
            moleculeId = MoleculeId(42),
            source = source,
            domains = domains,
        )

        assertEquals(MoleculeId(42), activeProtein.moleculeId)
        assertEquals(source, activeProtein.source)
        assertEquals(domains, activeProtein.domains)
        assertEquals(domains.flatMap(ProteinDomain::capabilities), activeProtein.capabilities)
    }

    @Test
    fun `interpret preserves interpreter output and derived flattened capabilities`() {
        val source = Polypeptide.of("AAKRGKTTHEMHPPW")

        val activeProtein = ActiveProtein.interpret(
            moleculeId = MoleculeId(9),
            source = source,
        )

        assertEquals(MoleculeId(9), activeProtein.moleculeId)
        assertEquals(source, activeProtein.source)
        assertEquals(ProteinInterpreter.interpret(source), activeProtein.domains)
        assertEquals(activeProtein.domains.flatMap(ProteinDomain::capabilities), activeProtein.capabilities)
    }

    @Test
    fun `fromDomains copies input domain list to keep domains and capabilities consistent`() {
        val source = Polypeptide.of("AAKRGKTTHEMH")
        val mutableDomains = mutableListOf(
            ProteinDomain(
                name = "BinderDomain",
                startInclusive = 2,
                endExclusive = 6,
                motif = "KRGK",
                capabilities = listOf(
                    SequenceBinder(
                        bindingPattern = NucleotideSequence.of("ACGUAC"),
                        affinity = 0.7,
                        specificity = 0.4,
                    ),
                ),
            ),
        )

        val activeProtein = ActiveProtein.fromDomains(
            moleculeId = MoleculeId(77),
            source = source,
            domains = mutableDomains,
        )

        mutableDomains.add(
            ProteinDomain(
                name = "CutterDomain",
                startInclusive = 8,
                endExclusive = 12,
                motif = "HEMH",
                capabilities = listOf(Cutter(catalyticStrength = 0.8)),
            ),
        )

        assertNotSame(mutableDomains, activeProtein.domains)
        assertEquals(1, activeProtein.domains.size)
        assertEquals(
            activeProtein.domains.flatMap(ProteinDomain::capabilities),
            activeProtein.capabilities,
        )
    }

    @Test
    fun `fromDomains copies domain capability lists to keep flattened capabilities consistent`() {
        val source = Polypeptide.of("AAKRGKTTHEMH")
        val mutableCapabilities = mutableListOf<MolecularCapability>(
            SequenceBinder(
                bindingPattern = NucleotideSequence.of("ACGUAC"),
                affinity = 0.7,
                specificity = 0.4,
            ),
        )
        val mutableDomains = listOf(
            ProteinDomain(
                name = "BinderDomain",
                startInclusive = 2,
                endExclusive = 6,
                motif = "KRGK",
                capabilities = mutableCapabilities,
            ),
        )

        val activeProtein = ActiveProtein.fromDomains(
            moleculeId = MoleculeId(88),
            source = source,
            domains = mutableDomains,
        )

        mutableCapabilities.add(Cutter(catalyticStrength = 0.8))

        assertNotSame(mutableCapabilities, activeProtein.domains.single().capabilities)
        assertEquals(1, activeProtein.domains.single().capabilities.size)
        assertEquals(
            activeProtein.domains.flatMap(ProteinDomain::capabilities),
            activeProtein.capabilities,
        )
    }

    @Test
    fun `fromDomains exposes unmodifiable domain and capability lists`() {
        val source = Polypeptide.of("AAKRGKTTHEMH")
        val activeProtein = ActiveProtein.fromDomains(
            moleculeId = MoleculeId(99),
            source = source,
            domains = listOf(
                ProteinDomain(
                    name = "BinderDomain",
                    startInclusive = 2,
                    endExclusive = 6,
                    motif = "KRGK",
                    capabilities = listOf(
                        SequenceBinder(
                            bindingPattern = NucleotideSequence.of("ACGUAC"),
                            affinity = 0.7,
                            specificity = 0.4,
                        ),
                    ),
                ),
            ),
        )

        val exposedDomains = activeProtein.domains as MutableList<ProteinDomain>
        assertFailsWith<UnsupportedOperationException> {
            exposedDomains.add(
                ProteinDomain(
                    name = "CutterDomain",
                    startInclusive = 8,
                    endExclusive = 12,
                    motif = "HEMH",
                    capabilities = listOf(Cutter(catalyticStrength = 0.8)),
                ),
            )
        }

        val exposedCapabilities = activeProtein.domains.single().capabilities as MutableList<MolecularCapability>
        assertFailsWith<UnsupportedOperationException> {
            exposedCapabilities.add(Cutter(catalyticStrength = 0.6))
        }

        assertEquals(1, activeProtein.domains.size)
        assertEquals(
            activeProtein.domains.flatMap(ProteinDomain::capabilities),
            activeProtein.capabilities,
        )
    }
}
