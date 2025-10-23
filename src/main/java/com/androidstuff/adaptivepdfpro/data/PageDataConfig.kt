package com.androidstuff.adaptivepdfpro.data

import androidx.annotation.ColorInt

/**
 * Configuration for page data overlays and metadata display
 */
data class PageDataConfig(
    val overlays: List<DataOverlay> = emptyList(),
    val showMetadata: Boolean = false,
    val metadataPosition: MetadataPosition = MetadataPosition.BOTTOM_SHEET,
    val infoPanels: List<InfoPanel> = emptyList(),
    val tooltips: List<Tooltip> = emptyList(),
    val floatingCards: List<FloatingCard> = emptyList(),
    val annotations: List<PageAnnotation> = emptyList(),
    val enableInteractiveElements: Boolean = true
) {
    class Builder {
        private val overlays = mutableListOf<DataOverlay>()
        private var showMetadata = false
        private var metadataPosition = MetadataPosition.BOTTOM_SHEET
        private val infoPanels = mutableListOf<InfoPanel>()
        private val tooltips = mutableListOf<Tooltip>()
        private val floatingCards = mutableListOf<FloatingCard>()
        private val annotations = mutableListOf<PageAnnotation>()
        private var enableInteractiveElements = true

        fun addOverlay(data: List<PageData>) = apply {
            overlays.add(DataOverlay.Table(data))
        }

        fun addOverlay(overlay: DataOverlay) = apply {
            overlays.add(overlay)
        }

        fun showMetadata(show: Boolean) = apply {
            showMetadata = show
        }

        fun metadataPosition(position: MetadataPosition) = apply {
            metadataPosition = position
        }

        fun addInfoPanel(panel: InfoPanel) = apply {
            infoPanels.add(panel)
        }

        fun addTooltip(tooltip: Tooltip) = apply {
            tooltips.add(tooltip)
        }

        fun addFloatingCard(card: FloatingCard) = apply {
            floatingCards.add(card)
        }

        fun addAnnotation(annotation: PageAnnotation) = apply {
            annotations.add(annotation)
        }

        fun enableInteractiveElements(enable: Boolean) = apply {
            enableInteractiveElements = enable
        }

        fun build() = PageDataConfig(
            overlays,
            showMetadata,
            metadataPosition,
            infoPanels,
            tooltips,
            floatingCards,
            annotations,
            enableInteractiveElements
        )
    }
}

/**
 * Base class for data overlays
 */
sealed class DataOverlay {
    data class Table(
        val data: List<PageData>,
        val position: OverlayPosition = OverlayPosition.BOTTOM,
        val style: TableStyle = TableStyle()
    ) : DataOverlay()

    data class Chart(
        val chartData: ChartData,
        val position: OverlayPosition = OverlayPosition.TOP_RIGHT,
        val size: OverlaySize = OverlaySize.MEDIUM
    ) : DataOverlay()

    data class Custom(
        val view: android.view.View,
        val position: OverlayPosition = OverlayPosition.CENTER
    ) : DataOverlay()
}

/**
 * Page data for tabular display
 */
data class PageData(
    val pageNumber: Int,
    val title: String,
    val subtitle: String? = null,
    val data: Map<String, Any>,
    val clickAction: (() -> Unit)? = null,
    val longClickAction: (() -> Unit)? = null,
    val isExpandable: Boolean = false,
    val children: List<PageData> = emptyList()
)

/**
 * Chart data for chart overlays
 */
data class ChartData(
    val type: ChartType,
    val values: List<Float>,
    val labels: List<String>,
    @ColorInt val colors: List<Int> = emptyList()
)

/**
 * Chart types
 */
enum class ChartType {
    PIE,
    BAR,
    LINE,
    SCATTER,
    RADAR
}

/**
 * Info panel configuration
 */
data class InfoPanel(
    val id: String,
    val title: String,
    val content: String,
    val pageNumbers: List<Int> = emptyList(),
    val position: InfoPanelPosition = InfoPanelPosition.RIGHT,
    val isCollapsible: Boolean = true,
    val isExpandedByDefault: Boolean = false,
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val textColor: Int = 0xFF000000.toInt()
)

/**
 * Tooltip configuration
 */
data class Tooltip(
    val id: String,
    val text: String,
    val pageNumber: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val showOnTap: Boolean = true,
    val showOnHover: Boolean = false,
    val autoDismiss: Boolean = true,
    val dismissDelay: Long = 3000,
    @ColorInt val backgroundColor: Int = 0xE6000000.toInt(),
    @ColorInt val textColor: Int = 0xFFFFFFFF.toInt()
)

/**
 * Floating card configuration
 */
data class FloatingCard(
    val id: String,
    val title: String,
    val content: String,
    val pageNumber: Int,
    val position: FloatingCardPosition = FloatingCardPosition.TOP_RIGHT,
    val isDraggable: Boolean = true,
    val isCloseable: Boolean = true,
    val width: Int = 200,
    val height: Int = 150,
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val titleColor: Int = 0xFF000000.toInt(),
    @ColorInt val contentColor: Int = 0xFF666666.toInt(),
    val elevation: Float = 8f,
    val cornerRadius: Float = 12f
)

/**
 * Page annotation
 */
data class PageAnnotation(
    val id: String,
    val pageNumber: Int,
    val type: AnnotationType,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val content: String? = null,
    @ColorInt val color: Int = 0x40FFFF00.toInt(),
    val strokeWidth: Float = 2f,
    val isInteractive: Boolean = true,
    val clickAction: (() -> Unit)? = null
)

/**
 * Annotation types
 */
enum class AnnotationType {
    HIGHLIGHT,
    UNDERLINE,
    STRIKETHROUGH,
    NOTE,
    DRAWING,
    RECTANGLE,
    CIRCLE,
    ARROW,
    TEXT
}

/**
 * Overlay positions
 */
enum class OverlayPosition {
    TOP,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    LEFT,
    RIGHT,
    CENTER,
    CUSTOM
}

/**
 * Overlay sizes
 */
enum class OverlaySize {
    SMALL,
    MEDIUM,
    LARGE,
    FULL_WIDTH,
    FULL_HEIGHT,
    FULL_SCREEN
}

/**
 * Table style configuration
 */
data class TableStyle(
    @ColorInt val headerBackground: Int = 0xFF2196F3.toInt(),
    @ColorInt val headerTextColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val rowBackground: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val alternateRowBackground: Int = 0xFFF5F5F5.toInt(),
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt(),
    val borderWidth: Float = 1f,
    val cellPadding: Int = 8,
    val headerTextSize: Float = 14f,
    val cellTextSize: Float = 12f,
    val enableSorting: Boolean = true,
    val enableFiltering: Boolean = false
)

/**
 * Metadata positions
 */
enum class MetadataPosition {
    BOTTOM_SHEET,
    SIDE_PANEL,
    POPUP,
    OVERLAY,
    TOOLBAR
}

/**
 * Info panel positions
 */
enum class InfoPanelPosition {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}

/**
 * Floating card positions
 */
enum class FloatingCardPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CENTER,
    CUSTOM
}