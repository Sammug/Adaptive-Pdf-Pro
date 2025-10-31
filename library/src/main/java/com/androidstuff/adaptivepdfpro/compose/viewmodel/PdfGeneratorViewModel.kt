package com.androidstuff.adaptivepdfpro.compose.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidstuff.adaptivepdfpro.data.PdfContentData
import com.androidstuff.adaptivepdfpro.data.PdfDocumentStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ViewModel for PDF generation operations with progress tracking and state management.
 * Handles the conversion of Compose UI previews to actual PDF documents.
 */
class PdfGeneratorViewModel : ViewModel() {

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    private val _generatedPdfFile = MutableStateFlow<File?>(null)
    val generatedPdfFile: StateFlow<File?> = _generatedPdfFile.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Generates a PDF document from the provided report data and layout configuration.
     * 
     * @param context Android context for file operations
     * @param data The report data to be rendered in the PDF
     * @param layoutConfig Layout configuration for PDF styling
     * @param outputFileName Optional custom filename for the generated PDF
     * @return File object representing the generated PDF, or null if generation failed
     */
    fun generatePdf(
        context: Context,
        data: PdfContentData,
        documentStyle: PdfDocumentStyle = PdfDocumentStyle(),
        outputFileName: String = "generated_report_${System.currentTimeMillis()}.pdf"
    ) {
        viewModelScope.launch {
            try {
                _isGenerating.value = true
                _generationProgress.value = 0f
                _errorMessage.value = null

                val pdfFile = withContext(Dispatchers.IO) {
                    createPdfDocument(context, data, documentStyle, outputFileName)
                }

                _generatedPdfFile.value = pdfFile
                _generationProgress.value = 1f
            } catch (e: Exception) {
                _errorMessage.value = "PDF generation failed: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Creates the actual PDF document using Android's PdfDocument API.
     */
    private suspend fun createPdfDocument(
        context: Context,
        data: PdfContentData,
        documentStyle: PdfDocumentStyle,
        fileName: String
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = documentStyle.pageWidth
        val pageHeight = documentStyle.pageHeight

        try {
            _generationProgress.value = 0.1f

            // Create page info
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            _generationProgress.value = 0.3f

            // Draw content on canvas
            drawPdfContent(canvas, data, documentStyle, pageWidth, pageHeight)

            _generationProgress.value = 0.7f

            // Finish the page
            pdfDocument.finishPage(page)

            _generationProgress.value = 0.9f

            // Save to file
            val outputFile = File(context.cacheDir, fileName)
            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            return outputFile
        } finally {
            pdfDocument.close()
        }
    }

    /**
     * Draws the report content onto the PDF canvas.
     */
    private fun drawPdfContent(
        canvas: Canvas,
        data: PdfContentData,
        documentStyle: PdfDocumentStyle,
        pageWidth: Int,
        pageHeight: Int
    ) {
        val paint = android.graphics.Paint().apply {
            textSize = documentStyle.typography.baseFontSize
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }

        var currentY = documentStyle.margins.top
        val marginLeft = documentStyle.margins.left
        val marginRight = documentStyle.margins.right

        // Draw header
        data.header?.let { header ->
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText(header.title, marginLeft, currentY, paint)
            currentY += 40f

            paint.textSize = 16f
            paint.isFakeBoldText = false
            header.subtitle?.let { subtitle ->
                canvas.drawText(subtitle, marginLeft, currentY, paint)
                currentY += 30f
            }

            // Draw header metadata
            paint.textSize = 12f
            header.headerItems.forEach { item ->
                canvas.drawText("${item.label}: ${item.value}", marginLeft, currentY, paint)
                currentY += 20f
            }
            currentY += 20f
        }

        // Draw sections
        data.sections.forEach { section ->
            // Section title
            paint.textSize = 18f
            paint.isFakeBoldText = true
            section.title?.let { title ->
                canvas.drawText(title, marginLeft, currentY, paint)
            }
            currentY += 35f

            // Section items
            paint.textSize = 12f
            paint.isFakeBoldText = false
            section.items.forEach { item ->
                when (item.type) {
                    com.androidstuff.adaptivepdfpro.data.PdfContentType.TEXT -> {
                        item.data.text?.let { text ->
                            canvas.drawText(text, marginLeft, currentY, paint)
                            currentY += 25f
                        }
                    }
                    com.androidstuff.adaptivepdfpro.data.PdfContentType.TABLE -> {
                        drawTable(canvas, item, marginLeft, currentY, pageWidth - marginRight, paint)
                        currentY += calculateTableHeight(item) + 20f
                    }
                    com.androidstuff.adaptivepdfpro.data.PdfContentType.IMAGE -> {
                        canvas.drawText("[Image]", marginLeft, currentY, paint)
                        currentY += 30f
                    }
                    else -> {
                        canvas.drawText("[${item.type}]", marginLeft, currentY, paint)
                        currentY += 25f
                    }
                }
            }
            currentY += 30f
        }

        // Draw footer
        data.footer?.let { footer ->
            val footerY = pageHeight - documentStyle.margins.bottom
            paint.textSize = 10f
            footer.leftText?.let { text ->
                canvas.drawText(text, marginLeft, footerY, paint)
            }
            footer.centerText?.let { text ->
                val textWidth = paint.measureText(text)
                canvas.drawText(text, (pageWidth - textWidth) / 2, footerY, paint)
            }
            footer.rightText?.let { text ->
                val textWidth = paint.measureText(text)
                canvas.drawText(text, pageWidth - marginRight - textWidth, footerY, paint)
            }
            if (footer.showPageNumbers) {
                val pageText = "Page 1"
                val textWidth = paint.measureText(pageText)
                canvas.drawText(pageText, pageWidth - marginRight - textWidth, footerY - 15f, paint)
            }
        }
    }

    /**
     * Draws a table on the canvas.
     */
    private fun drawTable(
        canvas: Canvas,
        item: com.androidstuff.adaptivepdfpro.data.PdfContentItem,
        startX: Float,
        startY: Float,
        maxWidth: Float,
        paint: android.graphics.Paint
    ) {
        var currentY = startY
        val rowHeight = 25f
        val cellPadding = 5f

        // Extract table data from item
        val headers = item.data.listData
        val rows = item.data.nestedData
        if (headers.isEmpty() || rows.isEmpty()) return
        val columnWidth = (maxWidth - startX) / headers.size

        // Draw headers
        paint.isFakeBoldText = true
        headers.forEachIndexed { index, header ->
            val cellX = startX + (index * columnWidth) + cellPadding
            canvas.drawText(header, cellX, currentY, paint)
        }
        currentY += rowHeight

        // Draw data rows
        paint.isFakeBoldText = false
        rows.forEach { row ->
            row.cells.forEachIndexed { index, cell ->
                val cellX = startX + (index * columnWidth) + cellPadding
                canvas.drawText(cell, cellX, currentY, paint)
            }
            currentY += rowHeight
        }
    }

    /**
     * Calculates the height required for a table.
     */
    private fun calculateTableHeight(item: com.androidstuff.adaptivepdfpro.data.PdfContentItem): Float {
        return (item.data.nestedData.size + 1) * 25f // +1 for header
    }

    /**
     * Shares the generated PDF file using Android's share intent.
     */
    fun sharePdf(context: Context, file: File) {
        viewModelScope.launch {
            try {
                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    type = "application/pdf"
                    putExtra(android.content.Intent.EXTRA_STREAM, 
                        androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        ))
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share PDF"))
            } catch (e: Exception) {
                _errorMessage.value = "Failed to share PDF: ${e.message}"
            }
        }
    }

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Resets the generation state.
     */
    fun resetGenerationState() {
        _isGenerating.value = false
        _generationProgress.value = 0f
        _generatedPdfFile.value = null
        _errorMessage.value = null
    }
}