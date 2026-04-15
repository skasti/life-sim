package life.sim.biology.proteins

import life.sim.biology.molecules.AminoAcid
import life.sim.biology.molecules.Polypeptide
import life.sim.biology.primitives.Nucleotide
import life.sim.biology.primitives.NucleotideSequence

/**
 * Interprets [Polypeptide] chains as composable domains by motif matching.
 */
object ProteinInterpreter {
    private const val DOMAIN_WINDOW_RADIUS = 2
    private const val BINDER_PATTERN_LENGTH = 6

    private data class DomainPattern(
        val name: String,
        val motif: String,
        val motifResidues: List<AminoAcid> = motif.map(AminoAcid::fromChar),
        val factory: (localWindow: List<AminoAcid>) -> MolecularCapability,
    )

    private val patterns = listOf(
        DomainPattern(
            name = "BinderDomain",
            motif = "KRGK",
            factory = { local ->
                SequenceBinder(
                    bindingPattern = deriveBinderPattern(local),
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
        if (polypeptide.isEmpty()) {
            return emptyList()
        }

        val domains = mutableListOf<ProteinDomain>()
        for (pattern in patterns) {
            val motif = pattern.motif
            val motifResidues = pattern.motifResidues
            if (motifResidues.size > polypeptide.size) {
                continue
            }

            for (start in 0..(polypeptide.size - motifResidues.size)) {
                if (matches(polypeptide, start, motifResidues)) {
                    val local = localWindow(polypeptide, start, motifResidues.size)
                    domains += ProteinDomain(
                        name = pattern.name,
                        startInclusive = start,
                        endExclusive = start + motifResidues.size,
                        motif = motif,
                        capabilities = listOf(pattern.factory(local)),
                    )
                }
            }
        }

        return domains.sortedWith(compareBy(ProteinDomain::startInclusive, ProteinDomain::name))
    }

    private fun matches(polypeptide: Polypeptide, start: Int, motifResidues: List<AminoAcid>): Boolean {
        for (offset in motifResidues.indices) {
            if (polypeptide[start + offset] != motifResidues[offset]) {
                return false
            }
        }

        return true
    }

    private fun localWindow(polypeptide: Polypeptide, motifStart: Int, motifLength: Int): List<AminoAcid> {
        val from = (motifStart - DOMAIN_WINDOW_RADIUS).coerceAtLeast(0)
        val to = (motifStart + motifLength + DOMAIN_WINDOW_RADIUS).coerceAtMost(polypeptide.size)
        return (from until to).map(polypeptide::get)
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

    private fun deriveBinderPattern(localWindow: List<AminoAcid>): NucleotideSequence {
        val seed = localWindow.foldIndexed(17) { index, acc, residue ->
            (acc * 31) + (residue.symbol.code * (index + 1))
        }

        var state = seed
        val pattern = List(BINDER_PATTERN_LENGTH) {
            state = state * 1103515245 + 12345
            when ((state ushr 16) and 0x3) {
                0 -> Nucleotide.A
                1 -> Nucleotide.C
                2 -> Nucleotide.G
                else -> Nucleotide.U
            }
        }

        return NucleotideSequence.from(pattern)
    }

    private fun clamp01(value: Double): Double = value.coerceIn(0.0, 1.0)
}
