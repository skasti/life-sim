package life.sim.biology.interactions

/**
 * Mutable in-memory store of active bonds.
 */
class BondRegistry(
    initialBonds: Iterable<Bond> = emptyList(),
) : Iterable<Bond> {
    private val bonds = initialBonds
        .filter(Bond::isActive)
        .toCollection(linkedSetOf())

    val size: Int
        get() = bonds.size

    fun isEmpty(): Boolean = bonds.isEmpty()

    fun add(bond: Bond): Bond {
        if (bond.isActive()) {
            bonds.add(bond)
        }
        return bond
    }

    fun remove(bond: Bond): Boolean = bonds.remove(bond)

    fun clear() {
        bonds.clear()
    }

    fun toList(): List<Bond> = bonds.toList()

    fun bondsFor(moleculeId: MoleculeId): List<Bond> = bonds.filter { it.involves(moleculeId) }

    fun bondsInvolving(site: BindingSite): List<Bond> =
        bonds.filter { bond -> bond.bindingSites().any { it == site } }

    fun bondsOnSurface(site: BindingSite): List<Bond> =
        bonds.filter { bond -> bond.bindingSites().any { it.sameSurfaceAs(site) } }

    fun overlapping(site: BindingSite): List<Bond> =
        bonds.filter { bond -> bond.bindingSites().any { it.overlaps(site) } }

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
