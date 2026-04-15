package life.sim.biology.proteins

/**
 * One interpreted motif hit within a [life.sim.biology.molecules.Polypeptide].
 */
data class ProteinDomain(
    val name: String,
    val startInclusive: Int,
    val endExclusive: Int,
    val motif: String,
    val capabilities: List<MolecularCapability>,
)
