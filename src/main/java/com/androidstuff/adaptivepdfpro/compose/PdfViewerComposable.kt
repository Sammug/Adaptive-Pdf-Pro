package com.androidstuff.adaptivepdfpro.compose

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidstuff.adaptivepdfpro.compose.components.*
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.compose.viewmodel.PdfViewerViewModel
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfSource
import com.androidstuff.adaptivepdfpro.data.PdfContentData
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.*
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import kotlinx.coroutines.launch
import java.io.File

/**
 * Main composable for displaying PDF documents with full customization
 */
@Composable
fun PdfViewer(
    config: PdfConfig,
    modifier: Modifier = Modifier,
    onDocumentLoaded: ((pages: Int) -> Unit)? = null,
    onPageChanged: ((page: Int, pageCount: Int) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onTap: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val viewModel: PdfViewerViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    
    var pdfView by remember { mutableStateOf<PDFView?>(null) }
    var showNavigationBar by remember { mutableStateOf(true) }
    var showThumbnails by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    
    val currentPage by viewModel.currentPage.collectAsState(initial = 0)
    val pageCount by viewModel.pageCount.collectAsState(initial = 0)
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    val isNightMode by viewModel.isNightMode.collectAsState(initial = config.viewerConfig.nightMode)
    
    AdaptivePdfTheme(
        themeConfig = config.themeConfig,
        darkTheme = isNightMode
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main PDF View
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PDFView(ctx, null).apply {
                        pdfView = this
                        configurePdfView(
                            this,
                            config,
                            context,
                            onLoadComplete = { pages ->
                                viewModel.setPageCount(pages)
                                viewModel.setLoading(false)
                                onDocumentLoaded?.invoke(pages)
                            },
                            onPageChange = { page, count ->
                                viewModel.setCurrentPage(page)
                                viewModel.setPageCount(count)
                                onPageChanged?.invoke(page, count)
                            },
                            onError = { error ->
                                viewModel.setLoading(false)
                                onError?.invoke(error)
                            },
                            onTap = {
                                onTap?.invoke()
                                showNavigationBar = !showNavigationBar
                                false
                            }
                        )
                    }
                }
            )
            
            // Branding Overlays
            if (config.brandingConfig.logos.isNotEmpty() || 
                config.brandingConfig.watermark != null) {
                BrandingOverlay(
                    config = config.brandingConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Header
            config.brandingConfig.header?.let { header ->
                PdfHeader(
                    config = header,
                    currentPage = currentPage,
                    pageCount = pageCount,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            
            // Footer
            config.brandingConfig.footer?.let { footer ->
                PdfFooter(
                    config = footer,
                    currentPage = currentPage,
                    pageCount = pageCount,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            
            // Navigation Bar
            if (config.navigationConfig.showNavigationButtons && showNavigationBar) {
                PdfNavigationBar(
                    config = config.navigationConfig.navigationBarConfig,
                    currentPage = currentPage,
                    pageCount = pageCount,
                    onPreviousPage = {
                        pdfView?.jumpTo(currentPage - 1, true)
                    },
                    onNextPage = {
                        pdfView?.jumpTo(currentPage + 1, true)
                    },
                    onPageJump = { page ->
                        pdfView?.jumpTo(page, true)
                    },
                    onToggleNightMode = {
                        viewModel.toggleNightMode()
                        pdfView?.setNightMode(!isNightMode)
                    },
                    onShowThumbnails = { showThumbnails = true },
                    onShowSearch = { showSearch = true },
                    onShowBookmarks = { showBookmarks = true },
                    onShare = {
                        // Handle share
                    },
                    onDownload = {
                        // Handle download
                    },
                    modifier = Modifier.align(
                        when (config.navigationConfig.navigationBarConfig.position) {
                            com.androidstuff.adaptivepdfpro.navigation.NavigationBarPosition.TOP -> Alignment.TopCenter
                            com.androidstuff.adaptivepdfpro.navigation.NavigationBarPosition.BOTTOM -> Alignment.BottomCenter
                            com.androidstuff.adaptivepdfpro.navigation.NavigationBarPosition.BOTH -> Alignment.BottomCenter
                        }
                    )
                )
            }
            
            // Page Slider
            if (config.navigationConfig.showPageSlider) {
                PdfPageSlider(
                    currentPage = currentPage,
                    pageCount = pageCount,
                    onPageChange = { page ->
                        pdfView?.jumpTo(page, true)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showNavigationBar) 80.dp else 16.dp)
                )
            }
            
            // Page Indicator
            if (config.viewerConfig.showPageNumbers) {
                PageIndicator(
                    currentPage = currentPage,
                    pageCount = pageCount,
                    config = config.navigationConfig.pageIndicatorConfig 
                        ?: com.androidstuff.adaptivepdfpro.navigation.PageIndicatorConfig(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
            
            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Thumbnails Sheet
            if (showThumbnails) {
                ThumbnailsBottomSheet(
                    config = config.navigationConfig.thumbnailConfig,
                    pageCount = pageCount,
                    currentPage = currentPage,
                    onPageSelected = { page ->
                        pdfView?.jumpTo(page, true)
                        showThumbnails = false
                    },
                    onDismiss = { showThumbnails = false }
                )
            }
            
            // Search Sheet
            if (showSearch) {
                SearchBottomSheet(
                    config = config.navigationConfig.searchConfig,
                    onSearch = { query ->
                        viewModel.searchInPdf(query)
                    },
                    onResultSelected = { result ->
                        pdfView?.jumpTo(result.pageNumber, true)
                        showSearch = false
                    },
                    onDismiss = { showSearch = false }
                )
            }
            
            // Bookmarks Sheet
            if (showBookmarks) {
                BookmarksBottomSheet(
                    bookmarks = viewModel.bookmarks.collectAsState(initial = emptyList()).value,
                    onBookmarkSelected = { bookmark ->
                        pdfView?.jumpTo(bookmark.pageNumber, true)
                        showBookmarks = false
                    },
                    onAddBookmark = {
                        viewModel.addBookmark(currentPage)
                    },
                    onRemoveBookmark = { bookmarkId ->
                        viewModel.removeBookmark(bookmarkId)
                    },
                    onDismiss = { showBookmarks = false }
                )
            }
            
            // Data Overlays
            if (config.pageDataConfig.overlays.isNotEmpty()) {
                DataOverlayContainer(
                    config = config.pageDataConfig,
                    currentPage = currentPage,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Configure PDFView with the provided configuration
 */
private fun configurePdfView(
    pdfView: PDFView,
    config: PdfConfig,
    context: Context,
    onLoadComplete: (Int) -> Unit,
    onPageChange: (Int, Int) -> Unit,
    onError: (Throwable) -> Unit,
    onTap: () -> Boolean
) {
    val configurator = when (val source = config.source) {
        is PdfSource.Uri -> pdfView.fromUri(source.uri)
        is PdfSource.File -> pdfView.fromFile(source.file)
        is PdfSource.Asset -> pdfView.fromAsset(source.assetPath)
        is PdfSource.ByteArray -> pdfView.fromBytes(source.bytes)
        is PdfSource.Url -> {
            // Handle URL download separately
            throw IllegalArgumentException("URL sources should be downloaded before creating PDF view")
        }
    }
    
    configurator
        .defaultPage(config.viewerConfig.defaultPage)
        .onPageChange { page, pageCount ->
            onPageChange(page, pageCount)
        }
        .onLoad { pages ->
            onLoadComplete(pages)
        }
        .onError { t ->
            t?.let { onError(it) }
        }
        .onTap { _ ->
            onTap()
        }
        .enableSwipe(config.viewerConfig.enableSwipe)
        .swipeHorizontal(config.viewerConfig.swipeHorizontal)
        .enableDoubleTap(config.viewerConfig.enableDoubleTap)
        .enableAnnotationRendering(config.viewerConfig.enableAnnotationRendering)
        .password(config.viewerConfig.password)
        .scrollHandle(if (config.viewerConfig.scrollHandle) DefaultScrollHandle(context) else null)
        .enableAntialiasing(config.viewerConfig.enableAntialiasing)
        .spacing(config.viewerConfig.spacing)
        .autoSpacing(config.viewerConfig.autoSpacing)
        .pageFitPolicy(
            when (config.viewerConfig.pageFitPolicy) {
                com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.WIDTH -> FitPolicy.WIDTH
                com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.HEIGHT -> FitPolicy.HEIGHT
                com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.BOTH -> FitPolicy.BOTH
            }
        )
        .fitEachPage(config.viewerConfig.fitEachPage)
        .pageSnap(config.viewerConfig.pageSnap)
        .pageFling(config.viewerConfig.pageFling)
        .nightMode(config.viewerConfig.nightMode)
        .load()
}

/**
 * Preview-friendly PDF viewer composable
 */
@Composable
fun PdfViewerPreview(
    pdfAssetPath: String,
    modifier: Modifier = Modifier
) {
    val config = PdfConfig.build {
        loadFromAssets(pdfAssetPath)
        setTheme {
            lightTheme()
        }
        setNavigation {
            enableSwipe(true)
            showPageSlider(true)
            showNavigationButtons(true)
        }
    }
    
    PdfViewer(
        config = config,
        modifier = modifier
    )
}

/**
 * Simplified PDF viewer with DSL-style configuration
 */
@Composable
fun SimplePdfViewer(
    source: PdfSource,
    modifier: Modifier = Modifier,
    configure: (PdfConfig.Builder.() -> Unit)? = null
) {
    val config = PdfConfig.Builder().apply {
        when (source) {
            is PdfSource.Uri -> load(source.uri)
            is PdfSource.File -> load(source.file)
            is PdfSource.Asset -> loadFromAssets(source.assetPath)
            is PdfSource.Url -> load(source.url)
            is PdfSource.ByteArray -> throw IllegalArgumentException("ByteArray not supported in SimplePdfViewer")
        }
        configure?.invoke(this)
    }.build()
    
    PdfViewer(
        config = config,
        modifier = modifier
    )
}

/**
 * Create PDF content viewer from data (for invoices, reports, etc.)
 */
@Composable
fun PdfContentViewer(
    data: PdfContentData,
    modifier: Modifier = Modifier,
    brandingConfig: com.androidstuff.adaptivepdfpro.branding.BrandingConfig = com.androidstuff.adaptivepdfpro.branding.BrandingConfig(),
    themeConfig: com.androidstuff.adaptivepdfpro.theme.ThemeConfig = com.androidstuff.adaptivepdfpro.theme.ThemeConfig.default(),
    onPageChanged: ((Int, Int) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    PdfFromData(
        data = data,
        modifier = modifier,
        brandingConfig = brandingConfig,
        themeConfig = themeConfig,
        onPageChanged = onPageChanged,
        onError = onError
    )
}

/**
 * Easy invoice viewer
 */
@Composable
fun InvoiceViewer(
    businessName: String,
    invoiceNumber: String,
    items: List<ContentItem.LineItem>,
    subtotal: java.math.BigDecimal,
    tax: java.math.BigDecimal? = null,
    total: java.math.BigDecimal,
    modifier: Modifier = Modifier,
    customerInfo: com.androidstuff.adaptivepdfpro.data.Address? = null,
    paymentInfo: com.androidstuff.adaptivepdfpro.data.PaymentInfo? = null
) {
    val invoiceData = com.androidstuff.adaptivepdfpro.data.invoice {
        header(businessName, invoiceNumber) {
            // Configure header if needed
        }
        items.forEach { item ->
            addItem(
                name = item.name,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                description = item.description,
                tax = item.tax
            )
        }
        summary(
            taxRate = tax?.let { it.multiply(java.math.BigDecimal(100)).divide(subtotal, 2, java.math.RoundingMode.HALF_UP) }
        )
        if (paymentInfo != null) {
            footer(
                bankDetails = paymentInfo.bankDetails
            )
        }
    }
    
    PdfContentViewer(
        data = invoiceData,
        modifier = modifier
    )
}

/**
 * Easy transaction report viewer
 */
@Composable
fun TransactionReportViewer(
    title: String,
    transactions: List<ContentItem.Transaction>,
    modifier: Modifier = Modifier,
    groupByCategory: Boolean = false,
    showSummary: Boolean = true
) {
    val reportData = com.androidstuff.adaptivepdfpro.data.transactionReport {
        header(title)
        addTransactions(transactions)
        if (groupByCategory) {
            groupByCategory()
        }
        if (showSummary) {
            summary(
                showTotal = true,
                showCount = true,
                showAverage = true
            )
        }
    }
    
    PdfContentViewer(
        data = reportData,
        modifier = modifier
    )
}

/**
 * Easy receipt viewer
 */
@Composable
fun ReceiptViewer(
    storeName: String,
    receiptNumber: String,
    items: List<Pair<String, java.math.BigDecimal>>, // description to amount
    total: java.math.BigDecimal,
    modifier: Modifier = Modifier,
    paymentMethod: com.androidstuff.adaptivepdfpro.data.PaymentType = com.androidstuff.adaptivepdfpro.data.PaymentType.CASH,
    taxRate: java.math.BigDecimal? = null
) {
    val receiptData = com.androidstuff.adaptivepdfpro.data.receipt {
        header(storeName, receiptNumber)
        items.forEach { (description, amount) ->
            addItem(description, 1, amount)
        }
        addPayment(paymentMethod, total)
        summary(taxRate)
        footer("Thank you for your purchase!")
    }
    
    PdfContentViewer(
        data = receiptData,
        modifier = modifier
    )
}