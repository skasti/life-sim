package life.sim.biology.interactions

/**
 * One endpoint of a runtime molecule association.
 */
sealed interface BondEndpoint {
    val moleculeId: EntityId
    val site: BindingSite?
}

/**
 * Endpoint that references only a whole molecule instance.
 */
data class WholeMoleculeEndpoint(
    override val moleculeId: EntityId,
) : BondEndpoint {
    override val site: BindingSite? = null
}

/**
 * Endpoint that references a specific binding site on a molecule.
 */
data class SiteEndpoint(
    override val site: BindingSite,
) : BondEndpoint {
    override val moleculeId: EntityId
        get() = site.moleculeId
}

/**
 * Runtime association between two molecule endpoints.
 */
data class Bond(
    val left: BondEndpoint,
    val right: BondEndpoint,
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

    fun involves(moleculeId: EntityId): Boolean =
        left.moleculeId == moleculeId || right.moleculeId == moleculeId

    fun bindingSites(): List<BindingSite> =
        listOfNotNull(left.site, right.site)

    fun decay(ticks: Int = 1): Bond {
        require(ticks >= 0) {
            "Bond decay ticks must be greater than or equal to zero, but was $ticks."
        }

        return copy(strength = (strength - (decayPerTick * ticks)).coerceAtLeast(0.0))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Bond) {
            return false
        }

        val endpointsMatch = (left == other.left && right == other.right) ||
            (left == other.right && right == other.left)

        return endpointsMatch &&
            strength.compareTo(other.strength) == 0 &&
            decayPerTick.compareTo(other.decayPerTick) == 0
    }

    override fun hashCode(): Int {
        val leftHash = left.hashCode()
        val rightHash = right.hashCode()
        val (firstEndpointHash, secondEndpointHash) = if (leftHash <= rightHash) {
            leftHash to rightHash
        } else {
            rightHash to leftHash
        }

        var result = 31 * firstEndpointHash + secondEndpointHash
        result = 31 * result + strength.hashCode()
        result = 31 * result + decayPerTick.hashCode()
        return result
    }
}
