package com.issakib.panel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@SuppressLint("ViewConstructor")
class CrosshairView(
    context: Context,
    private val onMoved: (Int, Int) -> Unit
) : View(context) {

    private var downX = 0f
    private var downY = 0f
    private var startX = 0
    private var startY = 0
    private var dragging = false

    fun refresh() {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val ctx = context
        val radius = Prefs.size(ctx)
        val color = Color(Prefs.color(ctx)).copy(alpha = Prefs.opacity(ctx) / 100f)

        CanvasDrawScope().draw(
            Density(ctx.resources.displayMetrics.density),
            LayoutDirection.Ltr,
            androidx.compose.ui.graphics.Canvas(canvas),
            androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())
        ) {
            drawCrosshair(
                style = Prefs.style(ctx),
                color = color,
                thickness = Prefs.thick(ctx),
                radius = radius,
                gap = Prefs.gap(ctx)
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!Prefs.moveMode(context)) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX; downY = event.rawY
                startX = Prefs.posX(context); startY = Prefs.posY(context)
                dragging = true
            }
            MotionEvent.ACTION_MOVE -> if (dragging) {
                val nx = startX + (event.rawX - downX).toInt()
                val ny = startY + (event.rawY - downY).toInt()
                onMoved(nx, ny)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> dragging = false
        }
        return true
    }
}