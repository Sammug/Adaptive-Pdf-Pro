package com.androidstuff.adaptivepdfpro.data

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

/**
 * Generic PDF content data structure for flexible document generation.
 * This provides a foundation for creating any type of PDF document without being
 * tied to specific business use cases.
 */
@Parcelize
data class PdfContentData(
    val header: PdfHeaderSection? = null,
    val sections: List<PdfContentSection> = emptyList(),
    val footer: PdfFooterSection? = null,
    val styling: PdfDocumentStyle = PdfDocumentStyle()
) : Parcelable

/**
 * Header section containing title and metadata.
 */
@Parcelize
data class PdfHeaderSection(
    val title: String,
    val subtitle: String? = null,
    val logoResourceId: Int? = null,
    val headerItems: List<PdfDataItem> = emptyList()
) : Parcelable

/**
 * Content section containing a collection of items.
 */
@Parcelize
data class PdfContentSection(
    val title: String? = null,
    val items: List<PdfContentItem> = emptyList(),
    val showSeparator: Boolean = false,
    val styling: PdfSectionStyle = PdfSectionStyle()
) : Parcelable

/**
 * Individual content item that can represent various types of data.
 */
@Parcelize
data class PdfContentItem(
    val type: PdfContentType,
    val data: PdfItemData,
    val styling: PdfItemStyle = PdfItemStyle()
) : Parcelable

/**
 * Types of content that can be included in PDF documents.
 */
enum class PdfContentType {
    TEXT,
    TABLE,
    IMAGE,
    SPACER,
    DIVIDER,
    DATA_GRID,
    KEY_VALUE_PAIRS
}

/**
 * Generic data container for content items.
 */
@Parcelize
data class PdfItemData(
    val text: String? = null,
    val properties: Map<String, String> = emptyMap(),
    val numericValues: Map<String, Float> = emptyMap(),
    val booleanValues: Map<String, Boolean> = emptyMap(),
    val listData: List<String> = emptyList(),
    val nestedData: List<PdfDataRow> = emptyList()
) : Parcelable

/**
 * Row of data for tables and grids.
 */
@Parcelize
data class PdfDataRow(
    val cells: List<String>,
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

/**
 * Footer section with customizable content.
 */
@Parcelize
data class PdfFooterSection(
    val leftText: String? = null,
    val centerText: String? = null,
    val rightText: String? = null,
    val showPageNumbers: Boolean = true,
    val additionalItems: List<PdfDataItem> = emptyList()
) : Parcelable

/**
 * Generic data item for key-value information.
 */
@Parcelize
data class PdfDataItem(
    val label: String,
    val value: String,
    val type: PdfDataType = PdfDataType.TEXT,
    val formatting: String? = null
) : Parcelable

/**
 * Data types for formatting and display purposes.
 */
enum class PdfDataType {
    TEXT,
    NUMBER,
    CURRENCY,
    PERCENTAGE,
    DATE,
    BOOLEAN,
    URL,
    EMAIL
}

/**
 * Document-wide styling configuration.
 */
@Parcelize
data class PdfDocumentStyle(
    val pageWidth: Int = 595, // A4 width in points
    val pageHeight: Int = 842, // A4 height in points
    val margins: PdfMargins = PdfMargins(),
    val colors: PdfColorScheme = PdfColorScheme(),
    val typography: PdfTypography = PdfTypography(),
    val spacing: PdfSpacing = PdfSpacing()
) : Parcelable

/**
 * Page margin configuration.
 */
@Parcelize
data class PdfMargins(
    val top: Float = 16f,
    val right: Float = 12f,
    val bottom: Float = 16f,
    val left: Float = 12f
) : Parcelable

/**
 * Color scheme for the document.
 */
@Parcelize
data class PdfColorScheme(
    @ColorInt val primaryColor: Int = 0xFF000000.toInt(),
    @ColorInt val secondaryColor: Int = 0xFF666666.toInt(),
    @ColorInt val accentColor: Int = 0xFF2196F3.toInt(),
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt(),
    @ColorInt val headerColor: Int = 0xFFF5F5F5.toInt()
) : Parcelable

/**
 * Typography settings.
 */
@Parcelize
data class PdfTypography(
    val baseFontSize: Float = 12f,
    val titleFontSize: Float = 20f,
    val subtitleFontSize: Float = 16f,
    val headerFontSize: Float = 14f,
    val bodyFontSize: Float = 12f,
    val captionFontSize: Float = 10f,
    val lineHeight: Float = 1.4f
) : Parcelable

/**
 * Spacing configuration.
 */
@Parcelize
data class PdfSpacing(
    val sectionSpacing: Float = 16f,
    val itemSpacing: Float = 8f,
    val paragraphSpacing: Float = 12f,
    val cellPadding: Float = 6f
) : Parcelable

/**
 * Section-specific styling.
 */
@Parcelize
data class PdfSectionStyle(
    @ColorInt val backgroundColor: Int = 0x00000000, // Transparent by default
    @ColorInt val titleColor: Int = 0xFF000000.toInt(),
    val titleFontSize: Float = 16f,
    val showBorder: Boolean = false,
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt(),
    val borderWidth: Float = 1f
) : Parcelable

/**
 * Item-specific styling.
 */
@Parcelize
data class PdfItemStyle(
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    val fontSize: Float = 12f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val alignment: PdfAlignment = PdfAlignment.LEFT,
    val padding: Float = 4f,
    @ColorInt val backgroundColor: Int = 0x00000000, // Transparent by default
    // Extended styling for exact designs
    @ColorInt val borderColor: Int = 0x00000000,
    val borderWidth: Float = 0f,
    val borderStyle: BorderStyle = BorderStyle.NONE,
    val cornerRadius: Float = 0f,
    val margin: Float = 0f,
    @ColorInt val alternateRowColor: Int = 0x00000000,
    val elevation: Float = 0f
) : Parcelable

/**
 * Text and content alignment options.
 */
enum class PdfAlignment {
    LEFT,
    CENTER,
    RIGHT,
    JUSTIFY
}

/**
 * Border style options for elements.
 */
enum class BorderStyle {
    NONE,
    SOLID,
    DASHED,
    DOTTED,
    DOUBLE
}

/**
 * Builder class for easy PDF content construction.
 */
class PdfContentBuilder {
    private var header: PdfHeaderSection? = null
    private val sections = mutableListOf<PdfContentSection>()
    private var footer: PdfFooterSection? = null
    private var styling = PdfDocumentStyle()

    fun header(
        title: String,
        subtitle: String? = null,
        logoResourceId: Int? = null,
        configure: PdfHeaderBuilder.() -> Unit = {}
    ) = apply {
        val builder = PdfHeaderBuilder(title, subtitle, logoResourceId)
        builder.configure()
        header = builder.build()
    }

    fun section(title: String? = null, configure: PdfSectionBuilder.() -> Unit) = apply {
        val builder = PdfSectionBuilder(title)
        builder.configure()
        sections.add(builder.build())
    }

    fun footer(configure: PdfFooterBuilder.() -> Unit) = apply {
        val builder = PdfFooterBuilder()
        builder.configure()
        footer = builder.build()
    }

    fun styling(configure: PdfDocumentStyle.() -> PdfDocumentStyle) = apply {
        styling = styling.configure()
    }

    fun build() = PdfContentData(header, sections, footer, styling)
}

/**
 * Builder for header sections.
 */
class PdfHeaderBuilder(
    private val title: String,
    private val subtitle: String?,
    private val logoResourceId: Int?
) {
    private val headerItems = mutableListOf<PdfDataItem>()

    fun dataItem(label: String, value: String, type: PdfDataType = PdfDataType.TEXT) = apply {
        headerItems.add(PdfDataItem(label, value, type))
    }

    fun build() = PdfHeaderSection(title, subtitle, logoResourceId, headerItems)
}

/**
 * Builder for content sections.
 */
class PdfSectionBuilder(private val title: String?) {
    private val items = mutableListOf<PdfContentItem>()
    private var showSeparator = false
    private var styling = PdfSectionStyle()

    fun text(content: String, styling: PdfItemStyle = PdfItemStyle()) = apply {
        val data = PdfItemData(text = content)
        items.add(PdfContentItem(PdfContentType.TEXT, data, styling))
    }

    fun table(headers: List<String>, rows: List<List<String>>, styling: PdfItemStyle = PdfItemStyle()) = apply {
        val rowData = rows.map { PdfDataRow(it) }
        val data = PdfItemData(
            listData = headers,
            nestedData = rowData
        )
        items.add(PdfContentItem(PdfContentType.TABLE, data, styling))
    }

    fun keyValuePairs(pairs: List<Pair<String, String>>, styling: PdfItemStyle = PdfItemStyle()) = apply {
        val rowData = pairs.map { PdfDataRow(listOf(it.first, it.second)) }
        val data = PdfItemData(nestedData = rowData)
        items.add(PdfContentItem(PdfContentType.KEY_VALUE_PAIRS, data, styling))
    }

    fun spacer(height: Float = 16f) = apply {
        val data = PdfItemData(numericValues = mapOf("height" to height))
        items.add(PdfContentItem(PdfContentType.SPACER, data))
    }

    fun divider(styling: PdfItemStyle = PdfItemStyle()) = apply {
        val data = PdfItemData()
        items.add(PdfContentItem(PdfContentType.DIVIDER, data, styling))
    }

    fun showSeparator(show: Boolean) = apply {
        showSeparator = show
    }

    fun build() = PdfContentSection(title, items, showSeparator, styling)
}

/**
 * Builder for footer sections.
 */
class PdfFooterBuilder {
    private var leftText: String? = null
    private var centerText: String? = null
    private var rightText: String? = null
    private var showPageNumbers = true
    private val additionalItems = mutableListOf<PdfDataItem>()

    fun leftText(text: String) = apply { leftText = text }
    fun centerText(text: String) = apply { centerText = text }
    fun rightText(text: String) = apply { rightText = text }
    fun showPageNumbers(show: Boolean) = apply { showPageNumbers = show }
    fun additionalItem(label: String, value: String) = apply {
        additionalItems.add(PdfDataItem(label, value))
    }

    fun build() = PdfFooterSection(leftText, centerText, rightText, showPageNumbers, additionalItems)
}

/**
 * Extension function to create PDF content using builder pattern.
 */
fun buildPdfContent(configure: PdfContentBuilder.() -> Unit): PdfContentData {
    val builder = PdfContentBuilder()
    builder.configure()
    return builder.build()
}

// Compatibility aliases for existing code
typealias PdfReportData = PdfContentData
typealias PdfLayoutConfig = PdfDocumentStyle