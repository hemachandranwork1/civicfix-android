package com.civicfix.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = CivicAccent,
    onPrimary        = CivicNavy,
    secondary        = CivicGreen,
    onSecondary      = CivicNavy,
    background       = CivicNavy,
    onBackground     = Color.White,
    surface          = CivicDeepBlue,
    onSurface        = Color.White,
    surfaceVariant   = CivicBlue,
    onSurfaceVariant = CivicMuted,
    outline          = CivicBorder,
    error            = StatusRejected
)

@Composable
fun CivicFixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = CivicTypography,
        content     = content
    )
}
