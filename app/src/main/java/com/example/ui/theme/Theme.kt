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

private val DarkColorScheme =
  darkColorScheme(
    primary = AgriGreenLight,
    onPrimary = AgriGreenDark,
    primaryContainer = AgriGreenDark,
    onPrimaryContainer = AgriGreenContainer,
    secondary = HarvestGold,
    tertiary = WaterBlue,
    background = Color(0xFF111411),
    surface = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE2E3DE),
    onSurface = Color(0xFFE2E3DE),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = AgriOnGreenContainer,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = Color(0xFF261A00),
    tertiary = WaterBlue,
    onTertiary = Color.White,
    tertiaryContainer = WaterBlueContainer,
    onTertiaryContainer = Color(0xFF001D32),
    background = SurfaceLight,
    surface = SurfaceCard,
    surfaceVariant = SurfaceElevated,
    outline = SurfaceBorder,
    outlineVariant = SurfaceBorder,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
