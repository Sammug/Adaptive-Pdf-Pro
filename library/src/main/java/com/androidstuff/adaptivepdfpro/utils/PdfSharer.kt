package com.androidstuff.adaptivepdfpro.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.androidstuff.adaptivepdfpro.core.SharingConfig
import java.io.File

/**
 * Utility class for sharing PDF files
 */
class PdfSharer(private val context: Context) {
    
    /**
     * Share a PDF file
     */
    fun share(file: File, config: SharingConfig) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        share(uri, config)
    }
    
    /**
     * Share a PDF from URI
     */
    fun share(uri: Uri, config: SharingConfig) {
        if (!config.enabled) return
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            
            config.customShareText?.let {
                putExtra(Intent.EXTRA_TEXT, it)
            }
            
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, config.shareTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
    
    /**
     * Share multiple PDF files
     */
    fun shareMultiple(files: List<File>, config: SharingConfig) {
        if (!config.enabled || files.isEmpty()) return
        
        val uris = files.map { file ->
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
        
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "application/pdf"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            
            config.customShareText?.let {
                putExtra(Intent.EXTRA_TEXT, it)
            }
            
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(intent, config.shareTitle)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}