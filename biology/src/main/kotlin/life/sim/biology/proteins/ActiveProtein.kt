package life.sim.biology.proteins

import life.sim.biology.interactions.MoleculeId
import life.sim.biology.molecules.Polypeptide

/**
 * First-class runtime protein molecule with stable identity and preserved interpretation output.
 */
data class ActiveProtein(
    val moleculeId: MoleculeId,
    val source: Polypeptide,
    val domains: List<ProteinDomain>,
    val capabilities: List<MolecularCapability>,
) {
    companion object {
        /**
         * Creates an [ActiveProtein] from already interpreted [domains] and flattens capability access.
         */
        fun fromDomains(
            moleculeId: MoleculeId,
            source: Polypeptide,
            domains: List<ProteinDomain>,
        ): ActiveProtein {
            val immutableDomains = domains.toList()

            return ActiveProtein(
                moleculeId = moleculeId,
                source = source,
                domains = immutableDomains,
                capabilities = immutableDomains.flatMap(ProteinDomain::capabilities),
            )
        }

        /**
         * Interprets [source] and returns a first-class runtime protein molecule.
         */
        fun interpret(
            moleculeId: MoleculeId,
            source: Polypeptide,
        ): ActiveProtein = fromDomains(
            moleculeId = moleculeId,
            source = source,
            domains = ProteinInterpreter.interpret(source),
        )
    }
}
