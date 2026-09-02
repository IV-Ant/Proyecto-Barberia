package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BarberGoldPrimary,
    onPrimary = TextDark,
    primaryContainer = BarberGoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = BarberCyan,
    onSecondary = TextDark,
    tertiary = BarberCrimson,
    background = SlateDarkBackground,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder
)

@Composable
fun BarberHubTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
