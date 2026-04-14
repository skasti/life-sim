package life.sim.biology.interactions

import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceRange

/**
 * Shared complementary matching helpers for sequence-driven binding.
 */
object BindingMatcher {
    fun complementaryMatchStart(pattern: NucleotideSequence, target: NucleotideSequence): Int {
        if (pattern.isEmpty()) {
            return 0
        }

        if (pattern.size > target.size) {
            return -1
        }

        val lastStartIndex = target.size - pattern.size
        for (startIndex in 0..lastStartIndex) {
            var matches = true

            for (offset in 0 until pattern.size) {
                if (pattern[offset] != target[startIndex + offset].complement()) {
                    matches = false
                    break
                }
            }

            if (matches) {
                return startIndex
            }
        }

        return -1
    }

    fun complementaryMatchSite(pattern: NucleotideSequence, surface: BindingSurface): BindingSite? {
        val start = complementaryMatchStart(pattern, surface.sequence)
        return if (start < 0) {
            null
        } else {
            BindingSite(surface, SequenceRange(start, start + pattern.size))
        }
    }
}

