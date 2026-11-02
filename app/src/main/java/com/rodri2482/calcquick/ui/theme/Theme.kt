package com.rodri2482.calcquick.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rodri2482.calcquick.viewmodel.ThemeStyle

private val NeonColorScheme = darkColorScheme(
    primary = NeonPrimary,
    onPrimary = EqualsBtnTextDark,
    secondary = NeonSecondary,
    onSecondary = Color.White,
    tertiary = NeonTertiary,
    background = NeonBackground,
    surface = NeonSurface,
    surfaceVariant = NeonSurfaceVariant,
    onBackground = Color(0xFFECEFF4),
    onSurface = Color(0xFFF0F4F8)
)

private val OledColorScheme = darkColorScheme(
    primary = OledPrimary,
    onPrimary = Color.Black,
    secondary = Color(0xFFFF9800),
    background = OledBackground,
    surface = OledSurface,
    surfaceVariant = OledSurfaceVariant,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = Color.White,
    tertiary = LightTertiary,
    background = LightBg,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF0F172A)
)

private val MidnightColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = Color(0xFF0B132B),
    secondary = Color(0xFF5BC0BE),
    background = MidnightBg,
    surface = MidnightSurface,
    surfaceVariant = Color(0xFF3A506B),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ApexCalcTheme(
    themeStyle: ThemeStyle = ThemeStyle.NEON_CYBER,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeStyle) {
        ThemeStyle.NEON_CYBER -> NeonColorScheme
        ThemeStyle.OLED_DARK -> OledColorScheme
        ThemeStyle.CLEAN_LIGHT -> LightColorScheme
        ThemeStyle.MIDNIGHT_BLUE -> MidnightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
