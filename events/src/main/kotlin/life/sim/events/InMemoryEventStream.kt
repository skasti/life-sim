package life.sim.events

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class InMemoryEventStream : EventStream, AutoCloseable {
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
    private val closed = AtomicBoolean(false)

    private val worker = Thread {
        while (true) {
            try {
                val event = eventQueue.take()
                subscribers.values
                    .asSequence()
                    .filter { it.matches(event) }
                    .forEach {
                        try {
                            it.listener.onEvent(event)
                        } catch (_: Exception) {
                            // One listener failing must not stop dispatch for other listeners or future events.
                        }
                    }
            } catch (_: InterruptedException) {
                if (closed.get()) {
                    return@Thread
                }
            }
        }
    }.apply {
        isDaemon = true
        name = "in-memory-event-stream-worker"
        start()
    }

    override fun publish(event: Event) {
        check(!closed.get()) { "InMemoryEventStream is closed" }
        eventQueue.put(event)
    }

    override fun subscribe(
        topic: String?,
        routingTagPrefix: String?,
        listener: EventListener,
    ): Subscription {
        check(!closed.get()) { "InMemoryEventStream is closed" }
        val subscriberId = nextSubscriberId.getAndIncrement()
        subscribers[subscriberId] = Subscriber(topic = topic, routingTagPrefix = routingTagPrefix, listener = listener)
        return Subscription {
            subscribers.remove(subscriberId)
        }
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) {
            return
        }
        worker.interrupt()
    }
}
