package com.issakib.panel

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val FILE = "is_sakib_panel"
    private fun sp(c: Context): SharedPreferences =
        c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    const val STYLE = "style"
    const val COLOR = "color"
    const val SIZE = "size"
    const val THICK = "thick"
    const val GAP = "gap"
    const val OPACITY = "opacity"
    const val POS_X = "pos_x"
    const val POS_Y = "pos_y"
    const val LOCKED = "locked"
    const val MOVE_MODE = "move_mode"
    const val BUBBLE = "bubble"
    const val BUBBLE_X = "bubble_x"
    const val BUBBLE_Y = "bubble_y"

    fun style(c: Context) = sp(c).getInt(STYLE, 0)
    fun color(c: Context) = sp(c).getInt(COLOR, 0xFF00E5FF.toInt())
    fun size(c: Context) = sp(c).getFloat(SIZE, 28f)
    fun thick(c: Context) = sp(c).getFloat(THICK, 2.5f)
    fun gap(c: Context) = sp(c).getFloat(GAP, 5f)
    fun opacity(c: Context) = sp(c).getInt(OPACITY, 100)
    fun posX(c: Context) = sp(c).getInt(POS_X, 0)
    fun posY(c: Context) = sp(c).getInt(POS_Y, 0)
    fun locked(c: Context) = sp(c).getBoolean(LOCKED, true)
    fun moveMode(c: Context) = sp(c).getBoolean(MOVE_MODE, false)
    fun bubble(c: Context) = sp(c).getBoolean(BUBBLE, true)
    fun bubbleX(c: Context) = sp(c).getInt(BUBBLE_X, 0)
    fun bubbleY(c: Context) = sp(c).getInt(BUBBLE_Y, 300)

    fun putInt(c: Context, k: String, v: Int) = sp(c).edit().putInt(k, v).apply()
    fun putFloat(c: Context, k: String, v: Float) = sp(c).edit().putFloat(k, v).apply()
    fun putBool(c: Context, k: String, v: Boolean) = sp(c).edit().putBoolean(k, v).apply()
}