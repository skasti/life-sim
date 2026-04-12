package life.sim.biology.interactions

/**
 * Mutable in-memory store of active bonds.
 */
class BondRegistry(
    bonds: Iterable<Bond> = emptyList(),
) : Iterable<Bond> {
    private val bonds = bonds.toMutableList()

    val size: Int
        get() = bonds.size

    fun isEmpty(): Boolean = bonds.isEmpty()

    fun add(bond: Bond): Bond {
        bonds.add(bond)
        return bond
    }

    fun remove(bond: Bond): Boolean = bonds.remove(bond)

    fun clear() {
        bonds.clear()
    }

    fun toList(): List<Bond> = bonds.toList()

    fun bondsFor(moleculeId: MoleculeId): List<Bond> = bonds.filter { it.site.moleculeId == moleculeId }

    fun bondsOnSurface(site: BindingSite): List<Bond> = bonds.filter { it.site.sameSurfaceAs(site) }

    fun overlapping(site: BindingSite): List<Bond> = bonds.filter { it.site.overlaps(site) }

    fun decayAll(ticks: Int = 1): List<Bond> {
        require(ticks >= 0) {
            "Bond decay ticks must be greater than or equal to zero, but was $ticks."
        }

        val remaining = bonds
            .map { it.decay(ticks) }
            .filter(Bond::isActive)

        bonds.clear()
        bonds.addAll(remaining)
        return toList()
    }

    override fun iterator(): Iterator<Bond> = bonds.toList().iterator()
}

