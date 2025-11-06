package com.androidstuff.adaptivepdfpro.core

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.androidstuff.adaptivepdfpro.branding.BrandingConfig
import com.androidstuff.adaptivepdfpro.data.PageDataConfig
import com.androidstuff.adaptivepdfpro.navigation.NavigationConfig
import com.androidstuff.adaptivepdfpro.theme.ThemeConfig
import kotlinx.parcelize.Parcelize
import java.io.File

/**
 * Main configuration class for PDF viewer
 */
@Parcelize
data class PdfConfig(
    val source: PdfSource,
    val brandingConfig: BrandingConfig = BrandingConfig(),
    val navigationConfig: NavigationConfig = NavigationConfig(),
    val themeConfig: ThemeConfig = ThemeConfig.default(),
    val pageDataConfig: PageDataConfig = PageDataConfig(),
    val viewerConfig: ViewerConfig = ViewerConfig(),
    val downloadConfig: DownloadConfig = DownloadConfig(),
    val sharingConfig: SharingConfig = SharingConfig()
) : Parcelable {
    class Builder {
        private var source: PdfSource? = null
        private var brandingConfig = BrandingConfig()
        private var navigationConfig = NavigationConfig()
        private var themeConfig = ThemeConfig.default()
        private var pageDataConfig = PageDataConfig()
        private var viewerConfig = ViewerConfig()
        private var downloadConfig = DownloadConfig()
        private var sharingConfig = SharingConfig()

        fun load(uri: Uri): Builder {
            source = PdfSource.Uri(uri)
            return this
        }

        fun load(url: String): Builder {
            source = PdfSource.Url(url)
            return this
        }

        fun load(file: File): Builder {
            source = PdfSource.File(file)
            return this
        }

        fun loadFromAssets(assetPath: String): Builder {
            source = PdfSource.Asset(assetPath)
            return this
        }

        fun setBranding(config: BrandingConfig): Builder {
            brandingConfig = config
            return this
        }

        fun setBranding(init: BrandingConfig.Builder.() -> Unit): Builder {
            brandingConfig = BrandingConfig.Builder().apply(init).build()
            return this
        }

        fun setNavigation(config: NavigationConfig): Builder {
            navigationConfig = config
            return this
        }

        fun setNavigation(init: NavigationConfig.Builder.() -> Unit): Builder {
            navigationConfig = NavigationConfig.Builder().apply(init).build()
            return this
        }

        fun setTheme(config: ThemeConfig): Builder {
            themeConfig = config
            return this
        }

        fun setTheme(init: ThemeConfig.Builder.() -> Unit): Builder {
            themeConfig = ThemeConfig.Builder().apply(init).build()
            return this
        }

        fun setPageData(config: PageDataConfig): Builder {
            pageDataConfig = config
            return this
        }

        fun setPageData(init: PageDataConfig.Builder.() -> Unit): Builder {
            pageDataConfig = PageDataConfig.Builder().apply(init).build()
            return this
        }

        fun setViewer(config: ViewerConfig): Builder {
            viewerConfig = config
            return this
        }

        fun setViewer(init: ViewerConfig.Builder.() -> Unit): Builder {
            viewerConfig = ViewerConfig.Builder().apply(init).build()
            return this
        }

        fun setDownload(config: DownloadConfig): Builder {
            downloadConfig = config
            return this
        }

        fun setSharing(config: SharingConfig): Builder {
            sharingConfig = config
            return this
        }

        fun build(): PdfConfig {
            return PdfConfig(
                source = source ?: throw IllegalArgumentException("PDF source must be specified"),
                brandingConfig = brandingConfig,
                navigationConfig = navigationConfig,
                themeConfig = themeConfig,
                pageDataConfig = pageDataConfig,
                viewerConfig = viewerConfig,
                downloadConfig = downloadConfig,
                sharingConfig = sharingConfig
            )
        }
    }

    companion object {
        inline fun build(init: Builder.() -> Unit): PdfConfig {
            return Builder().apply(init).build()
        }
    }
}

/**
 * PDF source types
 */
@Parcelize
sealed class PdfSource : Parcelable {
    @Parcelize
    data class Uri(val uri: android.net.Uri) : PdfSource()
    @Parcelize
    data class Url(val url: String) : PdfSource()
    @Parcelize
    data class File(val file: java.io.File) : PdfSource()
    @Parcelize
    data class Asset(val assetPath: String) : PdfSource()
    @Parcelize
    data class ByteArray(val bytes: kotlin.ByteArray) : PdfSource() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteArray

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }
}

/**
 * General viewer configuration
 */
@Parcelize
data class ViewerConfig(
    val enableZoom: Boolean = true,
    val minZoom: Float = 1f,
    val midZoom: Float = 1.75f,
    val maxZoom: Float = 3f,
    val enableDoubleTap: Boolean = true,
    val enableSwipe: Boolean = true,
    val enableAnnotationRendering: Boolean = true,
    val enableAntialiasing: Boolean = true,
    val enablePassword: Boolean = false,
    val password: String? = null,
    val spacing: Int = 0,
    val autoSpacing: Boolean = false,
    val pageFitPolicy: FitPolicy = FitPolicy.WIDTH,
    val fitEachPage: Boolean = true,
    val pageFling: Boolean = true,
    val pageSnap: Boolean = true,
    val nightMode: Boolean = false,
    val showPageNumbers: Boolean = true,
    val defaultPage: Int = 0,
    val swipeHorizontal: Boolean = false,
    val scrollHandle: Boolean = true,
    val applyLibraryTheme: Boolean = false
) : Parcelable {
    enum class FitPolicy {
        WIDTH, HEIGHT, BOTH
    }

    class Builder {
        private var enableZoom = true
        private var minZoom = 1f
        private var midZoom = 1.75f
        private var maxZoom = 3f
        private var enableDoubleTap = true
        private var enableSwipe = true
        private var enableAnnotationRendering = true
        private var enableAntialiasing = true
        private var enablePassword = false
        private var password: String? = null
        private var spacing = 0
        private var autoSpacing = false
        private var pageFitPolicy = FitPolicy.WIDTH
        private var fitEachPage = true
        private var pageFling = true
        private var pageSnap = true
        private var nightMode = false
        private var showPageNumbers = true
        private var defaultPage = 0
        private var swipeHorizontal = false
        private var scrollHandle = true
        private var applyLibraryTheme = false

        fun enableZoom(enable: Boolean) = apply { enableZoom = enable }
        fun setZoomLevels(min: Float, mid: Float, max: Float) = apply {
            minZoom = min
            midZoom = mid
            maxZoom = max
        }
        fun enableDoubleTap(enable: Boolean) = apply { enableDoubleTap = enable }
        fun enableSwipe(enable: Boolean) = apply { enableSwipe = enable }
        fun enableAnnotationRendering(enable: Boolean) = apply { enableAnnotationRendering = enable }
        fun enableAntialiasing(enable: Boolean) = apply { enableAntialiasing = enable }
        fun setPassword(pwd: String?) = apply {
            password = pwd
            enablePassword = pwd != null
        }
        fun spacing(space: Int) = apply { spacing = space }
        fun autoSpacing(enable: Boolean) = apply { autoSpacing = enable }
        fun pageFitPolicy(policy: FitPolicy) = apply { pageFitPolicy = policy }
        fun fitEachPage(enable: Boolean) = apply { fitEachPage = enable }
        fun pageFling(enable: Boolean) = apply { pageFling = enable }
        fun pageSnap(enable: Boolean) = apply { pageSnap = enable }
        fun nightMode(enable: Boolean) = apply { nightMode = enable }
        fun showPageNumbers(show: Boolean) = apply { showPageNumbers = show }
        fun defaultPage(page: Int) = apply { defaultPage = page }
        fun swipeHorizontal(horizontal: Boolean) = apply { swipeHorizontal = horizontal }
        fun scrollHandle(show: Boolean) = apply { scrollHandle = show }
        fun applyLibraryTheme(apply: Boolean) = apply { applyLibraryTheme = apply }

        fun build() = ViewerConfig(
            enableZoom, minZoom, midZoom, maxZoom, enableDoubleTap, enableSwipe,
            enableAnnotationRendering, enableAntialiasing, enablePassword, password,
            spacing, autoSpacing, pageFitPolicy, fitEachPage, pageFling, pageSnap,
            nightMode, showPageNumbers, defaultPage, swipeHorizontal, scrollHandle, applyLibraryTheme
        )
    }
}

/**
 * Download configuration
 */
@Parcelize
data class DownloadConfig(
    val enabled: Boolean = true,
    val downloadDirectory: String? = null,
    val showProgress: Boolean = true,
    val allowMobileData: Boolean = true,
    val allowRoaming: Boolean = false,
    val notificationTitle: String = "Downloading PDF",
    val notificationDescription: String? = null
) : Parcelable

/**
 * Sharing configuration
 */
@Parcelize
data class SharingConfig(
    val enabled: Boolean = true,
    val shareTitle: String = "Share PDF",
    val includeWatermark: Boolean = false,
    val customShareText: String? = null
) : Parcelable