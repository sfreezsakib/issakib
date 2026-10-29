package com.issakib.panel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

const val STYLE_COUNT = 9

val STYLE_NAMES = listOf(
    "Dot", "Plus", "Ring", "Gapped", "T", "Chevron", "Square", "X", "Ring Dot"
)

fun DrawScope.drawCrosshair(
    style: Int,
    color: Color,
    thickness: Float,
    radius: Float,
    gap: Float
) {
    val c = center
    val s = Stroke(width = thickness)
    val half = radius

    when (style) {
        0 -> drawCircle(color, radius = thickness * 1.6f, center = c)

        1 -> {
            drawLine(color, Offset(c.x, c.y - half), Offset(c.x, c.y - gap), s.width)
            drawLine(color, Offset(c.x, c.y + gap), Offset(c.x, c.y + half), s.width)
            drawLine(color, Offset(c.x - half, c.y), Offset(c.x - gap, c.y), s.width)
            drawLine(color, Offset(c.x + gap, c.y), Offset(c.x + half, c.y), s.width)
        }

        2 -> drawCircle(color, radius = half, center = c, style = s)

        3 -> {
            drawLine(color, Offset(c.x, c.y - half), Offset(c.x, c.y - gap * 2f), s.width)
            drawLine(color, Offset(c.x, c.y + gap * 2f), Offset(c.x, c.y + half), s.width)
            drawLine(color, Offset(c.x - half, c.y), Offset(c.x - gap * 2f, c.y), s.width)
            drawLine(color, Offset(c.x + gap * 2f, c.y), Offset(c.x + half, c.y), s.width)
            drawCircle(color, radius = thickness * 1.4f, center = c)
        }

        4 -> {
            drawLine(color, Offset(c.x - half, c.y), Offset(c.x + half, c.y), s.width)
            drawLine(color, Offset(c.x, c.y), Offset(c.x, c.y + half), s.width)
        }

        5 -> {
            val p = Path().apply {
                moveTo(c.x - half, c.y + half * 0.6f)
                lineTo(c.x, c.y - half * 0.6f)
                lineTo(c.x + half, c.y + half * 0.6f)
            }
            drawPath(p, color, style = s)
        }

        6 -> drawRect(
            color,
            topLeft = Offset(c.x - half, c.y - half),
            size = Size(half * 2f, half * 2f),
            style = s
        )

        7 -> {
            drawLine(color, Offset(c.x - half, c.y - half), Offset(c.x - half * 0.3f, c.y - half * 0.3f), s.width)
            drawLine(color, Offset(c.x + half, c.y - half), Offset(c.x + half * 0.3f, c.y - half * 0.3f), s.width)
            drawLine(color, Offset(c.x - half, c.y + half), Offset(c.x - half * 0.3f, c.y + half * 0.3f), s.width)
            drawLine(color, Offset(c.x + half, c.y + half), Offset(c.x + half * 0.3f, c.y + half * 0.3f), s.width)
        }

        8 -> {
            drawCircle(color, radius = half, center = c, style = s)
            drawCircle(color, radius = thickness * 1.6f, center = c)
        }
    }
}

fun crosshairBounds(radius: Float): Rect =
    Rect(-radius - 12f, -radius - 12f, radius + 12f, radius + 12f)