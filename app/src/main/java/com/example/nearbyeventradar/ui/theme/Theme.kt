package com.example.nearbyeventradar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KindredSpiritsColorScheme = lightColorScheme(
    primary = SunnyYellowDark,
    onPrimary = SurfaceWhite,
    primaryContainer = SunnyYellow,
    onPrimaryContainer = OnSunnyYellow,
    inversePrimary = Color(0xFFE7C353),

    secondary = SoftCoralDark,
    onSecondary = SurfaceWhite,
    secondaryContainer = SoftCoralContainer,
    onSecondaryContainer = OnSoftCoral,

    tertiary = VibrantMintDark,
    onTertiary = SurfaceWhite,
    tertiaryContainer = VibrantMintContainer,
    onTertiaryContainer = OnVibrantMint,

    background = CreamBg,
    onBackground = WarmGrayText,
    surface = SurfaceCream,
    onSurface = WarmGrayText,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = WarmGrayTextSecondary,

    outline = OutlineWarm,
    outlineVariant = OutlineVariant
)

@Composable
fun NearbyEventRadarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = KindredSpiritsColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CreamBg.toArgb()
            window.navigationBarColor = SurfaceCream.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

