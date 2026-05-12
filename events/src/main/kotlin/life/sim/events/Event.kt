package life.sim.events

interface Event {
    val id: String
    val type: String
    val version: String
    val source: String

    val parentCorrelationId: String?
    val rootCorrelationId: String

    val topic: String
    val routingTags: Set<String>
}
