package com.androidstuff.adaptivepdfpro.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import kotlin.math.max
import kotlin.math.min

/**
 * Modern PDF Viewer Composable with zoom, navigation, and comprehensive features.
 * 
 * Supports viewing PDFs from various sources (URI, URL, File, Assets) with extensive
 * customization options including zoom, page navigation, theming, and branding.
 * 
 * Example Usage:
 * ```kotlin
 * PdfViewer(
 *     config = PdfConfig.build {
 *         load(pdfFile)
 *         setViewer {
 *             enableZoom(true)
 *             setZoomLevels(1f, 2f, 4f)
 *             pageFitPolicy(ViewerConfig.FitPolicy.WIDTH)
 *         }
 *         setBranding {
 *             watermark("Confidential", opacity = 0.3f)
 *         }
 *     },
 *     modifier = Modifier.fillMaxSize(),
 *     onError = { error -> 
 *         println("PDF Error: $error") 
 *     }
 * )
 * ```
 */
@Composable
fun PdfViewer(
    config: PdfConfig,
    modifier: Modifier = Modifier,
    onError: (Throwable) -> Unit = {},
    onPageChanged: ((Int, Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var totalPages by remember { mutableIntStateOf(0) }
    var currentPage by remember { mutableIntStateOf(config.viewerConfig.defaultPage) }
    var isLoading by remember { mutableStateOf(true) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    
    val listState = rememberLazyListState()
    
    // Load PDF
    LaunchedEffect(config.source) {
        scope.launch {
            try {
                isLoading = true
                errorState = null
                
                val fileDescriptor = when (val source = config.source) {
                    is PdfSource.File -> ParcelFileDescriptor.open(source.file, ParcelFileDescriptor.MODE_READ_ONLY)
                    is PdfSource.Uri -> context.contentResolver.openFileDescriptor(source.uri, "r")
                    is PdfSource.Asset -> {
                        val inputStream = context.assets.open(source.assetPath)
                        val tempFile = File.createTempFile("pdf_temp", ".pdf", context.cacheDir)
                        tempFile.outputStream().use { output ->
                            inputStream.copyTo(output)
                        }
                        ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    }
                    is PdfSource.Url -> {
                        // For URL, you'd typically download first, then open
                        // This is a simplified version - in production, add proper download handling
                        throw UnsupportedOperationException("URL loading requires network implementation")
                    }
                    is PdfSource.ByteArray -> {
                        val tempFile = File.createTempFile("pdf_temp", ".pdf", context.cacheDir)
                        tempFile.writeBytes(source.bytes)
                        ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    }
                }
                
                fileDescriptor?.let { fd ->
                    val renderer = PdfRenderer(fd)
                    pdfRenderer = renderer
                    totalPages = renderer.pageCount
                    
                    onPageChanged?.invoke(currentPage, totalPages)
                }
                
            } catch (e: Exception) {
                errorState = e.message
                onError(e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Cleanup
    DisposableEffect(pdfRenderer) {
        onDispose {
            pdfRenderer?.close()
        }
    }
    
    val content = @Composable {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading -> {
                    LoadingScreen()
                }
                errorState != null -> {
                    ErrorScreen(errorState!!) {
                        // Retry logic
                        scope.launch {
                            isLoading = true
                            errorState = null
                        }
                    }
                }
                pdfRenderer != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Navigation Bar
                        if (config.navigationConfig.showNavigationButtons) {
                            PdfNavigationBar(
                                currentPage = currentPage + 1,
                                totalPages = totalPages,
                                onPreviousPage = {
                                    if (currentPage > 0) {
                                        currentPage--
                                        scope.launch {
                                            listState.animateScrollToItem(currentPage)
                                        }
                                        onPageChanged?.invoke(currentPage, totalPages)
                                    }
                                },
                                onNextPage = {
                                    if (currentPage < totalPages - 1) {
                                        currentPage++
                                        scope.launch {
                                            listState.animateScrollToItem(currentPage)
                                        }
                                        onPageChanged?.invoke(currentPage, totalPages)
                                    }
                                },
                                onZoomIn = {
                                    scale = min(scale * 1.2f, config.viewerConfig.maxZoom)
                                },
                                onZoomOut = {
                                    scale = max(scale / 1.2f, config.viewerConfig.minZoom)
                                },
                                scale = scale
                            )
                        }
                        
                        // PDF Content
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            PdfContent(
                                pdfRenderer = pdfRenderer!!,
                                totalPages = totalPages,
                                scale = scale,
                                offset = offset,
                                config = config,
                                listState = listState,
                                onScaleChange = { newScale ->
                                    scale = newScale.coerceIn(config.viewerConfig.minZoom, config.viewerConfig.maxZoom)
                                },
                                onOffsetChange = { newOffset ->
                                    offset = newOffset
                                },
                                onPageChanged = { page ->
                                    currentPage = page
                                    onPageChanged?.invoke(currentPage, totalPages)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (config.viewerConfig.applyLibraryTheme) {
        AdaptivePdfTheme(themeConfig = config.themeConfig) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading PDF...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Failed to load PDF",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun PdfNavigationBar(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page Navigation
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPreviousPage,
                    enabled = currentPage > 1
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                }
                
                Text(
                    text = "$currentPage / $totalPages",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = onNextPage,
                    enabled = currentPage < totalPages
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                }
            }
            
            // Zoom Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onZoomOut) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                }
                
                Text(
                    text = "${(scale * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(onClick = onZoomIn) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                }
            }
        }
    }
}

@Composable
private fun PdfContent(
    pdfRenderer: PdfRenderer,
    totalPages: Int,
    scale: Float,
    offset: androidx.compose.ui.geometry.Offset,
    config: PdfConfig,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (androidx.compose.ui.geometry.Offset) -> Unit,
    onPageChanged: (Int) -> Unit
) {
    var transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        if (config.viewerConfig.enableZoom) {
            onScaleChange(scale * zoomChange)
        }
        onOffsetChange(offset + offsetChange)
    }
    
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                if (config.viewerConfig.enableDoubleTap) {
                    detectTapGestures(
                        onDoubleTap = {
                            val newScale = if (scale > config.viewerConfig.midZoom) {
                                config.viewerConfig.minZoom
                            } else {
                                config.viewerConfig.midZoom
                            }
                            onScaleChange(newScale)
                            onOffsetChange(androidx.compose.ui.geometry.Offset.Zero)
                        }
                    )
                }
            },
        verticalArrangement = Arrangement.spacedBy(config.viewerConfig.spacing.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed((0 until totalPages).toList()) { index, pageNum ->
            PdfPage(
                pdfRenderer = pdfRenderer,
                pageIndex = pageNum,
                scale = scale,
                offset = offset,
                config = config,
                onPageVisible = {
                    onPageChanged(pageNum)
                }
            )
        }
    }
}

@Composable
private fun PdfPage(
    pdfRenderer: PdfRenderer,
    pageIndex: Int,
    scale: Float,
    offset: androidx.compose.ui.geometry.Offset,
    config: PdfConfig,
    onPageVisible: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var bitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pageIndex) { mutableStateOf(true) }
    
    LaunchedEffect(pageIndex, scale) {
        scope.launch {
            try {
                isLoading = true
                bitmap = withContext(Dispatchers.IO) {
                    val page = pdfRenderer.openPage(pageIndex)
                    val pageWidth = (page.width * scale).toInt()
                    val pageHeight = (page.height * scale).toInt()
                    
                    val pageBitmap = Bitmap.createBitmap(
                        pageWidth,
                        pageHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    
                    page.render(
                        pageBitmap,
                        null,
                        null,
                        if (config.viewerConfig.enableAntialiasing) {
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        } else {
                            PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                        }
                    )
                    
                    page.close()
                    pageBitmap
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (bitmap != null) bitmap!!.width.toFloat() / bitmap!!.height.toFloat() else 1f)
            .clip(MaterialTheme.shapes.medium)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "PDF Page ${pageIndex + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = when (config.viewerConfig.pageFitPolicy) {
                        com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.WIDTH -> ContentScale.FillWidth
                        com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.HEIGHT -> ContentScale.FillHeight
                        com.androidstuff.adaptivepdfpro.core.ViewerConfig.FitPolicy.BOTH -> ContentScale.Fit
                    }
                )
                
                // Page number overlay
                if (config.viewerConfig.showPageNumbers) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${pageIndex + 1}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
    
    LaunchedEffect(pageIndex) {
        onPageVisible()
    }
}