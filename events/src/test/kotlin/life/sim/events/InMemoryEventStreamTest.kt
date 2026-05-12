package life.sim.events

import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryEventStreamTest {
    @Test
    fun `publish delivers events to subscribers listening to all events`() {
        val stream = InMemoryEventStream()
        val received = mutableListOf<Event>()
        val event = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))

        stream.subscribe(listener = EventListener { received.add(it) })
        stream.publish(event)

        assertEquals(listOf(event), received)
    }

    @Test
    fun `publish only delivers events to matching topic subscribers`() {
        val stream = InMemoryEventStream()
        val received = mutableListOf<Event>()
        val matching = testEvent(topic = "biology/bonds/")
        val other = testEvent(id = "evt-2", topic = "simulator/input/")

        stream.subscribe(topic = "biology/bonds/", listener = EventListener { received.add(it) })
        stream.publish(matching)
        stream.publish(other)

        assertEquals(listOf(matching), received)
    }

    @Test
    fun `publish only delivers events to subscribers with matching routing tag prefix`() {
        val stream = InMemoryEventStream()
        val received = mutableListOf<Event>()
        val matching = testEvent(routingTags = setOf("biology/", "bonds/succeeded/", "entities/molecule-123/"))
        val other = testEvent(id = "evt-2", routingTags = setOf("bindings/started/"))

        stream.subscribe(routingTagPrefix = "bonds/", listener = EventListener { received.add(it) })
        stream.publish(matching)
        stream.publish(other)

        assertEquals(listOf(matching), received)
    }

    @Test
    fun `publish only delivers events when topic and routing tag prefix both match`() {
        val stream = InMemoryEventStream()
        val received = mutableListOf<Event>()
        val matching = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))
        val wrongTopic = testEvent(id = "evt-2", topic = "simulator/input/", routingTags = setOf("bonds/succeeded/"))
        val wrongTag = testEvent(id = "evt-3", topic = "biology/bonds/", routingTags = setOf("bindings/started/"))

        stream.subscribe(topic = "biology/bonds/", routingTagPrefix = "bonds/", listener = EventListener { received.add(it) })
        stream.publish(matching)
        stream.publish(wrongTopic)
        stream.publish(wrongTag)

        assertEquals(listOf(matching), received)
    }

    @Test
    fun `unsubscribed listeners no longer receive events`() {
        val stream = InMemoryEventStream()
        val received = mutableListOf<Event>()
        val event = testEvent()

        val subscription = stream.subscribe(listener = EventListener { received.add(it) })
        subscription.unsubscribe()
        stream.publish(event)

        assertEquals(emptyList(), received)
    }

    @Test
    fun `publish reaches multiple matching subscribers`() {
        val stream = InMemoryEventStream()
        val first = mutableListOf<Event>()
        val second = mutableListOf<Event>()
        val event = testEvent(topic = "biology/bonds/", routingTags = setOf("bonds/succeeded/"))

        stream.subscribe(topic = "biology/bonds/", listener = EventListener { first.add(it) })
        stream.subscribe(routingTagPrefix = "bonds/", listener = EventListener { second.add(it) })

        stream.publish(event)

        assertEquals(listOf(event), first)
        assertEquals(listOf(event), second)
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
