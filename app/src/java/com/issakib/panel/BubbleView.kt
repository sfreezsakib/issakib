package com.issakib.panel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class BubbleView(
    context: Context,
    private val onClick: () -> Unit,
    private val onMove: (Int, Int) -> Unit
) : View(context) {

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0C1524") }
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor("#00E5FF")
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00E5FF")
        textSize = 34f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private var downX = 0f; private var downY = 0f
    private var startX = 0; private var startY = 0
    private var moved = false

    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        glow.shader = RadialGradient(
            w / 2f, h / 2f, w / 2f,
            intArrayOf(Color.parseColor("#8000E5FF"), Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f; val cy = height / 2f
        val r = width / 2f - 4f
        canvas.drawCircle(cx, cy, r + 4f, glow)
        canvas.drawCircle(cx, cy, r, fill)
        canvas.drawCircle(cx, cy, r, ring)
        canvas.drawText("IS", cx, cy + 12f, text)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.rawX; downY = e.rawY
                startX = Prefs.bubbleX(context); startY = Prefs.bubbleY(context)
                moved = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.rawX - downX; val dy = e.rawY - downY
                if (dx * dx + dy * dy > 64) moved = true
                if (moved) {
                    val nx = startX + dx.toInt(); val ny = startY + dy.toInt()
                    val p = layoutParams as android.view.WindowManager.LayoutParams
                    p.x = nx; p.y = ny
                    (context as OverlayService).let { }
                    onMove(nx, ny)
                    parent?.let { }
                    windowManagerUpdate(nx, ny)
                }
            }
            MotionEvent.ACTION_UP -> if (!moved) onClick()
        }
        return true
    }

    private var cachedWm: android.view.WindowManager? = null

    private fun windowManagerUpdate(x: Int, y: Int) {
        if (cachedWm == null) {
            cachedWm = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        }
        val p = layoutParams as android.view.WindowManager.LayoutParams
        p.x = x; p.y = y
        runCatching { cachedWm?.updateViewLayout(this, p) }
    }
}