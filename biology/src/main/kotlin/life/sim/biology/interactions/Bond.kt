package life.sim.biology.interactions

/**
 * Runtime record of an agent bound to a site on a molecule.
 */
data class Bond(
    val site: BindingSite,
    val agent: BoundAgent,
    val strength: Double,
    val decayPerTick: Double,
) {
    init {
        require(strength >= 0.0) {
            "Bond strength must be greater than or equal to zero, but was $strength."
        }
        require(decayPerTick >= 0.0) {
            "Bond decayPerTick must be greater than or equal to zero, but was $decayPerTick."
        }
    }

    fun isActive(): Boolean = strength > 0.0

    fun decay(ticks: Int = 1): Bond {
        require(ticks >= 0) {
            "Bond decay ticks must be greater than or equal to zero, but was $ticks."
        }

        return copy(strength = (strength - (decayPerTick * ticks)).coerceAtLeast(0.0))
    }
}

