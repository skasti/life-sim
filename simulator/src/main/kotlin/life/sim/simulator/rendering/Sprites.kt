package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2

class Sprites {
    private val regions = mutableMapOf<SpriteKey, TextureRegion>()
    private val ownedTextures = mutableSetOf<Texture>()

    fun getOrCreate(key: SpriteKey, generator: () -> TextureRegion): TextureRegion =
        regions.getOrPut(key, generator)

    fun draw(
        key: SpriteKey,
        batch: SpriteBatch,
        position: Vector2,
        width: Float,
        height: Float,
        rotationDegrees: Float = 0f,
    ) {
        val region = requireNotNull(regions[key]) { "Sprite not found for key: $key" }
        batch.draw(region, position.x, position.y, width * 0.5f, height * 0.5f, width, height, 1f, 1f, rotationDegrees)
    }

    fun putPixmap(key: SpriteKey, pixmap: Pixmap): TextureRegion {
        val texture = Texture(pixmap)
        val region = TextureRegion(texture)
        pixmap.dispose()
        return putRegion(key, region, ownsTexture = true)
    }

    fun putRegion(key: SpriteKey, region: TextureRegion, ownsTexture: Boolean = false): TextureRegion {
        val previous = regions.put(key, region)
        if (previous != null) {
            maybeDisposeOwned(previous.texture)
        }
        if (ownsTexture) {
            region.texture?.let(ownedTextures::add)
        }
        return region
    }

    fun renderToSprite(
        key: SpriteKey,
        width: Int,
        height: Int,
        render: (TextureRegion) -> Unit,
    ): TextureRegion {
        val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, width.coerceAtLeast(1), height.coerceAtLeast(1), false)
        frameBuffer.begin()
        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val region = TextureRegion(texture).apply { flip(false, true) }
        render(region)
        frameBuffer.end()
        frameBuffer.dispose()
        return putRegion(key, region, ownsTexture = true)
    }

    fun dispose() {
        regions.values.forEach { region ->
            maybeDisposeOwned(region.texture)
        }
        regions.clear()
        ownedTextures.clear()
    }

    private fun maybeDisposeOwned(texture: Texture?) {
        if (texture != null && ownedTextures.remove(texture)) {
            texture.dispose()
        }
    }

    internal fun ownedTextureCount(): Int = ownedTextures.size
}
