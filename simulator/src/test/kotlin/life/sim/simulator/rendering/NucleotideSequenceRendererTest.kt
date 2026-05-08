package life.sim.simulator.rendering

import com.badlogic.gdx.math.Vector2
import life.sim.biology.primitives.NucleotideSequence
import life.sim.biology.primitives.SequenceDirection
import life.sim.simulator.rendering.geometry.rotatePoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NucleotideSequenceRendererTest {
    private val renderer = NucleotideSequenceRenderer(baseSize = 20f, tileGap = 10f)

    @Test
    fun `layout skips empty sequences`() {
        val layout = renderer.layout(NucleotideSequence.empty(), Vector2(10f, 20f))

        assertNull(layout)
    }

    @Test
    fun `layout uses the midpoint of the overhang inclusive backbone span as pivot`() {
        val sequence = NucleotideSequence.parse("ACG")
        val position = Vector2(100f, 50f)
        val layout = requireNotNull(renderer.layout(sequence, position))

        assertVectorEquals(position, layout.pivot)
        assertVectorEquals(Vector2(52f, 50f), layout.backboneStart)
        assertVectorEquals(Vector2(148f, 50f), layout.backboneEnd)
    }

    @Test
    fun `rotated layout moves nucleotide anchors backbone and direction indicator around the shared pivot`() {
        val sequence = NucleotideSequence.parse("AU")
        val position = Vector2(100f, 50f)
        val layout = requireNotNull(renderer.layout(sequence, position))
        val rotated = layout.rotated(90f)

        assertVectorEquals(layout.pivot, rotated.pivot)
        assertVectorEquals(rotatePoint(layout.backboneStart, layout.pivot, 90f), rotated.backboneStart)
        assertVectorEquals(rotatePoint(layout.backboneEnd, layout.pivot, 90f), rotated.backboneEnd)
        layout.nucleotideAnchors.zip(rotated.nucleotideAnchors).forEach { (expectedAnchor, actualAnchor) ->
            assertVectorEquals(rotatePoint(expectedAnchor, layout.pivot, 90f), actualAnchor)
        }
        layout.directionIndicatorVertices.zip(rotated.directionIndicatorVertices).forEach { (expectedVertex, actualVertex) ->
            assertVectorEquals(rotatePoint(expectedVertex, layout.pivot, 90f), actualVertex)
        }
    }

    @Test
    fun `nucleotideRotation applies opposite strand-facing offsets for forward and backward sequences`() {
        assertEquals(270f, renderer.nucleotideRotation(SequenceDirection.FORWARD))
        assertEquals(90f, renderer.nucleotideRotation(SequenceDirection.BACKWARD))
    }

    @Test
    fun `layout keeps nucleotide anchors centered and places the direction indicator on the strand-facing side`() {
        val position = Vector2(100f, 50f)
        val forwardLayout = requireNotNull(renderer.layout(NucleotideSequence.parse(">AU>"), position))
        val backwardLayout = requireNotNull(renderer.layout(NucleotideSequence.parse("<AU<"), position))

        assertEquals(position.y, forwardLayout.nucleotideAnchors.first().y)
        assertEquals(position.y, backwardLayout.nucleotideAnchors.first().y)

        val forwardIndicatorTip = forwardLayout.directionIndicatorVertices.first()
        val backwardIndicatorTip = backwardLayout.directionIndicatorVertices.first()

        assertEquals(137f, forwardIndicatorTip.x)
        assertEquals(40f, forwardIndicatorTip.y)
        assertEquals(63f, backwardIndicatorTip.x)
        assertEquals(60f, backwardIndicatorTip.y)
        assertEquals(true, forwardIndicatorTip.x > forwardLayout.backboneEnd.x)
        assertEquals(true, backwardIndicatorTip.x < backwardLayout.backboneStart.x)
    }

    private fun assertVectorEquals(expected: Vector2, actual: Vector2, tolerance: Float = 0.0001f) {
        assertEquals(expected.x, actual.x, tolerance)
        assertEquals(expected.y, actual.y, tolerance)
    }
}

