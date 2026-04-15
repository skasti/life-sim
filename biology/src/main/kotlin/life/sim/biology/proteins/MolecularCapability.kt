package life.sim.biology.proteins

/**
 * Runtime behavior exposed by one interpreted protein domain.
 */
sealed interface MolecularCapability {
    val kind: String
}

data class SequenceBinder(
    val affinity: Double,
    val specificity: Double,
) : MolecularCapability {
    override val kind: String = "SequenceBinder"
}

data class Cutter(
    val catalyticStrength: Double,
) : MolecularCapability {
    override val kind: String = "Cutter"
}

data class Ligase(
    val catalyticStrength: Double,
) : MolecularCapability {
    override val kind: String = "Ligase"
}

data class Blocker(
    val potency: Double,
) : MolecularCapability {
    override val kind: String = "Blocker"
}
