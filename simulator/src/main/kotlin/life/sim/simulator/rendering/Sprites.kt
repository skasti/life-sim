package life.sim.simulator.rendering

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class Sprites {
    private val regions = mutableMapOf<SpriteKey, TextureRegion>()

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
        regions[key] = region
        return region
    }

    fun dispose() {
        regions.values.forEach { it.texture.dispose() }
        regions.clear()
    }
}
