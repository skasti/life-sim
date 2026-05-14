package life.sim.biology.events

import life.sim.biology.interactions.*
import life.sim.biology.primitives.NucleotideSequence
import life.sim.events.EventVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BindingAvailabilityEventsTest {
    @Test
    fun `BindingEndpointAvailable exposes expected metadata payload and routing tags`() {
        val event = BindingEndpointAvailable(
            id = "evt-1",
            source = "biology-test",
            parentCorrelationId = "parent-1",
            rootCorrelationId = "root-1",
            endpoint = WholeMoleculeEndpoint(EntityId(123)),
            capabilities = BindingCapabilities(
                supportedBondTypes = setOf("hydrogen"),
                sequencePattern = NucleotideSequence.parse("AUGC"),
                affinity = 0.75,
            ),
        )

        assertEquals("binding-endpoint-available", event.type)
        assertEquals("1.0.0", event.version)
        assertEquals(EventVersion(1, 0, 0), EventVersion.parse(event.version))
        assertEquals("biology/bindings/", event.topic)
        assertEquals(
            setOf("biology/", "bindings/", "bindings/available/", "entities/123/"),
            event.routingTags,
        )
        assertEquals(EntityId(123), event.endpoint.moleculeId)
        assertEquals(setOf("hydrogen"), event.capabilities.supportedBondTypes)
    }

    @Test
    fun `BindingSurfaceAvailable exposes expected metadata payload and routing tags`() {
        val event = BindingSurfaceAvailable(
            id = "evt-2",
            source = "biology-test",
            parentCorrelationId = null,
            rootCorrelationId = "root-2",
            surface = BindingSurface(
                moleculeId = EntityId(999),
                strand = BindingStrand.SINGLE,
                sequence = NucleotideSequence.parse("GGCA"),
            ),
            capabilities = BindingCapabilities(affinity = 0.4),
        )

        assertEquals("binding-surface-available", event.type)
        assertEquals("1.0.0", event.version)
        assertTrue(EventVersion.parse(event.version).major == 1)
        assertEquals("biology/bindings/", event.topic)
        assertEquals(
            setOf("biology/", "bindings/", "bindings/available/", "entities/999/"),
            event.routingTags,
        )
        assertEquals(EntityId(999), event.surface.moleculeId)
        assertEquals(0.4, event.capabilities.affinity)
    }
}
