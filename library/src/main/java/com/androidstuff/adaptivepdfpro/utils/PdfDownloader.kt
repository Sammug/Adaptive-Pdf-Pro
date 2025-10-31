package com.androidstuff.adaptivepdfpro.utils

import android.content.Context
import android.os.Environment
import com.androidstuff.adaptivepdfpro.core.DownloadConfig
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Utility class for downloading PDF files
 */
class PdfDownloader(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val downloadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Download a PDF from a URL
     */
    fun download(
        url: String,
        config: DownloadConfig,
        onProgress: ((Int) -> Unit)? = null,
        onComplete: (File?) -> Unit
    ) {
        downloadScope.launch {
            try {
                val file = downloadFile(url, config, onProgress)
                withContext(Dispatchers.Main) {
                    onComplete(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
        }
    }
    
    private suspend fun downloadFile(
        url: String,
        config: DownloadConfig,
        onProgress: ((Int) -> Unit)?
    ): File = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        
        val response = client.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw IOException("Failed to download file: ${response.code}")
        }
        
        val fileName = getFileNameFromUrl(url)
        val downloadDir = getDownloadDirectory(config)
        val file = File(downloadDir, fileName)
        
        response.body?.let { body ->
            val contentLength = body.contentLength()
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                
                if (contentLength > 0) {
                    val progress = ((totalBytesRead * 100) / contentLength).toInt()
                    withContext(Dispatchers.Main) {
                        onProgress?.invoke(progress)
                    }
                }
            }
            
            outputStream.close()
            inputStream.close()
        }
        
        return@withContext file
    }
    
    private fun getDownloadDirectory(config: DownloadConfig): File {
        return if (config.downloadDirectory != null) {
            File(config.downloadDirectory)
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
        }
    }
    
    private fun getFileNameFromUrl(url: String): String {
        val urlParts = url.split("/")
        val fileName = urlParts.lastOrNull() ?: "document.pdf"
        return if (fileName.endsWith(".pdf", ignoreCase = true)) {
            fileName
        } else {
            "$fileName.pdf"
        }
    }
    
    /**
     * Cancel all ongoing downloads
     */
    fun cancelAll() {
        downloadScope.cancel()
    }
}