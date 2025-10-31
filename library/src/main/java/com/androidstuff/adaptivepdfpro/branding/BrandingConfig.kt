package com.androidstuff.adaptivepdfpro.branding

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/**
 * Configuration for PDF branding including logos, watermarks, headers and footers
 */
@Parcelize
data class BrandingConfig(
    val logos: List<LogoConfig> = emptyList(),
    val watermark: WatermarkConfig? = null,
    val header: HeaderConfig? = null,
    val footer: FooterConfig? = null,
    val title: TitleConfig? = null,
    val subtitle: SubtitleConfig? = null
) : Parcelable {
    class Builder {
        private val logos = mutableListOf<LogoConfig>()
        private var watermark: WatermarkConfig? = null
        private var header: HeaderConfig? = null
        private var footer: FooterConfig? = null
        private var title: TitleConfig? = null
        private var subtitle: SubtitleConfig? = null

        fun logo(
            @DrawableRes resId: Int? = null,
            bitmap: Bitmap? = null,
            position: LogoPosition = LogoPosition.TOP_LEFT,
            size: LogoSize = LogoSize.MEDIUM,
            opacity: Float = 1f,
            clickAction: (() -> Unit)? = null
        ) = apply {
            logos.add(LogoConfig(resId, bitmap, position, size, opacity, clickAction))
        }

        fun watermark(
            text: String,
            @ColorInt color: Int = 0x40000000,
            rotation: Float = -45f,
            opacity: Float = 0.1f,
            textSize: Float = 48f
        ) = apply {
            watermark = WatermarkConfig(text, color, rotation, opacity, textSize)
        }

        fun header(
            text: String? = null,
            showPageNumber: Boolean = true,
            showDate: Boolean = false,
            @ColorInt textColor: Int = 0xFF000000.toInt(),
            @ColorInt backgroundColor: Int = 0xFFFFFFFF.toInt(),
            height: Int = 48,
            textSize: Float = 14f
        ) = apply {
            header = HeaderConfig(text, showPageNumber, showDate, textColor, backgroundColor, height, textSize)
        }

        fun footer(
            text: String? = null,
            showPageNumber: Boolean = true,
            showTotalPages: Boolean = true,
            copyright: String? = null,
            @ColorInt textColor: Int = 0xFF000000.toInt(),
            @ColorInt backgroundColor: Int = 0xFFFFFFFF.toInt(),
            height: Int = 48,
            textSize: Float = 12f
        ) = apply {
            footer = FooterConfig(text, showPageNumber, showTotalPages, copyright, textColor, backgroundColor, height, textSize)
        }

        fun title(
            text: String,
            @ColorInt color: Int = 0xFF000000.toInt(),
            textSize: Float = 24f,
            isBold: Boolean = true,
            position: TitlePosition = TitlePosition.TOP_CENTER
        ) = apply {
            title = TitleConfig(text, color, textSize, isBold, position)
        }

        fun subtitle(
            text: String,
            @ColorInt color: Int = 0xFF666666.toInt(),
            textSize: Float = 16f,
            isItalic: Boolean = false
        ) = apply {
            subtitle = SubtitleConfig(text, color, textSize, isItalic)
        }

        fun build() = BrandingConfig(logos, watermark, header, footer, title, subtitle)
    }
}

/**
 * Logo configuration
 */
@Parcelize
data class LogoConfig(
    @DrawableRes val resourceId: Int? = null,
    val bitmap: Bitmap? = null,
    val position: LogoPosition = LogoPosition.TOP_LEFT,
    val size: LogoSize = LogoSize.MEDIUM,
    val opacity: Float = 1f,
    val clickAction: (() -> Unit)? = null,
    val marginHorizontal: Int = 16,
    val marginVertical: Int = 16
) : Parcelable

/**
 * Logo positions
 */
enum class LogoPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
    CENTER,
    CUSTOM
}

/**
 * Logo sizes
 */
enum class LogoSize(val dp: Int) {
    SMALL(32),
    MEDIUM(48),
    LARGE(64),
    EXTRA_LARGE(96)
}

/**
 * Watermark configuration
 */
@Parcelize
data class WatermarkConfig(
    val text: String,
    @ColorInt val color: Int = 0x40000000,
    val rotation: Float = -45f,
    val opacity: Float = 0.1f,
    val textSize: Float = 48f,
    val repeat: Boolean = true,
    val spacing: Float = 100f
) : Parcelable

/**
 * Header configuration
 */
@Parcelize
data class HeaderConfig(
    val text: String? = null,
    val showPageNumber: Boolean = true,
    val showDate: Boolean = false,
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val height: Int = 48,
    val textSize: Float = 14f,
    val alignment: TextAlignment = TextAlignment.CENTER
) : Parcelable

/**
 * Footer configuration
 */
@Parcelize
data class FooterConfig(
    val text: String? = null,
    val showPageNumber: Boolean = true,
    val showTotalPages: Boolean = true,
    val copyright: String? = null,
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val height: Int = 48,
    val textSize: Float = 12f,
    val alignment: TextAlignment = TextAlignment.CENTER
) : Parcelable

/**
 * Title configuration
 */
@Parcelize
data class TitleConfig(
    val text: String,
    @ColorInt val color: Int = 0xFF000000.toInt(),
    val textSize: Float = 24f,
    val isBold: Boolean = true,
    val position: TitlePosition = TitlePosition.TOP_CENTER,
    val marginTop: Int = 8,
    val marginBottom: Int = 4
) : Parcelable

/**
 * Title positions
 */
enum class TitlePosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    OVERLAY_TOP
}

/**
 * Subtitle configuration
 */
@Parcelize
data class SubtitleConfig(
    val text: String,
    @ColorInt val color: Int = 0xFF666666.toInt(),
    val textSize: Float = 16f,
    val isItalic: Boolean = false,
    val marginTop: Int = 4,
    val marginBottom: Int = 8
) : Parcelable

/**
 * Text alignment
 */
enum class TextAlignment {
    LEFT,
    CENTER,
    RIGHT
}