package com.tensormind.binme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = WhitePrimary,
    primaryContainer = GreenSurface,
    onPrimaryContainer = GreenDark,
    secondary = GreenSecondary,
    onSecondary = BlackPrimary,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenDark,
    tertiary = YellowPrimary,
    onTertiary = BlackPrimary,
    background = BackgroundMain,
    onBackground = BlackPrimary,
    surface = WhitePrimary,
    onSurface = BlackPrimary,
    surfaceVariant = GrayLight,
    onSurfaceVariant = GrayDark,
    error = ErrorRed,
    onError = WhitePrimary,
    outline = GrayMedium,
)

@Composable
fun BinMeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
