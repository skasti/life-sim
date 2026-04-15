package life.sim.biology.molecules

/**
 * Amino-acid alphabet used by [Polypeptide].
 *
 * We use one-letter residue codes and treat unknown residues as invalid input.
 */
enum class AminoAcid(val symbol: Char) {
    A('A'), R('R'), N('N'), D('D'), C('C'), E('E'), Q('Q'), G('G'), H('H'), I('I'),
    L('L'), K('K'), M('M'), F('F'), P('P'), S('S'), T('T'), W('W'), Y('Y'), V('V');

    companion object {
        private val bySymbol = entries.associateBy { it.symbol }

        fun fromChar(symbol: Char): AminoAcid =
            bySymbol[symbol.uppercaseChar()]
                ?: throw IllegalArgumentException(
                    "Invalid amino-acid '$symbol'. Expected one of ${entries.joinToString(", ") { it.symbol.toString() }}.",
                )
    }
}
