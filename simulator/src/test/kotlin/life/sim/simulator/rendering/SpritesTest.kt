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
            CachedSprite(TextureRegion(), 1f, 1f, 0f, 0f, 0f, 0f)
        }
        val second = sprites.getOrCreate(SpriteKey("k")) {
            generated += 1
            CachedSprite(TextureRegion(), 1f, 1f, 0f, 0f, 0f, 0f)
        }

        assertSame(first, second)
        assertEquals(1, generated)
    }

    @Test
    fun `putRegion overwrites existing region for same key`() {
        val sprites = Sprites()
        val first = TextureRegion()
        val second = TextureRegion()

        sprites.putRegion(SpriteKey("k"), first, 1f, 1f, 0f, 0f, 0f, 0f)
        val stored = sprites.putRegion(SpriteKey("k"), second, 1f, 1f, 0f, 0f, 0f, 0f)

        assertSame(second, stored.region)
        assertSame(second, sprites.getOrCreate(SpriteKey("k")) { CachedSprite(TextureRegion(), 1f, 1f, 0f, 0f, 0f, 0f) }.region)
    }
}
