package life.sim.biology.proteins

import life.sim.biology.interactions.BindingMatcher
import life.sim.biology.interactions.BindingSurface
import life.sim.biology.interactions.Bond
import life.sim.biology.interactions.BondRegistry
import life.sim.biology.interactions.MoleculeId
import life.sim.biology.interactions.SiteEndpoint
import life.sim.biology.interactions.WholeMoleculeEndpoint

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
    ): Bond? {
        val targetSite = BindingMatcher.complementaryMatchSite(binder.bindingPattern, target)
            ?: return null

        val normalizedStrength = binder.affinity.coerceIn(0.0, 1.0)
        if (normalizedStrength <= 0.0) {
            return null
        }

        val overlapping = registry.overlapping(targetSite)
        val strongestOverlap = overlapping.maxOfOrNull(Bond::strength)
        val conflictThreshold = if (normalizedStrength > CONFLICT_EPSILON) {
            normalizedStrength - CONFLICT_EPSILON
        } else {
            normalizedStrength
        }

        if (strongestOverlap != null && strongestOverlap >= conflictThreshold) {
            return null
        }

        overlapping.forEach(registry::remove)

        val bond = Bond(
            left = WholeMoleculeEndpoint(proteinId),
            right = SiteEndpoint(targetSite),
            strength = normalizedStrength,
            decayPerTick = DEFAULT_DECAY_PER_TICK,
        )

        registry.add(bond)

        return bond
    }
}
