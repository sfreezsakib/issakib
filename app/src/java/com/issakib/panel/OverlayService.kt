package com.issakib.panel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewTreeLifecycleOwner
import androidx.compose.ui.platform.setViewTreeLifecycleOwner
import androidx.compose.ui.platform.setViewTreeSavedStateRegistryOwner
import androidx.compose.ui.platform.setViewTreeViewModelStoreOwner
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner as _unused

class OverlayService : Service() {

    companion object {
        const val CHANNEL = "is_sakib_panel"
        const val NOTIF_ID = 77
        @Volatile var running = false
    }

    private lateinit var wm: WindowManager
    private var crosshair: CrosshairView? = null
    private var bubble: android.view.View? = null
    private var panel: ComposeView? = null
    private var owner: OverlayLifecycleOwner? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        running = true
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForeground(NOTIF_ID, buildNotification())
        addCrosshair()
        if (Prefs.bubble(this)) addBubble()
    }

    private fun baseParams(
        w: Int, h: Int, x: Int, y: Int,
        focusable: Boolean, touchable: Boolean
    ): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
        var flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        if (!focusable) flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        if (!touchable) flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        return WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.START
            this.x = x; this.y = y
        }
    }

    private fun addCrosshair() {
        val radius = Prefs.size(this).toInt() + 12
        val size = radius * 2
        val v = CrosshairView(this) { nx, ny ->
            Prefs.putInt(this, Prefs.POS_X, nx)
            Prefs.putInt(this, Prefs.POS_Y, ny)
            crosshair?.let {
                val p = it.layoutParams as WindowManager.LayoutParams
                p.x = nx; p.y = ny
                wm.updateViewLayout(it, p)
            }
        }
        val p = baseParams(
            size, size,
            Prefs.posX(this), Prefs.posY(this),
            focusable = false,
            touchable = Prefs.moveMode(this)
        )
        wm.addView(v, p)
        crosshair = v
    }

    private fun addBubble() {
        val v = BubbleView(this,
            onClick = { togglePanel() },
            onMove = { x, y ->
                Prefs.putInt(this, Prefs.BUBBLE_X, x)
                Prefs.putInt(this, Prefs.BUBBLE_Y, y)
            })
        val p = baseParams(
            56.dp(), 56.dp(),
            Prefs.bubbleX(this), Prefs.bubbleY(this),
            focusable = false, touchable = true
        )
        wm.addView(v, p)
        bubble = v
    }

    private fun togglePanel() {
        if (panel != null) { removePanel(); return }
        val o = OverlayLifecycleOwner()
        owner = o
        val cv = ComposeView(this).apply {
            setViewTreeLifecycleOwner(o)
            setViewTreeSavedStateRegistryOwner(o)
            setViewTreeViewModelStoreOwner(o)
            setContent { PanelScreen(onClose = { togglePanel() }, onRefresh = { refreshCrosshair() }) }
        }
        val p = baseParams(
            300.dp(), WindowManager.LayoutParams.WRAP_CONTENT,
            0, 80, focusable = false, touchable = true
        ).apply { gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL }
        wm.addView(cv, p)
        o.start()
        panel = cv
    }

    private fun removePanel() {
        panel?.let { wm.removeView(it) }
        owner?.stop()
        owner = null
        panel = null
    }

    fun refreshCrosshair() {
        crosshair?.let {
            val p = it.layoutParams as WindowManager.LayoutParams
            p.width = (Prefs.size(this).toInt() + 12) * 2
            p.height = p.width
            val move = Prefs.moveMode(this)
            var flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            if (!move) flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            p.flags = flags
            wm.updateViewLayout(it, p)
            it.refresh()
        }
    }

    private fun Int.dp() = (this * resources.displayMetrics.density).toInt()

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "IS SAKIB PANEL", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_panel)
            .setContentTitle("IS SAKIB PANEL")
            .setContentText("Crosshair active")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        running = false
        crosshair?.let { runCatching { wm.removeView(it) } }
        bubble?.let { runCatching { wm.removeView(it) } }
        panel?.let { runCatching { wm.removeView(it) } }
        owner?.stop()
        crosshair = null; bubble = null; panel = null; owner = null
        super.onDestroy()
    }
}