package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TokokuhMaroonPrimary,
    onPrimary = TokokuhWhite,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = TokokuhMaroonDark,
    secondary = TokokuhMaroonLight,
    onSecondary = TokokuhWhite,
    tertiary = TokokuhAccentYellow,
    onTertiary = TokokuhBlack,
    background = TokokuhBackground,
    onBackground = TokokuhTextPrimary,
    surface = TokokuhSurface,
    onSurface = TokokuhTextPrimary,
    surfaceVariant = TokokuhSurfaceVariant,
    onSurfaceVariant = TokokuhTextSecondary,
    outline = TokokuhBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF690005),
    primaryContainer = TokokuhMaroonPrimary,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = TokokuhAccentYellow,
    onSecondary = TokokuhBlack,
    background = Color(0xFF1A1110),
    onBackground = Color(0xFFF1DFDD),
    surface = Color(0xFF201A1A),
    onSurface = Color(0xFFEDE0DF),
    surfaceVariant = Color(0xFF534341),
    onSurfaceVariant = Color(0xFFD8C2BF),
    outline = Color(0xFFA08C8A)
)

@Composable
fun TokokuhRestoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Always preserve brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
