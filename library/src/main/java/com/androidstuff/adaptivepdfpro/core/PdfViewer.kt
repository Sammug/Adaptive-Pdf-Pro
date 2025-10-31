package com.androidstuff.adaptivepdfpro.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androidstuff.adaptivepdfpro.compose.PdfViewerActivity
import java.io.File

/**
 * Main entry point for PDF viewing functionality.
 * 
 * Provides both programmatic API for launching PDF viewer activities and Composable
 * integration for embedding PDF viewers in existing Compose UIs.
 * 
 * Example Usage:
 * ```kotlin
 * // Launch PDF viewer activity
 * PdfViewer.with(context)
 *     .load(pdfFile)
 *     .setBranding {
 *         logo(R.drawable.company_logo, LogoPosition.TOP_RIGHT)
 *         watermark("Confidential", opacity = 0.3f)
 *     }
 *     .setViewer {
 *         enableZoom(true)
 *         setZoomLevels(1f, 2f, 4f)
 *     }
 *     .show()
 * 
 * // Or use as Composable
 * val config = PdfConfig.build {
 *     load(pdfFile)
 *     setViewer { enableZoom(true) }
 * }
 * PdfViewer(config = config)
 * ```
 */
class PdfViewer private constructor(
    private val context: Context,
    private val config: PdfConfig
) {
    
    /**
     * Show PDF in a new activity with the configured settings.
     */
    fun show(title: String = "PDF Viewer") {
        val intent = Intent(context, PdfViewerActivity::class.java).apply {
            putExtra(PdfViewerActivity.EXTRA_CONFIG, config)
            putExtra(PdfViewerActivity.EXTRA_TITLE, title)
            if (context !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }

    companion object {
        /**
         * Create a new PDF viewer builder instance.
         */
        fun with(context: Context) = Builder(context)
        
        /**
         * Quick method to show PDF file in viewer activity with default settings.
         */
        fun showPdf(context: Context, file: File, title: String = file.name) {
            with(context)
                .load(file)
                .show(title)
        }
        
        /**
         * Quick method to show PDF from URI in viewer activity with default settings.
         */
        fun showPdf(context: Context, uri: Uri, title: String = "PDF Document") {
            with(context)
                .load(uri)
                .show(title)
        }
        
        /**
         * Quick method to show PDF from URL in viewer activity with default settings.
         */
        fun showPdf(context: Context, url: String, title: String = "PDF Document") {
            with(context)
                .load(url)
                .show(title)
        }
    }
    
    /**
     * Builder for configuring PDF viewer with fluent API.
     */
    class Builder(private val context: Context) {
        private var source: PdfSource? = null
        private var brandingConfig = com.androidstuff.adaptivepdfpro.branding.BrandingConfig()
        private var navigationConfig = com.androidstuff.adaptivepdfpro.navigation.NavigationConfig()
        private var themeConfig = com.androidstuff.adaptivepdfpro.theme.ThemeConfig.default()
        private var pageDataConfig = com.androidstuff.adaptivepdfpro.data.PageDataConfig()
        private var viewerConfig = ViewerConfig()
        private var downloadConfig = DownloadConfig()
        private var sharingConfig = SharingConfig()

        fun load(file: File): Builder {
            source = PdfSource.File(file)
            return this
        }
        
        fun load(uri: Uri): Builder {
            source = PdfSource.Uri(uri)
            return this
        }
        
        fun load(url: String): Builder {
            source = PdfSource.Url(url)
            return this
        }
        
        fun loadFromAssets(assetPath: String): Builder {
            source = PdfSource.Asset(assetPath)
            return this
        }
        
        fun load(bytes: ByteArray): Builder {
            source = PdfSource.ByteArray(bytes)
            return this
        }
        
        fun setBranding(config: com.androidstuff.adaptivepdfpro.branding.BrandingConfig): Builder {
            brandingConfig = config
            return this
        }
        
        fun setBranding(init: com.androidstuff.adaptivepdfpro.branding.BrandingConfig.Builder.() -> Unit): Builder {
            brandingConfig = com.androidstuff.adaptivepdfpro.branding.BrandingConfig.Builder().apply(init).build()
            return this
        }
        
        fun setNavigation(config: com.androidstuff.adaptivepdfpro.navigation.NavigationConfig): Builder {
            navigationConfig = config
            return this
        }
        
        fun setNavigation(init: com.androidstuff.adaptivepdfpro.navigation.NavigationConfig.Builder.() -> Unit): Builder {
            navigationConfig = com.androidstuff.adaptivepdfpro.navigation.NavigationConfig.Builder().apply(init).build()
            return this
        }
        
        fun setTheme(config: com.androidstuff.adaptivepdfpro.theme.ThemeConfig): Builder {
            themeConfig = config
            return this
        }
        
        fun setTheme(init: com.androidstuff.adaptivepdfpro.theme.ThemeConfig.Builder.() -> Unit): Builder {
            themeConfig = com.androidstuff.adaptivepdfpro.theme.ThemeConfig.Builder().apply(init).build()
            return this
        }
        
        fun setPageData(config: com.androidstuff.adaptivepdfpro.data.PageDataConfig): Builder {
            pageDataConfig = config
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
        
        fun build(): PdfViewer {
            val pdfConfig = PdfConfig(
                source = source ?: throw IllegalArgumentException("PDF source must be specified"),
                brandingConfig = brandingConfig,
                navigationConfig = navigationConfig,
                themeConfig = themeConfig,
                pageDataConfig = pageDataConfig,
                viewerConfig = viewerConfig,
                downloadConfig = downloadConfig,
                sharingConfig = sharingConfig
            )
            return PdfViewer(context, pdfConfig)
        }
        
        fun show(title: String = "PDF Viewer") {
            build().show(title)
        }
    }
}

/**
 * Composable PDF viewer component for embedding in existing Compose UIs.
 * 
 * This is the Composable version of the PDF viewer that can be embedded directly
 * in your existing Compose layouts without launching a separate activity.
 * 
 * Example Usage:
 * ```kotlin
 * @Composable
 * fun MyPdfScreen() {
 *     val config = PdfConfig.build {
 *         load(myPdfFile)
 *         setViewer {
 *             enableZoom(true)
 *             pageFitPolicy(ViewerConfig.FitPolicy.WIDTH)
 *         }
 *         setBranding {
 *             watermark("Draft", opacity = 0.2f)
 *         }
 *     }
 *     
 *     PdfViewer(
 *         config = config,
 *         modifier = Modifier.fillMaxSize(),
 *         onError = { error -> 
 *             Log.e("PDF", "Error: ${error.message}") 
 *         },
 *         onPageChanged = { page, total ->
 *             Log.d("PDF", "Page $page of $total")
 *         }
 *     )
 * }
 * ```
 */
@Composable
fun PdfViewer(
    config: PdfConfig,
    modifier: Modifier = Modifier,
    onError: (Throwable) -> Unit = {},
    onPageChanged: ((Int, Int) -> Unit)? = null
) {
    com.androidstuff.adaptivepdfpro.compose.PdfViewer(
        config = config,
        modifier = modifier,
        onError = onError,
        onPageChanged = onPageChanged
    )
}