package life.sim.simulator.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2

data class CachedSprite(
    val region: TextureRegion,
    val width: Float,
    val height: Float,
    val tileOriginX: Float,
    val tileOriginY: Float,
    val rotationOriginX: Float,
    val rotationOriginY: Float,
)

class Sprites {
    private val sprites = mutableMapOf<SpriteKey, CachedSprite>()
    private val ownedTextures = mutableSetOf<Texture>()

    fun getOrCreate(key: SpriteKey, generator: () -> CachedSprite): CachedSprite =
        sprites.getOrPut(key, generator)

    fun draw(
        key: SpriteKey,
        batch: SpriteBatch,
        position: Vector2,
        rotationDegrees: Float = 0f,
    ) {
        val sprite = requireNotNull(sprites[key]) { "Sprite not found for key: $key" }
        val x = position.x - sprite.tileOriginX
        val y = position.y - sprite.tileOriginY
        batch.draw(
            sprite.region,
            x,
            y,
            sprite.rotationOriginX,
            sprite.rotationOriginY,
            sprite.width,
            sprite.height,
            1f,
            1f,
            rotationDegrees,
        )
    }

    fun putPixmap(key: SpriteKey, pixmap: Pixmap): CachedSprite {
        val texture = try {
            Texture(pixmap)
        } finally {
            pixmap.dispose()
        }

        val region = TextureRegion(texture)
        return try {
            putRegion(
                key,
                region,
                region.regionWidth.toFloat(),
                region.regionHeight.toFloat(),
                tileOriginX = 0f,
                tileOriginY = 0f,
                rotationOriginX = 0f,
                rotationOriginY = 0f,
                ownsTexture = true,
            )
        } catch (error: Throwable) {
            texture.dispose()
            throw error
        }
    }

    fun putRegion(
        key: SpriteKey,
        region: TextureRegion,
        width: Float,
        height: Float,
        tileOriginX: Float,
        tileOriginY: Float,
        rotationOriginX: Float,
        rotationOriginY: Float,
        ownsTexture: Boolean = false,
    ): CachedSprite {
        val sprite = CachedSprite(region, width, height, tileOriginX, tileOriginY, rotationOriginX, rotationOriginY)
        val previous = sprites.put(key, sprite)
        if (previous != null) {
            maybeDisposeOwned(previous.region.texture)
        }
        if (ownsTexture) {
            region.texture?.let(ownedTextures::add)
        }
        return sprite
    }

    fun renderToSprite(
        key: SpriteKey,
        width: Int,
        height: Int,
        tileOriginX: Float,
        tileOriginY: Float,
        rotationOriginX: Float,
        rotationOriginY: Float,
        render: () -> Unit,
    ): CachedSprite {
        val safeWidth = width.coerceAtLeast(1)
        val safeHeight = height.coerceAtLeast(1)

        val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, safeWidth, safeHeight, false)
        val pixels = Pixmap(safeWidth, safeHeight, Pixmap.Format.RGBA8888)
        try {
            frameBuffer.begin()
            Gdx.gl.glViewport(0, 0, safeWidth, safeHeight)
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            render()
            Gdx.gl.glFlush()
            Gdx.gl.glReadPixels(0, 0, safeWidth, safeHeight, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels.pixels)
        } finally {
            frameBuffer.end()
            frameBuffer.dispose()
        }

        val texture = try {
            Texture(pixels)
        } finally {
            pixels.dispose()
        }
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val region = TextureRegion(texture).apply { flip(false, true) }
        return putRegion(
            key,
            region,
            safeWidth.toFloat(),
            safeHeight.toFloat(),
            tileOriginX,
            tileOriginY,
            rotationOriginX,
            rotationOriginY,
            ownsTexture = true,
        )
    }

    fun dispose() {
        sprites.values.forEach { sprite ->
            maybeDisposeOwned(sprite.region.texture)
        }
        sprites.clear()
        ownedTextures.clear()
    }

    private fun maybeDisposeOwned(texture: Texture?) {
        if (texture != null && ownedTextures.remove(texture)) {
            texture.dispose()
        }
    }

    internal fun ownedTextureCount(): Int = ownedTextures.size
}
