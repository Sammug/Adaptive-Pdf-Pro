package com.androidstuff.adaptivepdfpro.navigation

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

/**
 * Configuration for PDF navigation features
 */
@Parcelize
data class NavigationConfig(
    val enableSwipe: Boolean = true,
    val swipeDirection: SwipeDirection = SwipeDirection.HORIZONTAL,
    val enablePageJump: Boolean = true,
    val showThumbnails: Boolean = true,
    val showTableOfContents: Boolean = true,
    val showBookmarks: Boolean = true,
    val enableSearch: Boolean = true,
    val showPageSlider: Boolean = true,
    val showNavigationButtons: Boolean = true,
    val enableDoubleTapNavigation: Boolean = false,
    val enableVolumeButtonNavigation: Boolean = false,
    val pageTransition: PageTransition = PageTransition.SLIDE,
    val navigationBarConfig: NavigationBarConfig = NavigationBarConfig(),
    val thumbnailConfig: ThumbnailConfig = ThumbnailConfig(),
    val searchConfig: SearchConfig = SearchConfig()
) : Parcelable {
    class Builder {
        private var enableSwipe = true
        private var swipeDirection = SwipeDirection.HORIZONTAL
        private var enablePageJump = true
        private var showThumbnails = true
        private var showTableOfContents = true
        private var showBookmarks = true
        private var enableSearch = true
        private var showPageSlider = true
        private var showNavigationButtons = true
        private var enableDoubleTapNavigation = false
        private var enableVolumeButtonNavigation = false
        private var pageTransition = PageTransition.SLIDE
        private var navigationBarConfig = NavigationBarConfig()
        private var thumbnailConfig = ThumbnailConfig()
        private var searchConfig = SearchConfig()

        fun enableSwipe(enable: Boolean) = apply { enableSwipe = enable }
        fun swipeDirection(direction: SwipeDirection) = apply { swipeDirection = direction }
        fun enablePageJump(enable: Boolean) = apply { enablePageJump = enable }
        fun showThumbnails(show: Boolean) = apply { showThumbnails = show }
        fun showTableOfContents(show: Boolean) = apply { showTableOfContents = show }
        fun showBookmarks(show: Boolean) = apply { showBookmarks = show }
        fun enableSearch(enable: Boolean) = apply { enableSearch = enable }
        fun showPageSlider(show: Boolean) = apply { showPageSlider = show }
        fun showNavigationButtons(show: Boolean) = apply { showNavigationButtons = show }
        fun enableDoubleTapNavigation(enable: Boolean) = apply { enableDoubleTapNavigation = enable }
        fun enableVolumeButtonNavigation(enable: Boolean) = apply { enableVolumeButtonNavigation = enable }
        fun pageTransition(transition: PageTransition) = apply { pageTransition = transition }
        fun navigationBarConfig(config: NavigationBarConfig) = apply { navigationBarConfig = config }
        fun thumbnailConfig(config: ThumbnailConfig) = apply { thumbnailConfig = config }
        fun searchConfig(config: SearchConfig) = apply { searchConfig = config }

        fun build() = NavigationConfig(
            enableSwipe, swipeDirection, enablePageJump, showThumbnails, showTableOfContents,
            showBookmarks, enableSearch, showPageSlider, showNavigationButtons,
            enableDoubleTapNavigation, enableVolumeButtonNavigation, pageTransition,
            navigationBarConfig, thumbnailConfig, searchConfig
        )
    }
}

/**
 * Swipe directions
 */
enum class SwipeDirection {
    HORIZONTAL,
    VERTICAL,
    BOTH,
    NONE
}

/**
 * Page transition animations
 */
enum class PageTransition {
    NONE,
    SLIDE,
    FADE,
    ZOOM,
    FLIP,
    CURL
}

/**
 * Navigation bar configuration
 */
@Parcelize
data class NavigationBarConfig(
    val position: NavigationBarPosition = NavigationBarPosition.BOTTOM,
    val autoHide: Boolean = true,
    val autoHideDelay: Long = 3000,
    @ColorInt val backgroundColor: Int = 0xE6000000.toInt(),
    @ColorInt val iconColor: Int = 0xFFFFFFFF.toInt(),
    val showPageNumber: Boolean = true,
    val showZoomControls: Boolean = true,
    val showShareButton: Boolean = true,
    val showDownloadButton: Boolean = true,
    val showRotateButton: Boolean = true,
    val showFullscreenButton: Boolean = true,
    val customButtons: List<CustomNavigationButton> = emptyList()
) : Parcelable

/**
 * Navigation bar positions
 */
enum class NavigationBarPosition {
    TOP,
    BOTTOM,
    BOTH
}

/**
 * Custom navigation button
 */
@Parcelize
data class CustomNavigationButton(
    val id: String,
    val iconResId: Int,
    val contentDescription: String,
    val onClick: (() -> Unit)? = null
) : Parcelable

/**
 * Thumbnail configuration
 */
@Parcelize
data class ThumbnailConfig(
    val gridColumns: Int = 3,
    val thumbnailSize: ThumbnailSize = ThumbnailSize.MEDIUM,
    val showPageNumbers: Boolean = true,
    @ColorInt val selectedBorderColor: Int = 0xFF2196F3.toInt(),
    val selectedBorderWidth: Int = 3,
    val cacheSize: Int = 20,
    val preloadCount: Int = 5,
    val animateSelection: Boolean = true
) : Parcelable

/**
 * Thumbnail sizes
 */
enum class ThumbnailSize(val dp: Int) {
    SMALL(80),
    MEDIUM(120),
    LARGE(160),
    EXTRA_LARGE(200)
}

/**
 * Search configuration
 */
@Parcelize
data class SearchConfig(
    val caseSensitive: Boolean = false,
    val wholeWord: Boolean = false,
    val highlightAll: Boolean = true,
    @ColorInt val highlightColor: Int = 0x60FFFF00.toInt(),
    @ColorInt val currentHighlightColor: Int = 0x60FF9800.toInt(),
    val searchBarPosition: SearchBarPosition = SearchBarPosition.TOP,
    val autoSearch: Boolean = true,
    val minSearchLength: Int = 2,
    val showSearchCount: Boolean = true,
    val enableVoiceSearch: Boolean = false
) : Parcelable

/**
 * Search bar positions
 */
enum class SearchBarPosition {
    TOP,
    BOTTOM,
    FLOATING
}

/**
 * Page indicator configuration
 */
@Parcelize
data class PageIndicatorConfig(
    val show: Boolean = true,
    val position: PageIndicatorPosition = PageIndicatorPosition.TOP_RIGHT,
    val format: PageIndicatorFormat = PageIndicatorFormat.CURRENT_OF_TOTAL,
    @ColorInt val textColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val backgroundColor: Int = 0x80000000.toInt(),
    val textSize: Float = 14f,
    val padding: Int = 8,
    val margin: Int = 16,
    val cornerRadius: Float = 8f
) : Parcelable

/**
 * Page indicator positions
 */
enum class PageIndicatorPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}

/**
 * Page indicator formats
 */
enum class PageIndicatorFormat {
    CURRENT_ONLY,           // "5"
    CURRENT_OF_TOTAL,      // "5 / 20"
    CURRENT_DASH_TOTAL,    // "5 - 20"
    PAGE_CURRENT,          // "Page 5"
    PAGE_CURRENT_OF_TOTAL, // "Page 5 of 20"
    PERCENTAGE,            // "25%"
    CUSTOM
}