package com.androidstuff.adaptivepdfpro.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.androidstuff.adaptivepdfpro.theme.ColorScheme as PdfColorScheme
import com.androidstuff.adaptivepdfpro.theme.ThemeConfig
import com.androidstuff.adaptivepdfpro.theme.ThemeMode

/**
 * Main theme for the PDF viewer library
 */
@Composable
fun AdaptivePdfTheme(
    themeConfig: ThemeConfig = ThemeConfig.default(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = themeConfig.enableDynamicColors,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (themeConfig.themeMode) {
                ThemeMode.LIGHT -> pdfToMaterialColorScheme(themeConfig.colors, false)
                ThemeMode.DARK -> pdfToMaterialColorScheme(themeConfig.colors, true)
                ThemeMode.SEPIA -> sepiaColorScheme()
                ThemeMode.HIGH_CONTRAST -> highContrastColorScheme()
                ThemeMode.CUSTOM -> pdfToMaterialColorScheme(themeConfig.colors, darkTheme)
                ThemeMode.SYSTEM -> if (darkTheme) {
                    pdfToMaterialColorScheme(PdfColorScheme.dark(), true)
                } else {
                    pdfToMaterialColorScheme(PdfColorScheme.light(), false)
                }
            }
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = pdfTypography(),
        content = content
    )
}

/**
 * Convert PDF ColorScheme to Material3 ColorScheme
 */
private fun pdfToMaterialColorScheme(
    pdfColors: PdfColorScheme,
    darkTheme: Boolean
): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = Color(pdfColors.primary),
            onPrimary = Color(pdfColors.onPrimary),
            primaryContainer = Color(pdfColors.primaryVariant),
            onPrimaryContainer = Color(pdfColors.onPrimary),
            secondary = Color(pdfColors.secondary),
            onSecondary = Color(pdfColors.onSecondary),
            secondaryContainer = Color(pdfColors.secondaryVariant),
            onSecondaryContainer = Color(pdfColors.onSecondary),
            tertiary = Color(pdfColors.secondary),
            onTertiary = Color(pdfColors.onSecondary),
            tertiaryContainer = Color(pdfColors.secondaryVariant),
            onTertiaryContainer = Color(pdfColors.onSecondary),
            error = Color(pdfColors.error),
            onError = Color(pdfColors.onError),
            errorContainer = Color(pdfColors.error).copy(alpha = 0.2f),
            onErrorContainer = Color(pdfColors.onError),
            background = Color(pdfColors.background),
            onBackground = Color(pdfColors.onBackground),
            surface = Color(pdfColors.surface),
            onSurface = Color(pdfColors.onSurface),
            surfaceVariant = Color(pdfColors.surface).copy(alpha = 0.8f),
            onSurfaceVariant = Color(pdfColors.onSurface).copy(alpha = 0.8f),
            outline = Color(pdfColors.divider),
            outlineVariant = Color(pdfColors.divider).copy(alpha = 0.5f),
            scrim = Color(pdfColors.shadowColor),
            inverseSurface = Color(pdfColors.onSurface),
            inverseOnSurface = Color(pdfColors.surface),
            inversePrimary = Color(pdfColors.onPrimary)
        )
    } else {
        lightColorScheme(
            primary = Color(pdfColors.primary),
            onPrimary = Color(pdfColors.onPrimary),
            primaryContainer = Color(pdfColors.primaryVariant).copy(alpha = 0.2f),
            onPrimaryContainer = Color(pdfColors.primary),
            secondary = Color(pdfColors.secondary),
            onSecondary = Color(pdfColors.onSecondary),
            secondaryContainer = Color(pdfColors.secondaryVariant).copy(alpha = 0.2f),
            onSecondaryContainer = Color(pdfColors.secondary),
            tertiary = Color(pdfColors.secondary),
            onTertiary = Color(pdfColors.onSecondary),
            tertiaryContainer = Color(pdfColors.secondaryVariant).copy(alpha = 0.2f),
            onTertiaryContainer = Color(pdfColors.secondary),
            error = Color(pdfColors.error),
            onError = Color(pdfColors.onError),
            errorContainer = Color(pdfColors.error).copy(alpha = 0.2f),
            onErrorContainer = Color(pdfColors.error),
            background = Color(pdfColors.background),
            onBackground = Color(pdfColors.onBackground),
            surface = Color(pdfColors.surface),
            onSurface = Color(pdfColors.onSurface),
            surfaceVariant = Color(pdfColors.surface).copy(alpha = 0.95f),
            onSurfaceVariant = Color(pdfColors.onSurface).copy(alpha = 0.8f),
            outline = Color(pdfColors.divider),
            outlineVariant = Color(pdfColors.divider).copy(alpha = 0.3f),
            scrim = Color(pdfColors.shadowColor),
            inverseSurface = Color(pdfColors.onSurface),
            inverseOnSurface = Color(pdfColors.surface),
            inversePrimary = Color(pdfColors.primaryVariant)
        )
    }
}

/**
 * Sepia color scheme for reading mode
 */
private fun sepiaColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color(0xFF8D6E63),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFA1887F),
        onPrimaryContainer = Color(0xFF3E2723),
        secondary = Color(0xFFA1887F),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFFBCAAA4),
        onSecondaryContainer = Color(0xFF3E2723),
        background = Color(0xFFF5E6D3),
        onBackground = Color(0xFF3E2723),
        surface = Color(0xFFFAF0E6),
        onSurface = Color(0xFF3E2723),
        surfaceVariant = Color(0xFFEFDFCF),
        onSurfaceVariant = Color(0xFF5D4037),
        error = Color(0xFFB00020),
        onError = Color(0xFFFFFFFF)
    )
}

/**
 * High contrast color scheme for accessibility
 */
private fun highContrastColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color(0xFF000000),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF000000),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF000000),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF000000),
        onSecondaryContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFF000000),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF000000),
        surfaceVariant = Color(0xFFF0F0F0),
        onSurfaceVariant = Color(0xFF000000),
        error = Color(0xFFFF0000),
        onError = Color(0xFFFFFFFF),
        outline = Color(0xFF000000),
        outlineVariant = Color(0xFF666666)
    )
}

/**
 * PDF Typography
 */
@Composable
fun pdfTypography(): Typography {
    return Typography(
        displayLarge = MaterialTheme.typography.displayLarge,
        displayMedium = MaterialTheme.typography.displayMedium,
        displaySmall = MaterialTheme.typography.displaySmall,
        headlineLarge = MaterialTheme.typography.headlineLarge,
        headlineMedium = MaterialTheme.typography.headlineMedium,
        headlineSmall = MaterialTheme.typography.headlineSmall,
        titleLarge = MaterialTheme.typography.titleLarge,
        titleMedium = MaterialTheme.typography.titleMedium,
        titleSmall = MaterialTheme.typography.titleSmall,
        bodyLarge = MaterialTheme.typography.bodyLarge,
        bodyMedium = MaterialTheme.typography.bodyMedium,
        bodySmall = MaterialTheme.typography.bodySmall,
        labelLarge = MaterialTheme.typography.labelLarge,
        labelMedium = MaterialTheme.typography.labelMedium,
        labelSmall = MaterialTheme.typography.labelSmall
    )
}

/**
 * PDF Colors object for easy access to theme colors
 */
object PdfColors {
    val LightPrimary = Color(0xFF2196F3)
    val LightPrimaryVariant = Color(0xFF1976D2)
    val LightSecondary = Color(0xFF03DAC6)
    val LightSecondaryVariant = Color(0xFF018786)
    val LightBackground = Color(0xFFF5F5F5)
    val LightSurface = Color(0xFFFFFFFF)
    val LightError = Color(0xFFB00020)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightOnSecondary = Color(0xFF000000)
    val LightOnBackground = Color(0xFF000000)
    val LightOnSurface = Color(0xFF000000)
    val LightOnError = Color(0xFFFFFFFF)
    
    val DarkPrimary = Color(0xFF3F51B5)
    val DarkPrimaryVariant = Color(0xFF303F9F)
    val DarkSecondary = Color(0xFF03DAC6)
    val DarkSecondaryVariant = Color(0xFF03DAC6)
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkError = Color(0xFFCF6679)
    val DarkOnPrimary = Color(0xFFFFFFFF)
    val DarkOnSecondary = Color(0xFF000000)
    val DarkOnBackground = Color(0xFFFFFFFF)
    val DarkOnSurface = Color(0xFFFFFFFF)
    val DarkOnError = Color(0xFF000000)
    
    val SepiaPrimary = Color(0xFF8D6E63)
    val SepiaBackground = Color(0xFFF5E6D3)
    val SepiaSurface = Color(0xFFFAF0E6)
    val SepiaOnBackground = Color(0xFF3E2723)
    
    val Transparent = Color.Transparent
    val Black = Color.Black
    val White = Color.White
    val Gray = Color.Gray
    val LightGray = Color.LightGray
    val DarkGray = Color.DarkGray
}