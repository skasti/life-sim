package life.sim.events

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InMemoryEventStreamTest {
    @Test
    fun `publish delivers events to subscribers listening to all events`() {
        InMemoryEventStream().use { stream ->
        val received = CopyOnWriteArrayList<Event>()
        val event = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))
        val delivery = CountDownLatch(1)

        stream.subscribe(listener = EventListener {
            received.add(it)
            delivery.countDown()
        })
        stream.publish(event)

        assertTrue(delivery.await(1, TimeUnit.SECONDS))
        assertEquals(listOf(event), received.toList())
        }
    }

    @Test
    fun `publish only delivers events to matching topic subscribers`() {
        InMemoryEventStream().use { stream ->
        val received = CopyOnWriteArrayList<Event>()
        val matching = testEvent(topic = "biology/bonds/")
        val other = testEvent(id = "evt-2", topic = "simulator/input/")
        val delivery = CountDownLatch(1)

        stream.subscribe(topic = "biology/bonds/", listener = EventListener {
            received.add(it)
            delivery.countDown()
        })
        stream.publish(matching)
        stream.publish(other)

        assertTrue(delivery.await(1, TimeUnit.SECONDS))
        Thread.sleep(50)
        assertEquals(listOf(matching), received.toList())
        }
    }

    @Test
    fun `publish only delivers events to subscribers with matching routing tag prefix`() {
        InMemoryEventStream().use { stream ->
        val received = CopyOnWriteArrayList<Event>()
        val matching = testEvent(routingTags = setOf("biology/", "bonds/succeeded/", "entities/molecule-123/"))
        val other = testEvent(id = "evt-2", routingTags = setOf("bindings/started/"))
        val delivery = CountDownLatch(1)

        stream.subscribe(routingTagPrefix = "bonds/", listener = EventListener {
            received.add(it)
            delivery.countDown()
        })
        stream.publish(matching)
        stream.publish(other)

        assertTrue(delivery.await(1, TimeUnit.SECONDS))
        Thread.sleep(50)
        assertEquals(listOf(matching), received.toList())
        }
    }

    @Test
    fun `publish only delivers events when topic and routing tag prefix both match`() {
        InMemoryEventStream().use { stream ->
        val received = CopyOnWriteArrayList<Event>()
        val matching = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))
        val wrongTopic = testEvent(id = "evt-2", topic = "simulator/input/", routingTags = setOf("bonds/succeeded/"))
        val wrongTag = testEvent(id = "evt-3", topic = "biology/bonds/", routingTags = setOf("bindings/started/"))
        val delivery = CountDownLatch(1)

        stream.subscribe(topic = "biology/bonds/", routingTagPrefix = "bonds/", listener = {
            received.add(it)
            delivery.countDown()
        })
        stream.publish(matching)
        stream.publish(wrongTopic)
        stream.publish(wrongTag)

        assertTrue(delivery.await(1, TimeUnit.SECONDS))
        Thread.sleep(50)
        assertEquals(listOf(matching), received.toList())
        }
    }

    @Test
    fun `unsubscribed listeners no longer receive events`() {
        InMemoryEventStream().use { stream ->
        val received = CopyOnWriteArrayList<Event>()
        val event = testEvent()

        val subscription = stream.subscribe(listener = EventListener { received.add(it) })
        subscription.unsubscribe()
        stream.publish(event)

        Thread.sleep(50)
        assertEquals(emptyList(), received.toList())
        }
    }

    @Test
    fun `publish reaches multiple matching subscribers`() {
        InMemoryEventStream().use { stream ->
        val first = CopyOnWriteArrayList<Event>()
        val second = CopyOnWriteArrayList<Event>()
        val event = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))
        val delivery = CountDownLatch(2)

        stream.subscribe(topic = "biology/bonds/", listener = EventListener {
            first.add(it)
            delivery.countDown()
        })
        stream.subscribe(routingTagPrefix = "bonds/", listener = EventListener {
            second.add(it)
            delivery.countDown()
        })

        stream.publish(event)

        assertTrue(delivery.await(1, TimeUnit.SECONDS))
        assertEquals(listOf(event), first.toList())
        assertEquals(listOf(event), second.toList())
        }
    }

    @Test
    fun `publish dispatches matching subscribers in subscription order`() {
        InMemoryEventStream().use { stream ->
            val received = CopyOnWriteArrayList<String>()
            val delivery = CountDownLatch(3)

            stream.subscribe(listener = EventListener {
                received += "first"
                delivery.countDown()
            })
            stream.subscribe(listener = EventListener {
                received += "second"
                delivery.countDown()
            })
            stream.subscribe(listener = EventListener {
                received += "third"
                delivery.countDown()
            })

            stream.publish(testEvent())

            assertTrue(delivery.await(1, TimeUnit.SECONDS))
            assertEquals(listOf("first", "second", "third"), received.toList())
        }
    }

    @Test
    fun `subscribe does not block while listeners process events`() {
        InMemoryEventStream().use { stream ->
            val listenerStarted = CountDownLatch(1)
            val releaseListener = CountDownLatch(1)
            val subscribeCompleted = CountDownLatch(1)

            stream.subscribe(listener = EventListener {
                listenerStarted.countDown()
                assertTrue(releaseListener.await(1, TimeUnit.SECONDS))
            })

            stream.publish(testEvent())
            assertTrue(listenerStarted.await(1, TimeUnit.SECONDS))

            val subscribeThread = Thread {
                stream.subscribe(listener = EventListener { })
                subscribeCompleted.countDown()
            }
            subscribeThread.start()

            assertTrue(subscribeCompleted.await(200, TimeUnit.MILLISECONDS))

            releaseListener.countDown()
            subscribeThread.join(1_000)
            assertTrue(!subscribeThread.isAlive)
        }
    }

    @Test
    fun `listener exception does not stop future dispatch`() {
        InMemoryEventStream().use { stream ->
            val received = CopyOnWriteArrayList<String>()
            val delivery = CountDownLatch(2)

            stream.subscribe(listener = EventListener { throw IllegalStateException("boom") })
            stream.subscribe(listener = EventListener {
                received.add(it.id)
                delivery.countDown()
            })

            stream.publish(testEvent(id = "evt-1"))
            stream.publish(testEvent(id = "evt-2"))

            assertTrue(delivery.await(1, TimeUnit.SECONDS))
            assertEquals(listOf("evt-1", "evt-2"), received.toList())
        }
    }

    @Test
    fun `publishing after close fails`() {
        val stream = InMemoryEventStream()
        stream.close()

        assertFailsWith<IllegalStateException> {
            stream.publish(testEvent())
        }
    }

    private data class TestEvent(
        override val id: String,
        override val type: String,
        override val version: String,
        override val source: String,
        override val parentCorrelationId: String?,
        override val rootCorrelationId: String,
        override val topic: String,
        override val routingTags: Set<String>,
    ) : Event

    private fun testEvent(
        id: String = "evt-1",
        type: String = "bond-succeeded",
        version: String = "1",
        source: String = "biology-bond-system",
        parentCorrelationId: String? = null,
        rootCorrelationId: String = "root-1",
        topic: String = "biology/bonds/",
        routingTags: Set<String> = setOf("biology/", "bonds/"),
    ): Event = TestEvent(
        id = id,
        type = type,
        version = version,
        source = source,
        parentCorrelationId = parentCorrelationId,
        rootCorrelationId = rootCorrelationId,
        topic = topic,
        routingTags = routingTags,
    )
}
