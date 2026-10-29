package com.issakib.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Palette = listOf(
    0xFF00E5FF, 0xFF7C4DFF, 0xFFFFC400, 0xFF00E676,
    0xFFFF5252, 0xFFFFFFFF, 0xFFFF4081, 0xFF40C4FF
)

@Composable
fun PanelScreen(onClose: () -> Unit, onRefresh: () -> Unit) {
    val ctx = LocalContext.current
    var style by remember { mutableStateOf(Prefs.style(ctx)) }
    var color by remember { mutableStateOf(Prefs.color(ctx)) }
    var size by remember { mutableStateOf(Prefs.size(ctx)) }
    var thick by remember { mutableStateOf(Prefs.thick(ctx)) }
    var gap by remember { mutableStateOf(Prefs.gap(ctx)) }
    var opacity by remember { mutableStateOf(Prefs.opacity(ctx).toFloat()) }
    var moveMode by remember { mutableStateOf(Prefs.moveMode(ctx)) }

    fun apply() {
        Prefs.putInt(ctx, Prefs.STYLE, style)
        Prefs.putInt(ctx, Prefs.COLOR, color)
        Prefs.putFloat(ctx, Prefs.SIZE, size)
        Prefs.putFloat(ctx, Prefs.THICK, thick)
        Prefs.putFloat(ctx, Prefs.GAP, gap)
        Prefs.putInt(ctx, Prefs.OPACITY, opacity.toInt())
        Prefs.putBool(ctx, Prefs.MOVE_MODE, moveMode)
        onRefresh()
    }

    PanelTheme {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xF00C1524), Color(0xF0050A14)))
                )
                .border(1.dp, Brush.verticalGradient(listOf(Neon.copy(0.5f), Violet.copy(0.3f))), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("IS SAKIB", color = Neon, fontWeight = FontWeight.Black, fontSize = 17.sp, letterSpacing = 3.sp)
                    Text("PANEL", color = TextDim, fontSize = 10.sp, letterSpacing = 4.sp)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, null, tint = TextDim)
                }
            }

            Spacer(Modifier.height(14.dp))

            SectionLabel("CROSSHAIR")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until STYLE_COUNT) {
                    StyleChip(i, i == style, color) { style = i; apply() }
                }
            }

            Spacer(Modifier.height(14.dp))
            SectionLabel("COLOR")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Palette.forEach { c ->
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(c))
                            .border(
                                if (c == color) 2.dp else 1.dp,
                                if (c == color) Color.White else Color.White.copy(0.2f),
                                CircleShape
                            )
                            .clickable { color = c; apply() }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            SliderRow("SIZE", size, 12f..70f) { size = it; apply() }
            SliderRow("THICKNESS", thick, 1f..7f) { thick = it; apply() }
            SliderRow("GAP", gap, 0f..18f) { gap = it; apply() }
            SliderRow("OPACITY", opacity, 20f..100f) { opacity = it; apply() }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("MOVE MODE", color = TextDim, fontSize = 11.sp, letterSpacing = 1.5.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = moveMode,
                    onCheckedChange = { moveMode = it; apply() },
                    colors = SwitchDefaults.colors(checkedTrackColor = Neon)
                )
            }
            Text(
                "চালু থাকলে ক্রসহেয়ার টেনে পজিশন বদলাও, তারপর বন্ধ করো",
                color = TextDim.copy(0.7f), fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SectionLabel(t: String) {
    Text(t, color = TextDim, fontSize = 10.sp, letterSpacing = 2.sp, modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun StyleChip(index: Int, selected: Boolean, color: Int, onClick: () -> Unit) {
    val c = Color(color)
    Box(
        Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) c.copy(0.18f) else Surface2)
            .border(1.dp, if (selected) c else Color.Transparent, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(Modifier.size(18.dp)) {
            drawCrosshair(index, if (selected) c else TextDim, 1.4f, this.size.minDimension / 2f - 1f, 2f)
        }
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextDim, fontSize = 10.sp, letterSpacing = 1.5.sp, modifier = Modifier.width(80.dp))
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = Neon, activeTrackColor = Neon, inactiveTrackColor = Surface2)
        )
        Text(value.toInt().toString(), color = Neon, fontSize = 11.sp, modifier = Modifier.width(28.dp))
    }
}