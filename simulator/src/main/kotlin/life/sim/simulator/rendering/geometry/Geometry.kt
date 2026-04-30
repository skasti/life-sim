package life.sim.simulator.rendering.geometry

import com.badlogic.gdx.graphics.Color
import life.sim.simulator.rendering.RenderContext

internal data class Geometry(
    val filledTriangles: List<Triangle>,
    val filledRects: List<Rect>,
    val filledArcs: List<Arc>,
    val arcs: List<Arc>,
    val triangles: List<Triangle>,
    val lines: List<Line>,
    val polygons: List<Polygon>,
)

internal fun Geometry.render(context: RenderContext, color: Color) {
    this.filledRects.forEach { rect ->
        context.drawFilledRect(rect.x, rect.y, rect.width, rect.height, color)
    }
    this.filledTriangles.forEach { triangle ->
        context.drawFilledTriangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, triangle.x3, triangle.y3, color)
    }
    this.filledArcs.forEach { arc ->
        context.drawFilledArc(arc.x, arc.y, arc.radius, arc.startDegrees, arc.degrees, color)
    }
    this.arcs.forEach { arc ->
        context.drawArc(arc.x, arc.y, arc.radius, arc.startDegrees, arc.degrees, color, arc.lineWidth)
    }
    this.triangles.forEach { triangle ->
        context.drawTriangle(triangle.x1, triangle.y1, triangle.x2, triangle.y2, triangle.x3, triangle.y3, color)
    }
    this.lines.forEach { line ->
        context.drawLine(line.a, line.b, line.width, color)
    }
    this.polygons.forEach { polygon ->
        context.drawPolygon(polygon.vertices, polygon.drawMode, color)
    }
}

