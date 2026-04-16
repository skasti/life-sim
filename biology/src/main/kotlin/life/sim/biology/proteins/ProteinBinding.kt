package life.sim.biology.proteins

import life.sim.biology.interactions.BindingMatcher
import life.sim.biology.interactions.BindingSurface
import life.sim.biology.interactions.Bond
import life.sim.biology.interactions.BondRegistry
import life.sim.biology.interactions.MoleculeId
import life.sim.biology.interactions.SiteEndpoint
import life.sim.biology.interactions.WholeMoleculeEndpoint

/**
 * Result of a conflict-aware runtime bind attempt.
 */
data class BindingDecision(
    val outcome: BindingOutcome,
    val bond: Bond? = null,
    val displaced: List<Bond> = emptyList(),
)

enum class BindingOutcome {
    BOUND,
    BOUND_AFTER_DISPLACEMENT,
    REJECTED_NO_SITE,
    REJECTED_INACTIVE_STRENGTH,
    REJECTED_CONFLICT,
}

/**
 * Runtime bridge from interpreted protein capabilities to concrete bonds.
 */
object ProteinBinding {
    private const val DEFAULT_DECAY_PER_TICK = 0.05
    private const val CONFLICT_EPSILON = 1.0e-9

    fun tryBind(
        proteinId: MoleculeId,
        binder: SequenceBinder,
        target: BindingSurface,
        registry: BondRegistry,
    ): BindingDecision {
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)
            ?: return BindingDecision(outcome = BindingOutcome.REJECTED_NO_SITE)

        val normalizedStrength = binder.affinity.coerceIn(0.0, 1.0)
        if (normalizedStrength <= 0.0) {
            return BindingDecision(outcome = BindingOutcome.REJECTED_INACTIVE_STRENGTH)
        }

        val overlapping = registry.overlapping(targetSite)
        val strongestOverlap = overlapping.maxOfOrNull(Bond::strength)

        if (strongestOverlap != null && strongestOverlap >= normalizedStrength - CONFLICT_EPSILON) {
            return BindingDecision(outcome = BindingOutcome.REJECTED_CONFLICT)
        }

        overlapping.forEach(registry::remove)

        val bond = Bond(
            left = WholeMoleculeEndpoint(proteinId),
            right = SiteEndpoint(targetSite),
            strength = normalizedStrength,
            decayPerTick = DEFAULT_DECAY_PER_TICK,
        )

        registry.add(bond)

        return BindingDecision(
            outcome = if (overlapping.isEmpty()) BindingOutcome.BOUND else BindingOutcome.BOUND_AFTER_DISPLACEMENT,
            bond = bond,
            displaced = overlapping,
        )
    }
}
