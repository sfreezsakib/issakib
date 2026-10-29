package com.issakib.panel

data class SensiPreset(
    val name: String,
    val tag: String,
    val values: Map<String, Int>
)

val PRESETS = listOf(
    SensiPreset(
        "Close Range", "CQC",
        mapOf("General" to 96, "Red Dot" to 92, "2x" to 88, "4x" to 74, "AWM" to 60, "Free Look" to 90)
    ),
    SensiPreset(
        "Mid Range", "BALANCED",
        mapOf("General" to 78, "Red Dot" to 74, "2x" to 70, "4x" to 62, "AWM" to 52, "Free Look" to 72)
    ),
    SensiPreset(
        "Sniper", "LONG",
        mapOf("General" to 62, "Red Dot" to 58, "2x" to 55, "4x" to 48, "AWM" to 40, "Free Look" to 58)
    ),
    SensiPreset(
        "Four Finger", "CLAW",
        mapOf("General" to 90, "Red Dot" to 86, "2x" to 82, "4x" to 70, "AWM" to 58, "Free Look" to 84)
    )
)

fun SensiPreset.asText(): String = buildString {
    appendLine("IS SAKIB PANEL — ${name} (${tag})")
    values.forEach { (k, v) -> appendLine("$k: $v") }
    append("DPI suggestion: 480-560 (developer options)")
}