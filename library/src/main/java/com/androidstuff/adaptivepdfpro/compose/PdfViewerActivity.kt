package com.androidstuff.adaptivepdfpro.compose

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfViewer

/**
 * Modern PDF Viewer Activity with comprehensive viewing capabilities.
 * 
 * Supports viewing PDFs from various sources with zoom, navigation, theming, and branding.
 * Activity automatically handles PDF loading, error states, and user interactions.
 */
class PdfViewerActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_CONFIG = "pdf_config"
        const val EXTRA_TITLE = "pdf_title"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CONFIG, PdfConfig::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CONFIG) as? PdfConfig
        } ?: throw IllegalArgumentException("PdfConfig must be provided")
        
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "PDF Viewer"
        
        setContent {
            PdfViewerScreen(
                config = config,
                title = title,
                onBackPressed = { finish() }
            )
        }
    }
}

/**
 * Full-screen PDF viewer composable with app bar and comprehensive features.
 * 
 * Example Usage:
 * ```kotlin
 * PdfViewerScreen(
 *     config = PdfConfig.build {
 *         load(pdfFile)
 *         setViewer {
 *             enableZoom(true)
 *             pageFitPolicy(ViewerConfig.FitPolicy.WIDTH)
 *         }
 *     },
 *     title = "My Document",
 *     onBackPressed = { finish() }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    config: PdfConfig,
    title: String = "PDF Viewer", 
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(title)
                        if (totalPages > 0) {
                            Text(
                                text = "Page $currentPage of $totalPages",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        PdfViewer(
            config = config,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onError = { error: Throwable ->
                Toast.makeText(
                    context,
                    "Error loading PDF: ${error.message ?: "Unknown error"}",
                    Toast.LENGTH_LONG
                ).show()
            },
            onPageChanged = { page: Int, total: Int ->
                currentPage = page + 1
                totalPages = total
            }
        )
    }
}