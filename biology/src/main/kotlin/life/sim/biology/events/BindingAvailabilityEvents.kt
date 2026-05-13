package life.sim.biology.events

import life.sim.biology.interactions.*
import life.sim.biology.primitives.NucleotideSequence
import life.sim.events.Event
import life.sim.events.EventVersion

data class BindingCapabilities(
    val supportedBondTypes: Set<String> = emptySet(),
    val sequencePattern: NucleotideSequence? = null,
    val affinity: Double? = null,
)

data class BindingEndpointAvailable(
    override val id: String,
    override val source: String,
    override val parentCorrelationId: String?,
    override val rootCorrelationId: String,
    val endpoint: BondEndpoint,
    val capabilities: BindingCapabilities,
) : Event {
    override val type: String = "binding-endpoint-available"
    override val version: String = V1_0_0.toString()
    override val topic: String = BINDINGS_TOPIC

    override val routingTags: Set<String> = setOf(
        BIOLOGY_TAG,
        BINDINGS_TAG,
        BINDINGS_AVAILABLE_TAG,
        "entities/${endpoint.moleculeId.value}/",
    )
}

data class BindingSurfaceAvailable(
    override val id: String,
    override val source: String,
    override val parentCorrelationId: String?,
    override val rootCorrelationId: String,
    val surface: BindingSurface,
    val capabilities: BindingCapabilities,
) : Event {
    override val type: String = "binding-surface-available"
    override val version: String = V1_0_0.toString()
    override val topic: String = BINDINGS_TOPIC

    override val routingTags: Set<String> = setOf(
        BIOLOGY_TAG,
        BINDINGS_TAG,
        BINDINGS_AVAILABLE_TAG,
        "entities/${surface.moleculeId.value}/",
    )
}

private val V1_0_0 = EventVersion(1, 0, 0)

private const val BINDINGS_TOPIC = "biology/bindings/"
private const val BIOLOGY_TAG = "biology/"
private const val BINDINGS_TAG = "bindings/"
private const val BINDINGS_AVAILABLE_TAG = "bindings/available/"
