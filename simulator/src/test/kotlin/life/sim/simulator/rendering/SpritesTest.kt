package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.g2d.TextureRegion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SpritesTest {
    @Test
    fun `getOrCreate reuses cached sprite for the same key`() {
        val sprites = Sprites()
        var generated = 0

        val first = sprites.getOrCreate(SpriteKey("k")) {
            generated += 1
            TextureRegion()
        }
        val second = sprites.getOrCreate(SpriteKey("k")) {
            generated += 1
            TextureRegion()
        }

        assertSame(first, second)
        assertEquals(1, generated)
    }

    @Test
    fun `putRegion overwrites existing region for same key`() {
        val sprites = Sprites()
        val first = TextureRegion()
        val second = TextureRegion()

        sprites.putRegion(SpriteKey("k"), first)
        val stored = sprites.putRegion(SpriteKey("k"), second)

        assertSame(second, stored)
        assertSame(second, sprites.getOrCreate(SpriteKey("k")) { TextureRegion() })
    }
}
