package com.androidstuff.adaptivepdfpro.theme

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

/**
 * Theme configuration for PDF viewer
 */
@Parcelize
data class ThemeConfig(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val colors: ColorScheme = ColorScheme.light(),
    val customColors: Map<String, Int> = emptyMap(),
    val enableSystemTheme: Boolean = false,
    val enableDynamicColors: Boolean = false
) : Parcelable {
    class Builder {
        private var themeMode = ThemeMode.LIGHT
        private var colors = ColorScheme.light()
        private val customColors = mutableMapOf<String, Int>()
        private var enableSystemTheme = false
        private var enableDynamicColors = false

        fun themeMode(mode: ThemeMode) = apply { themeMode = mode }
        fun lightTheme() = apply {
            themeMode = ThemeMode.LIGHT
            colors = ColorScheme.light()
        }
        fun darkTheme() = apply {
            themeMode = ThemeMode.DARK
            colors = ColorScheme.dark()
        }
        fun sepiaTheme() = apply {
            themeMode = ThemeMode.SEPIA
            colors = ColorScheme.sepia()
        }
        fun highContrastTheme() = apply {
            themeMode = ThemeMode.HIGH_CONTRAST
            colors = ColorScheme.highContrast()
        }
        fun customTheme(colorScheme: ColorScheme) = apply {
            themeMode = ThemeMode.CUSTOM
            colors = colorScheme
        }
        fun primaryColor(@ColorInt color: Int) = apply { colors = colors.copy(primary = color) }
        fun primaryVariantColor(@ColorInt color: Int) = apply { colors = colors.copy(primaryVariant = color) }
        fun secondaryColor(@ColorInt color: Int) = apply { colors = colors.copy(secondary = color) }
        fun secondaryVariantColor(@ColorInt color: Int) = apply { colors = colors.copy(secondaryVariant = color) }
        fun backgroundColor(@ColorInt color: Int) = apply { colors = colors.copy(background = color) }
        fun surfaceColor(@ColorInt color: Int) = apply { colors = colors.copy(surface = color) }
        fun errorColor(@ColorInt color: Int) = apply { colors = colors.copy(error = color) }
        fun onPrimaryColor(@ColorInt color: Int) = apply { colors = colors.copy(onPrimary = color) }
        fun onSecondaryColor(@ColorInt color: Int) = apply { colors = colors.copy(onSecondary = color) }
        fun onBackgroundColor(@ColorInt color: Int) = apply { colors = colors.copy(onBackground = color) }
        fun onSurfaceColor(@ColorInt color: Int) = apply { colors = colors.copy(onSurface = color) }
        fun onErrorColor(@ColorInt color: Int) = apply { colors = colors.copy(onError = color) }
        fun pageBackgroundColor(@ColorInt color: Int) = apply { colors = colors.copy(pageBackground = color) }
        fun toolbarColor(@ColorInt color: Int) = apply { colors = colors.copy(toolbar = color) }
        fun pageIndicatorColor(@ColorInt color: Int) = apply { colors = colors.copy(pageIndicator = color) }
        fun selectionColor(@ColorInt color: Int) = apply { colors = colors.copy(selection = color) }
        fun highlightColor(@ColorInt color: Int) = apply { colors = colors.copy(highlight = color) }
        fun addCustomColor(key: String, @ColorInt color: Int) = apply { customColors[key] = color }
        fun enableSystemTheme(enable: Boolean) = apply { enableSystemTheme = enable }
        fun enableDynamicColors(enable: Boolean) = apply { enableDynamicColors = enable }

        fun build() = ThemeConfig(themeMode, colors, customColors, enableSystemTheme, enableDynamicColors)
    }

    companion object {
        fun default() = ThemeConfig()
        fun light() = ThemeConfig(ThemeMode.LIGHT, ColorScheme.light())
        fun dark() = ThemeConfig(ThemeMode.DARK, ColorScheme.dark())
        fun sepia() = ThemeConfig(ThemeMode.SEPIA, ColorScheme.sepia())
        fun highContrast() = ThemeConfig(ThemeMode.HIGH_CONTRAST, ColorScheme.highContrast())
    }
}

/**
 * Theme modes
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SEPIA,
    HIGH_CONTRAST,
    CUSTOM,
    SYSTEM
}

/**
 * Color scheme for theming
 */
@Parcelize
data class ColorScheme(
    @ColorInt val primary: Int,
    @ColorInt val primaryVariant: Int,
    @ColorInt val secondary: Int,
    @ColorInt val secondaryVariant: Int,
    @ColorInt val background: Int,
    @ColorInt val surface: Int,
    @ColorInt val error: Int,
    @ColorInt val onPrimary: Int,
    @ColorInt val onSecondary: Int,
    @ColorInt val onBackground: Int,
    @ColorInt val onSurface: Int,
    @ColorInt val onError: Int,
    @ColorInt val pageBackground: Int,
    @ColorInt val toolbar: Int,
    @ColorInt val pageIndicator: Int,
    @ColorInt val selection: Int,
    @ColorInt val highlight: Int,
    @ColorInt val shadowColor: Int,
    @ColorInt val divider: Int,
    @ColorInt val ripple: Int
) : Parcelable {
    companion object {
        fun light() = ColorScheme(
            primary = 0xFF2196F3.toInt(),
            primaryVariant = 0xFF1976D2.toInt(),
            secondary = 0xFF03DAC6.toInt(),
            secondaryVariant = 0xFF018786.toInt(),
            background = 0xFFF5F5F5.toInt(),
            surface = 0xFFFFFFFF.toInt(),
            error = 0xFFB00020.toInt(),
            onPrimary = 0xFFFFFFFF.toInt(),
            onSecondary = 0xFF000000.toInt(),
            onBackground = 0xFF000000.toInt(),
            onSurface = 0xFF000000.toInt(),
            onError = 0xFFFFFFFF.toInt(),
            pageBackground = 0xFFFFFFFF.toInt(),
            toolbar = 0xFFFFFFFF.toInt(),
            pageIndicator = 0x80000000.toInt(),
            selection = 0x402196F3.toInt(),
            highlight = 0x60FFFF00.toInt(),
            shadowColor = 0x40000000,
            divider = 0x1F000000,
            ripple = 0x40000000
        )

        fun dark() = ColorScheme(
            primary = 0xFF3F51B5.toInt(),
            primaryVariant = 0xFF303F9F.toInt(),
            secondary = 0xFF03DAC6.toInt(),
            secondaryVariant = 0xFF03DAC6.toInt(),
            background = 0xFF121212.toInt(),
            surface = 0xFF1E1E1E.toInt(),
            error = 0xFFCF6679.toInt(),
            onPrimary = 0xFFFFFFFF.toInt(),
            onSecondary = 0xFF000000.toInt(),
            onBackground = 0xFFFFFFFF.toInt(),
            onSurface = 0xFFFFFFFF.toInt(),
            onError = 0xFF000000.toInt(),
            pageBackground = 0xFF1E1E1E.toInt(),
            toolbar = 0xFF2C2C2C.toInt(),
            pageIndicator = 0xE6000000.toInt(),
            selection = 0x403F51B5.toInt(),
            highlight = 0x60FFFF00.toInt(),
            shadowColor = 0x80000000.toInt(),
            divider = 0x1FFFFFFF,
            ripple = 0x40FFFFFF
        )

        fun sepia() = ColorScheme(
            primary = 0xFF8D6E63.toInt(),
            primaryVariant = 0xFF6D4C41.toInt(),
            secondary = 0xFFA1887F.toInt(),
            secondaryVariant = 0xFF8D6E63.toInt(),
            background = 0xFFF5E6D3.toInt(),
            surface = 0xFFFAF0E6.toInt(),
            error = 0xFFB00020.toInt(),
            onPrimary = 0xFFFFFFFF.toInt(),
            onSecondary = 0xFF000000.toInt(),
            onBackground = 0xFF3E2723.toInt(),
            onSurface = 0xFF3E2723.toInt(),
            onError = 0xFFFFFFFF.toInt(),
            pageBackground = 0xFFFAF0E6.toInt(),
            toolbar = 0xFFEFDFCF.toInt(),
            pageIndicator = 0x803E2723.toInt(),
            selection = 0x408D6E63.toInt(),
            highlight = 0x60FFE082.toInt(),
            shadowColor = 0x403E2723,
            divider = 0x1F3E2723,
            ripple = 0x403E2723
        )

        fun highContrast() = ColorScheme(
            primary = 0xFF000000.toInt(),
            primaryVariant = 0xFF000000.toInt(),
            secondary = 0xFF000000.toInt(),
            secondaryVariant = 0xFF000000.toInt(),
            background = 0xFFFFFFFF.toInt(),
            surface = 0xFFFFFFFF.toInt(),
            error = 0xFFFF0000.toInt(),
            onPrimary = 0xFFFFFFFF.toInt(),
            onSecondary = 0xFFFFFFFF.toInt(),
            onBackground = 0xFF000000.toInt(),
            onSurface = 0xFF000000.toInt(),
            onError = 0xFFFFFFFF.toInt(),
            pageBackground = 0xFFFFFFFF.toInt(),
            toolbar = 0xFF000000.toInt(),
            pageIndicator = 0xFF000000.toInt(),
            selection = 0xFF000000.toInt(),
            highlight = 0xFFFFFF00.toInt(),
            shadowColor = 0xFF000000.toInt(),
            divider = 0xFF000000.toInt(),
            ripple = 0x80000000.toInt()
        )
    }
}

/**
 * Typography configuration
 */
@Parcelize
data class TypographyConfig(
    val titleSize: Float = 24f,
    val subtitleSize: Float = 18f,
    val bodySize: Float = 14f,
    val captionSize: Float = 12f,
    val buttonSize: Float = 14f,
    val overlineSize: Float = 10f,
    val fontFamily: String? = null,
    val titleWeight: FontWeight = FontWeight.BOLD,
    val subtitleWeight: FontWeight = FontWeight.MEDIUM,
    val bodyWeight: FontWeight = FontWeight.NORMAL
) : Parcelable

/**
 * Font weights
 */
enum class FontWeight {
    THIN,
    LIGHT,
    NORMAL,
    MEDIUM,
    BOLD,
    BLACK
}