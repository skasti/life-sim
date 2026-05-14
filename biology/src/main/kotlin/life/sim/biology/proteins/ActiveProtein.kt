package life.sim.biology.proteins

import life.sim.biology.interactions.EntityId
import life.sim.biology.molecules.Polypeptide
import java.util.Collections
import kotlin.ConsistentCopyVisibility

/**
 * First-class runtime protein molecule with stable identity and preserved interpretation output.
 */
@ConsistentCopyVisibility
data class ActiveProtein private constructor(
    val moleculeId: EntityId,
    val source: Polypeptide,
    val domains: List<ProteinDomain>,
) {
    val capabilities: List<MolecularCapability> =
        Collections.unmodifiableList(domains.flatMap(ProteinDomain::capabilities))

    companion object {
        /**
         * Creates an [ActiveProtein] from already interpreted [domains] and flattens capability access.
         */
        fun fromDomains(
            moleculeId: EntityId,
            source: Polypeptide,
            domains: List<ProteinDomain>,
        ): ActiveProtein {
            val immutableDomains = Collections.unmodifiableList(
                domains.map { domain ->
                    domain.copy(
                        capabilities = Collections.unmodifiableList(domain.capabilities.toList()),
                    )
                },
            )

            return ActiveProtein(
                moleculeId = moleculeId,
                source = source,
                domains = immutableDomains,
            )
        }

        /**
         * Interprets [source] and returns a first-class runtime protein molecule.
         */
        fun interpret(
            moleculeId: EntityId,
            source: Polypeptide,
        ): ActiveProtein = fromDomains(
            moleculeId = moleculeId,
            source = source,
            domains = ProteinInterpreter.interpret(source),
        )
    }
}
