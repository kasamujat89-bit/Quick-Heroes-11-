package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CricTeal,
    onPrimary = Color.White,
    primaryContainer = CricDarkCard,
    onPrimaryContainer = CricTealLight,
    secondary = CricSecondary,
    onSecondary = Color.White,
    tertiary = CricGold,
    background = CricDarkBackground,
    surface = CricDarkSurface,
    surfaceVariant = CricDarkCard,
    onBackground = CricDarkTextPrimary,
    onSurface = CricDarkTextPrimary,
    outline = CricDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = CricTeal,
    onPrimary = Color.White,
    primaryContainer = CricTealLight,
    onPrimaryContainer = CricTeal,
    secondary = CricSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4E6),
    onSecondaryContainer = CricSecondary,
    tertiary = CricGold,
    background = CricSurfaceLight,
    surface = CricCardLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = CricTextPrimary,
    onSurface = CricTextPrimary,
    outline = CricBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default so the authentic CricHeroes branding shines consistently
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
