package life.sim.biology.primitives

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SequenceRangeTest {
    @Test
    fun `range exposes length and containment`() {
        val range = SequenceRange(2, 6)

        assertEquals(4, range.length)
        assertTrue(range.contains(2))
        assertTrue(range.contains(5))
        assertFalse(range.contains(6))
    }

    @Test
    fun `range overlap uses half open semantics`() {
        val left = SequenceRange(2, 6)

        assertTrue(left.overlaps(SequenceRange(5, 8)))
        assertFalse(left.overlaps(SequenceRange(6, 9)))
    }

    @Test
    fun `empty ranges never overlap`() {
        val empty = SequenceRange(5, 5)

        assertFalse(empty.overlaps(SequenceRange(4, 6)))
        assertFalse(SequenceRange(4, 6).overlaps(empty))
        assertFalse(empty.overlaps(SequenceRange(5, 5)))
    }

    @Test
    fun `range rejects negative start`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SequenceRange(-1, 2)
        }

        assertEquals(
            "Sequence range start must be greater than or equal to zero, but was -1.",
            exception.message,
        )
    }

    @Test
    fun `range rejects end exclusive before start`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            SequenceRange(4, 3)
        }

        assertEquals(
            "Sequence range endExclusive must be greater than or equal to start, but was 3 for start 4.",
            exception.message,
        )
    }
}

