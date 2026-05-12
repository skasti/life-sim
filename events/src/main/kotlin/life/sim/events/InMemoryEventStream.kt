package life.sim.events

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

class InMemoryEventStream(
    workerCount: Int = 1,
) : EventStream {
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

    private val subscribers = ConcurrentHashMap<Long, Subscriber>()
    private val nextSubscriberId = AtomicLong(0)
    private val eventQueue = LinkedBlockingQueue<Event>()

    init {
        require(workerCount > 0) { "workerCount must be greater than 0" }
        repeat(workerCount) {
            Thread {
                while (true) {
                    val event = eventQueue.take()
                    subscribers.values
                        .asSequence()
                        .filter { it.matches(event) }
                        .forEach { it.listener.onEvent(event) }
                }
            }.apply {
                isDaemon = true
                name = "in-memory-event-stream-worker-$it"
                start()
            }
        }
    }

    override fun publish(event: Event) {
        eventQueue.put(event)
    }

    override fun subscribe(
        topic: String?,
        routingTagPrefix: String?,
        listener: EventListener,
    ): Subscription {
        val subscriberId = nextSubscriberId.getAndIncrement()
        subscribers[subscriberId] = Subscriber(topic = topic, routingTagPrefix = routingTagPrefix, listener = listener)
        return Subscription {
            subscribers.remove(subscriberId)
        }
    }
}
