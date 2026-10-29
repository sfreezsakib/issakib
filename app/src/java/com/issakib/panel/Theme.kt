package com.issakib.panel

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Bg = Color(0xFF050A14)
val Surface1 = Color(0xFF0C1524)
val Surface2 = Color(0xFF132033)
val Neon = Color(0xFF00E5FF)
val Violet = Color(0xFF7C4DFF)
val Gold = Color(0xFFFFC400)
val TextMain = Color(0xFFEAF4FF)
val TextDim = Color(0xFF7A8CA3)

private val Scheme = darkColorScheme(
    primary = Neon,
    secondary = Violet,
    tertiary = Gold,
    background = Bg,
    surface = Surface1,
    onPrimary = Bg,
    onBackground = TextMain,
    onSurface = TextMain
)

private val Type = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 1.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 1.5.sp)
)

private val Shape = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun PanelTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Type, shapes = Shape, content = content)
}