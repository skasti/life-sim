package life.sim.events

class InMemoryEventStream : EventStream {
    private data class Subscriber(
        val topic: String?,
        val routingTagPrefix: String?,
        val listener: EventListener,
    ) {
        fun matches(event: Event): Boolean {
            val topicMatches = topic == null || topic == event.topic
            val routingTagMatches = routingTagPrefix == null || event.routingTags.any { it.startsWith(routingTagPrefix) }
            return topicMatches && routingTagMatches
        }
    }

    private val subscribers = linkedMapOf<Long, Subscriber>()
    private var nextSubscriberId = 0L

    override fun publish(event: Event) {
        subscribers.values
            .toList()
            .filter { it.matches(event) }
            .forEach { it.listener.onEvent(event) }
    }

    override fun subscribe(
        topic: String?,
        routingTagPrefix: String?,
        listener: EventListener,
    ): Subscription {
        val subscriberId = nextSubscriberId++
        subscribers[subscriberId] = Subscriber(topic = topic, routingTagPrefix = routingTagPrefix, listener = listener)
        return Subscription {
            subscribers.remove(subscriberId)
        }
    }
}
