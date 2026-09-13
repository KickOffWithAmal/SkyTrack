package com.skypass.skytrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SkyLightScheme = lightColorScheme(
    primary = SkyRed,
    onPrimary = SkySurface,
    primaryContainer = SkyRedBright,
    onPrimaryContainer = SkySurface,
    secondary = SkyTextSecondary,
    onSecondary = SkySurface,
    secondaryContainer = SkyRedSoft,
    onSecondaryContainer = SkyRedDark,
    background = SkyBackground,
    onBackground = SkyText,
    surface = SkySurface,
    onSurface = SkyText,
    surfaceVariant = SkySurfaceMuted,
    onSurfaceVariant = SkyTextSecondary,
    outline = SkyOutline,
    error = SkyRed
)

@Composable
fun SkyTrackTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SkyLightScheme,
        typography = Typography,
        content = content
    )
}
