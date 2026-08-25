package com.reneprojects.ui.theme

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

// Primary - Rojo comercial
val RedPrimary = Color(0xFFF20D22)
val RedPrimaryDark = Color(0xFFC70016)
val RedPrimaryLight = Color(0xFFFF4D5E)

// Secondary - Azul eléctrico
val BlueSecondary = Color(0xFF075CE5)
val BlueSecondaryDark = Color(0xFF0042B8)
val BlueSecondaryLight = Color(0xFF4A8CFF)

// Colores adicionales
val PinkAccent = Color(0xFFFF0A66)
val OrangeRating = Color(0xFFFFA000)

// Background / Surface
val BackgroundLight = Color(0xFFF8F9FC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F3F6)

// Texto
val TextPrimary = Color(0xFF18191C)
val TextSecondary = Color(0xFF686B73)

// Bordes
val BorderLight = Color(0xFFE1E3E8)

// Dark theme
val BackgroundDark = Color(0xFF111216)
val SurfaceDark = Color(0xFF1B1C21)
val SurfaceVariantDark = Color(0xFF25262C)
private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = SurfaceLight,

    primaryContainer = RedPrimaryLight,
    onPrimaryContainer = TextPrimary,

    secondary = BlueSecondary,
    onSecondary = SurfaceLight,

    secondaryContainer = BlueSecondaryLight,
    onSecondaryContainer = SurfaceLight,

    tertiary = PinkAccent,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceLight,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,

    outline = BorderLight,

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimaryLight,
    onPrimary = Color.White,

    secondary = BlueSecondaryLight,
    onSecondary = Color.White,

    tertiary = PinkAccent,

    background = BackgroundDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCACACE),

    outline = Color(0xFF44464D),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun RenEcommerceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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