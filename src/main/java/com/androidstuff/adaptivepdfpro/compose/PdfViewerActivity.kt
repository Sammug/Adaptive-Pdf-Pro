package com.androidstuff.adaptivepdfpro.compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.androidstuff.adaptivepdfpro.core.PdfConfig

/**
 * Compose-based PDF Viewer Activity
 */
class PdfViewerActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_CONFIG = "pdf_config"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_CONFIG, PdfConfig::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_CONFIG) as? PdfConfig
        } ?: throw IllegalArgumentException("PdfConfig must be provided")
        
        setContent {
            PdfViewerScreen(
                config = config,
                onBackPressed = { finish() }
            )
        }
    }
}

/**
 * Full-screen PDF viewer composable
 */
@Composable
fun PdfViewerScreen(
    config: PdfConfig,
    onBackPressed: () -> Unit = {}
) {
    PdfViewer(
        config = config,
        onError = { error ->
            // Handle error
        }
    )
}