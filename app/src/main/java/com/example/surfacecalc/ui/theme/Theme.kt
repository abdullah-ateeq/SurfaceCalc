package com.example.surfacecalc.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.surfacecalc.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SurfaceCalcPrimaryDark,
    onPrimary = SurfaceCalcOnPrimaryDark,
    primaryContainer = SurfaceCalcPrimaryContainerDark,
    onPrimaryContainer = SurfaceCalcOnPrimaryContainerDark,
    secondary = SurfaceCalcSecondaryDark,
    onSecondary = SurfaceCalcOnSecondaryDark,
    secondaryContainer = SurfaceCalcSecondaryContainerDark,
    onSecondaryContainer = SurfaceCalcOnSecondaryContainerDark,
    tertiary = SurfaceCalcTertiaryDark,
    onTertiary = SurfaceCalcOnTertiaryDark,
    tertiaryContainer = SurfaceCalcTertiaryContainerDark,
    onTertiaryContainer = SurfaceCalcOnTertiaryContainerDark,
    background = SurfaceCalcBackgroundDark,
    onBackground = SurfaceCalcOnBackgroundDark,
    surface = SurfaceCalcSurfaceDark,
    onSurface = SurfaceCalcOnSurfaceDark,
    surfaceVariant = SurfaceCalcSurfaceVariantDark,
    onSurfaceVariant = SurfaceCalcOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = SurfaceCalcPrimaryLight,
    onPrimary = SurfaceCalcOnPrimaryLight,
    primaryContainer = SurfaceCalcPrimaryContainerLight,
    onPrimaryContainer = SurfaceCalcOnPrimaryContainerLight,
    secondary = SurfaceCalcSecondaryLight,
    onSecondary = SurfaceCalcOnSecondaryLight,
    secondaryContainer = SurfaceCalcSecondaryContainerLight,
    onSecondaryContainer = SurfaceCalcOnSecondaryContainerLight,
    tertiary = SurfaceCalcTertiaryLight,
    onTertiary = SurfaceCalcOnTertiaryLight,
    tertiaryContainer = SurfaceCalcTertiaryContainerLight,
    onTertiaryContainer = SurfaceCalcOnTertiaryContainerLight,
    background = SurfaceCalcBackgroundLight,
    onBackground = SurfaceCalcOnBackgroundLight,
    surface = SurfaceCalcSurfaceLight,
    onSurface = SurfaceCalcOnSurfaceLight,
    surfaceVariant = SurfaceCalcSurfaceVariantLight,
    onSurfaceVariant = SurfaceCalcOnSurfaceVariantLight
)

@Composable
fun SurfaceCalcTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

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
