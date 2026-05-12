package life.sim.events

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class InMemoryEventStream : EventStream, AutoCloseable {
    private class SubscriberRegistration(
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

    private sealed interface QueueItem {
        data class EventItem(val event: Event) : QueueItem
        data object Stop : QueueItem
    }

    private val subscribers = CopyOnWriteArrayList<SubscriberRegistration>()
    private val eventQueue = LinkedBlockingQueue<QueueItem>()
    private val closed = AtomicBoolean(false)
    private val lifecycleLock = Any()

    private val worker = Thread {
        while (true) {
            when (val item = eventQueue.take()) {
                is QueueItem.EventItem -> dispatch(item.event)
                QueueItem.Stop -> return@Thread
            }
        }
    }.apply {
        isDaemon = true
        name = "in-memory-event-stream-worker"
        start()
    }

    override fun publish(event: Event) {
        synchronized(lifecycleLock) {
            check(!closed.get()) { "InMemoryEventStream is closed" }
            eventQueue.put(QueueItem.EventItem(event))
        }
    }

    override fun subscribe(
        topic: String?,
        routingTagPrefix: String?,
        listener: EventListener,
    ): Subscription {
        synchronized(lifecycleLock) {
            check(!closed.get()) { "InMemoryEventStream is closed" }
            val registration = SubscriberRegistration(topic = topic, routingTagPrefix = routingTagPrefix, listener = listener)
            subscribers += registration
            return Subscription {
                subscribers.remove(registration)
            }
        }
    }

    override fun close() {
        synchronized(lifecycleLock) {
            if (!closed.compareAndSet(false, true)) {
                return
            }
            eventQueue.put(QueueItem.Stop)
        }
        worker.join()
    }

    private fun dispatch(event: Event) {
        for (subscriber in subscribers) {
            if (!subscriber.matches(event)) {
                continue
            }

            try {
                subscriber.listener.onEvent(event)
            } catch (_: Exception) {
                // One listener failing must not stop dispatch for other listeners or future events.
            }
        }
    }
}
