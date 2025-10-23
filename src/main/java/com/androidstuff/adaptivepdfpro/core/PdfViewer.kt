package com.androidstuff.adaptivepdfpro.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androidstuff.adaptivepdfpro.compose.PdfViewerActivity
import com.androidstuff.adaptivepdfpro.compose.PdfViewer as ComposePdfViewer
import java.io.File

/**
 * Main entry point for PDF viewing functionality
 */
class PdfViewer private constructor(
    private val context: Context,
    private val config: PdfConfig
) {
    
    /**
     * Show PDF in a new activity
     */
    fun show() {
        val intent = Intent(context, PdfViewerActivity::class.java).apply {
            putExtra(PdfViewerActivity.EXTRA_CONFIG, config)
            if (context !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }
    
    /**
     * Get the PDF configuration for use in Compose
     */
    fun getConfig(): PdfConfig {
        return config
    }
    
    /**
     * Show PDF with callback
     */
    fun show(callback: PdfViewerCallback) {
        PdfViewerCallbackManager.setCallback(callback)
        show()
    }
    
    companion object {
        /**
         * Create a new PdfViewer instance with context
         */
        @JvmStatic
        fun with(context: Context): Builder {
            return Builder(context)
        }
    }
    
    class Builder(private val context: Context) {
        private var config: PdfConfig.Builder = PdfConfig.Builder()
        
        fun load(uri: Uri): Builder {
            config.load(uri)
            return this
        }
        
        fun load(url: String): Builder {
            config.load(url)
            return this
        }
        
        fun load(file: File): Builder {
            config.load(file)
            return this
        }
        
        fun loadFromAssets(assetPath: String): Builder {
            config.loadFromAssets(assetPath)
            return this
        }
        
        fun setBranding(init: com.androidstuff.adaptivepdfpro.branding.BrandingConfig.Builder.() -> Unit): Builder {
            config.setBranding(init)
            return this
        }
        
        fun setTheme(init: com.androidstuff.adaptivepdfpro.theme.ThemeConfig.Builder.() -> Unit): Builder {
            config.setTheme(init)
            return this
        }
        
        fun setNavigation(init: com.androidstuff.adaptivepdfpro.navigation.NavigationConfig.Builder.() -> Unit): Builder {
            config.setNavigation(init)
            return this
        }
        
        fun setPageData(init: com.androidstuff.adaptivepdfpro.data.PageDataConfig.Builder.() -> Unit): Builder {
            config.setPageData(init)
            return this
        }
        
        fun setViewer(init: ViewerConfig.Builder.() -> Unit): Builder {
            config.setViewer(init)
            return this
        }
        
        fun setConfig(pdfConfig: PdfConfig): Builder {
            config = PdfConfig.Builder().apply {
                when (val source = pdfConfig.source) {
                    is PdfSource.Uri -> load(source.uri)
                    is PdfSource.Url -> load(source.url)
                    is PdfSource.File -> load(source.file)
                    is PdfSource.Asset -> loadFromAssets(source.assetPath)
                    is PdfSource.ByteArray -> throw IllegalArgumentException("ByteArray source not supported in builder")
                }
                setBranding(pdfConfig.brandingConfig)
                setNavigation(pdfConfig.navigationConfig)
                setTheme(pdfConfig.themeConfig)
                setPageData(pdfConfig.pageDataConfig)
                setViewer(pdfConfig.viewerConfig)
                setDownload(pdfConfig.downloadConfig)
                setSharing(pdfConfig.sharingConfig)
            }
            return this
        }
        
        fun build(): PdfViewer {
            return PdfViewer(context, config.build())
        }
        
        fun show() {
            build().show()
        }
        
        fun show(callback: PdfViewerCallback) {
            build().show(callback)
        }
        
        fun getConfig(): PdfConfig {
            return config.build()
        }
    }
}

/**
 * Callback interface for PDF viewer events
 */
interface PdfViewerCallback {
    fun onDocumentLoaded(pages: Int, documentPath: String?)
    fun onPageChanged(page: Int, pageCount: Int)
    fun onPageError(page: Int, error: Throwable)
    fun onRender(page: Int)
    fun onPageScrolled(page: Int, positionOffset: Float)
    fun onTap(event: android.view.MotionEvent): Boolean
    fun onLongPress(event: android.view.MotionEvent)
    fun onError(error: Throwable)
    fun onDownloadStart(url: String)
    fun onDownloadProgress(progress: Int)
    fun onDownloadComplete(filePath: String)
    fun onDownloadError(error: Throwable)
    fun onShare(uri: Uri)
}

/**
 * Callback manager to handle callbacks across activities
 */
internal object PdfViewerCallbackManager {
    private var callback: PdfViewerCallback? = null
    
    fun setCallback(callback: PdfViewerCallback) {
        this.callback = callback
    }
    
    fun getCallback(): PdfViewerCallback? {
        return callback
    }
    
    fun clearCallback() {
        callback = null
    }
}