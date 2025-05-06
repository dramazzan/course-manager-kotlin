package com.example.coursemanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = AppColors.primary,
    secondary = AppColors.secondary,
    tertiary = AppColors.secondary,
    background = AppColors.backgroundTop,
    surface = AppColors.cardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = AppColors.textPrimary,
    onSurface = AppColors.textPrimary,
    error = AppColors.error,
    onError = Color.White,
    errorContainer = AppColors.errorContainer,
    onErrorContainer = AppColors.onErrorContainer,
    surfaceVariant = AppColors.backgroundBottom,
    onSurfaceVariant = AppColors.textSecondary,
    outline = AppColors.fieldBorder
)

// Dark theme colors - creating a dark version of our palette
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.primary,
    secondary = AppColors.secondary,
    tertiary = AppColors.secondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = AppColors.error,
    onError = Color.White,
    errorContainer = Color(0xFF370000),
    onErrorContainer = Color(0xFFFFB4AB),
    surfaceVariant = Color(0xFF252525),
    onSurfaceVariant = Color(0xFFDDDDDD),
    outline = Color(0xFF767676)
)

@Composable
fun CourseManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    // Optional: Set status bar color to match the theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

