package life.sim.biology.proteins

import life.sim.biology.interactions.MoleculeId
import life.sim.biology.molecules.Polypeptide
import life.sim.biology.primitives.NucleotideSequence

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
