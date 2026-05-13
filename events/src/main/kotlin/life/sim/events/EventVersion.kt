package life.sim.events

data class EventVersion(
    val major: Int,
    val minor: Int,
    val revision: Int,
) {
    init {
        require(major >= 0) { "Event version major must be non-negative, but was $major." }
        require(minor >= 0) { "Event version minor must be non-negative, but was $minor." }
        require(revision >= 0) { "Event version revision must be non-negative, but was $revision." }
    }

    override fun toString(): String = "$major.$minor.$revision"

    companion object {
        private val versionPattern = Regex("^(\\d+)\\.(\\d+)\\.(\\d+)$")

        fun parse(value: String): EventVersion {
            val match = versionPattern.matchEntire(value)
                ?: throw IllegalArgumentException(
                    "Event version must match [major].[minor].[revision], but was '$value'.",
                )

            val (major, minor, revision) = match.destructured
            return EventVersion(major.toInt(), minor.toInt(), revision.toInt())
        }
    }
}
