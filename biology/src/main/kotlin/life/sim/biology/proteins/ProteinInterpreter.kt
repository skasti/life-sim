package life.sim.biology.proteins

import life.sim.biology.molecules.AminoAcid
import life.sim.biology.molecules.Polypeptide

/**
 * Interprets [Polypeptide] chains as composable domains by motif matching.
 */
object ProteinInterpreter {
    private const val DOMAIN_WINDOW_RADIUS = 2

    private data class DomainPattern(
        val name: String,
        val motif: String,
        val factory: (localWindow: List<AminoAcid>) -> MolecularCapability,
    )

    private val patterns = listOf(
        DomainPattern(
            name = "BinderDomain",
            motif = "KRGK",
            factory = { local ->
                SequenceBinder(
                    affinity = clamp01(0.45 + weightedSignal(local, chargeWeight = 0.08, hydrophobicWeight = 0.02)),
                    specificity = clamp01(0.35 + weightedSignal(local, chargeWeight = 0.04, hydrophobicWeight = 0.05)),
                )
            },
        ),
        DomainPattern(
            name = "CutterDomain",
            motif = "HEMH",
            factory = { local ->
                Cutter(
                    catalyticStrength = clamp01(0.4 + weightedSignal(local, chargeWeight = 0.06, hydrophobicWeight = 0.01)),
                )
            },
        ),
        DomainPattern(
            name = "LigaseDomain",
            motif = "GGH",
            factory = { local ->
                Ligase(
                    catalyticStrength = clamp01(0.4 + weightedSignal(local, chargeWeight = 0.03, hydrophobicWeight = 0.05)),
                )
            },
        ),
        DomainPattern(
            name = "BlockerDomain",
            motif = "PPW",
            factory = { local ->
                Blocker(
                    potency = clamp01(0.4 + weightedSignal(local, chargeWeight = 0.02, hydrophobicWeight = 0.08)),
                )
            },
        ),
    )

    fun interpret(polypeptide: Polypeptide): List<ProteinDomain> {
        val chain = polypeptide.toList()
        if (chain.isEmpty()) {
            return emptyList()
        }

        val domains = mutableListOf<ProteinDomain>()
        for (pattern in patterns) {
            val motif = pattern.motif
            if (motif.length > chain.size) {
                continue
            }

            for (start in 0..(chain.size - motif.length)) {
                if (matches(chain, start, motif)) {
                    val local = localWindow(chain, start, motif.length)
                    domains += ProteinDomain(
                        name = pattern.name,
                        startInclusive = start,
                        endExclusive = start + motif.length,
                        motif = motif,
                        capabilities = listOf(pattern.factory(local)),
                    )
                }
            }
        }

        return domains.sortedWith(compareBy(ProteinDomain::startInclusive, ProteinDomain::name))
    }

    private fun matches(chain: List<AminoAcid>, start: Int, motif: String): Boolean {
        for (offset in motif.indices) {
            if (chain[start + offset] != AminoAcid.fromChar(motif[offset])) {
                return false
            }
        }

        return true
    }

    private fun localWindow(chain: List<AminoAcid>, motifStart: Int, motifLength: Int): List<AminoAcid> {
        val from = (motifStart - DOMAIN_WINDOW_RADIUS).coerceAtLeast(0)
        val to = (motifStart + motifLength + DOMAIN_WINDOW_RADIUS).coerceAtMost(chain.size)
        return chain.subList(from, to)
    }

    private fun weightedSignal(
        residues: List<AminoAcid>,
        chargeWeight: Double,
        hydrophobicWeight: Double,
    ): Double {
        var signal = 0.0
        for (residue in residues) {
            signal += chargeScore(residue) * chargeWeight
            signal += hydrophobicScore(residue) * hydrophobicWeight
        }
        return signal
    }

    private fun chargeScore(residue: AminoAcid): Double = when (residue) {
        AminoAcid.K, AminoAcid.R, AminoAcid.H -> 1.0
        AminoAcid.D, AminoAcid.E -> -1.0
        else -> 0.0
    }

    private fun hydrophobicScore(residue: AminoAcid): Double = when (residue) {
        AminoAcid.A, AminoAcid.I, AminoAcid.L, AminoAcid.M, AminoAcid.F, AminoAcid.W, AminoAcid.Y, AminoAcid.V -> 1.0
        else -> 0.0
    }

    private fun clamp01(value: Double): Double = value.coerceIn(0.0, 1.0)
}
