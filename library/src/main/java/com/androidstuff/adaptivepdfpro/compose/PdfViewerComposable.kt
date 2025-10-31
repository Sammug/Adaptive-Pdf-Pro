package com.androidstuff.adaptivepdfpro.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidstuff.adaptivepdfpro.compose.components.*
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.compose.theme.PoppinsFontFamily
import com.androidstuff.adaptivepdfpro.compose.viewmodel.PdfGeneratorViewModel
import com.androidstuff.adaptivepdfpro.data.*
import com.androidstuff.adaptivepdfpro.theme.ThemeConfig
import java.io.File

/**
 * AdaptivePdfPro - Professional PDF Generator Library
 * 
 * A comprehensive Jetpack Compose library for generating PDF documents from any structured data
 * with live preview capabilities. Completely generic and flexible - perfect for creating any type 
 * of document: reports, forms, certificates, layouts, and more.
 * 
 * Key Features:
 * ✅ Live preview with WYSIWYG editing
 * ✅ Generic data structure - no business assumptions  
 * ✅ Professional styling with comprehensive theming
 * ✅ Flexible content types: TEXT, TABLES, KEY-VALUE pairs, DIVIDERS
 * ✅ Builder pattern for easy document construction
 * ✅ Export to PDF files with sharing capabilities
 * ✅ Built with pure Android APIs (no external dependencies)
 * ✅ Reactive UI with StateFlow progress tracking
 * 
 * @see [PdfReportGenerator] for main PDF generation functionality
 */

/**
 * Primary PDF generator composable with live preview and export functionality.
 * 
 * This composable displays a real-time preview of how the PDF will appear and provides
 * action buttons for generating and sharing the PDF file. The layout is fully customizable
 * through the PdfLayoutConfig parameter.
 * 
 * Example Usage:
 * ```kotlin
 * // Basic Usage
 * PdfReportGenerator(
 *     data = buildPdfContent {
 *         header("Sales Report", "Q1 2024") {
 *             dataItem("Company", "ACME Corp")
 *             dataItem("Department", "Sales")
 *         }
 *         section("Performance") {
 *             keyValuePairs(listOf(
 *                 "Revenue" to "$125,000",
 *                 "Growth" to "15%"
 *             ))
 *             table(
 *                 headers = listOf("Product", "Units", "Revenue"),
 *                 rows = listOf(
 *                     listOf("Product A", "150", "$75,000"),
 *                     listOf("Product B", "100", "$50,000")
 *                 )
 *             )
 *         }
 *         footer {
 *             leftText("Confidential")
 *             rightText("Page 1 of 1")
 *         }
 *     },
 *     documentStyle = PdfDocumentStyle(
 *         colors = PdfColorScheme(
 *             primaryColor = 0xFF2196F3.toInt(),
 *             accentColor = 0xFF4CAF50.toInt()
 *         )
 *     ),
 *     onPdfGenerated = { file -> shareDocument(file) }
 * )
 * ```
 * 
 * @param data The structured data to display and generate as PDF
 * @param modifier Compose modifier for styling
 * @param documentStyle Configuration for fonts, colors, spacing, and layout
 * @param onPdfGenerated Callback invoked when PDF generation completes successfully
 * @param onError Callback for handling PDF generation errors
 */
@Composable
fun PdfReportGenerator(
    data: PdfContentData,
    modifier: Modifier = Modifier,
    documentStyle: PdfDocumentStyle = PdfDocumentStyle(),
    onPdfGenerated: ((File) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    val viewModel: PdfGeneratorViewModel = viewModel()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val generatedFile by viewModel.generatedPdfFile.collectAsState()

    AdaptivePdfTheme(themeConfig = ThemeConfig.default()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Action Bar with Generate and Share buttons
            PdfActionBar(
                isGenerating = isGenerating,
                onGeneratePdf = {
                    viewModel.generatePdf(context, data, documentStyle)
                },
                onSharePdf = {
                    generatedFile?.let { file ->
                        viewModel.sharePdf(context, file)
                    } ?: run {
                        viewModel.generatePdf(context, data, documentStyle)
                    }
                }
            )
            
            // Progress indicator during generation
            if (isGenerating) {
                LinearProgressIndicator(
                    progress = { generationProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            HorizontalDivider()
            
            // Live Preview - Shows exactly what the PDF will look like
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                PdfDocumentLayoutGeneric(
                    data = data,
                    documentStyle = documentStyle
                )
            }
        }
    }
}



// Private helper composables for internal use

@Composable
private fun PdfActionBar(
    isGenerating: Boolean,
    onGeneratePdf: () -> Unit,
    onSharePdf: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
    ) {
        Button(
            onClick = onGeneratePdf,
            enabled = !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generating...", fontFamily = PoppinsFontFamily)
            } else {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Generate PDF", fontFamily = PoppinsFontFamily)
            }
        }
        
        Button(
            onClick = onSharePdf,
            enabled = !isGenerating
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Share", fontFamily = PoppinsFontFamily)
        }
    }
}

@Composable
private fun PdfDocumentLayoutGeneric(
    data: PdfContentData,
    documentStyle: PdfDocumentStyle
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = documentStyle.margins.top.dp,
                    start = documentStyle.margins.left.dp,
                    end = documentStyle.margins.right.dp,
                    bottom = documentStyle.margins.bottom.dp
                )
        ) {
            // Header
            data.header?.let { header ->
                PdfHeaderSectionGeneric(header, documentStyle)
                Spacer(modifier = Modifier.height(documentStyle.spacing.sectionSpacing.dp))
            }
            
            // Sections
            data.sections.forEach { section ->
                PdfContentSectionGeneric(section, documentStyle)
                Spacer(modifier = Modifier.height(documentStyle.spacing.sectionSpacing.dp))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer
            data.footer?.let { footer ->
                PdfFooterSectionGeneric(footer, documentStyle)
            }
        }
    }
}

@Composable
private fun PdfHeaderSectionGeneric(
    headerData: com.androidstuff.adaptivepdfpro.data.PdfHeaderSection,
    documentStyle: PdfDocumentStyle
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left: Title and Subtitle
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = headerData.title,
                    fontSize = documentStyle.typography.titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFontFamily,
                    color = Color(documentStyle.colors.primaryColor),
                    letterSpacing = 0.5.sp
                )
                headerData.subtitle?.let { subtitle ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = documentStyle.typography.subtitleFontSize.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color(documentStyle.colors.secondaryColor),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Center: Logo placeholder
            headerData.logoResourceId?.let {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Color(documentStyle.colors.accentColor).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            Color(documentStyle.colors.borderColor).copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGO",
                        fontSize = 10.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color(documentStyle.colors.secondaryColor),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Right: Header items
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.End
            ) {
                headerData.headerItems.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.label}:",
                            fontSize = documentStyle.typography.captionFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.secondaryColor),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.value,
                            fontSize = documentStyle.typography.bodyFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.primaryColor),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun PdfContentSectionGeneric(
    sectionData: com.androidstuff.adaptivepdfpro.data.PdfContentSection,
    documentStyle: PdfDocumentStyle
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        sectionData.title?.let { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(0.3f),
                    color = Color(documentStyle.colors.accentColor).copy(alpha = 0.3f),
                    thickness = 1.5.dp
                )
                
                Text(
                    text = title,
                    fontSize = documentStyle.typography.headerFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFontFamily,
                    color = Color(documentStyle.colors.primaryColor),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    letterSpacing = 0.5.sp
                )
                
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(documentStyle.colors.accentColor).copy(alpha = 0.3f),
                    thickness = 1.5.dp
                )
            }
        }
        
        sectionData.items.forEachIndexed { index, item ->
            PdfContentItemGeneric(item, documentStyle)
            
            if (index < sectionData.items.size - 1) {
                Spacer(modifier = Modifier.height(documentStyle.spacing.itemSpacing.dp))
            }
        }
    }
}

@Composable
private fun PdfContentItemGeneric(
    item: com.androidstuff.adaptivepdfpro.data.PdfContentItem,
    documentStyle: PdfDocumentStyle
) {
    when (item.type) {
        com.androidstuff.adaptivepdfpro.data.PdfContentType.TEXT -> {
            item.data.text?.let { text ->
                // Use BoxLayout if border or background styles are set
                if (item.styling.borderWidth > 0 || item.styling.backgroundColor != 0x00000000 || item.styling.elevation > 0) {
                    BoxLayout(style = item.styling) {
                        Text(
                            text = text,
                            fontSize = item.styling.fontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(item.styling.textColor),
                            fontWeight = if (item.styling.isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (item.styling.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                        )
                    }
                } else {
                    Text(
                        text = text,
                        fontSize = item.styling.fontSize.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color(item.styling.textColor),
                        fontWeight = if (item.styling.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (item.styling.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                        modifier = Modifier.padding(item.styling.padding.dp)
                    )
                }
            }
        }
        com.androidstuff.adaptivepdfpro.data.PdfContentType.TABLE -> {
            // Use StyledTable with enhanced border support
            if (item.styling.borderWidth > 0 || item.styling.alternateRowColor != 0x00000000) {
                StyledTable(
                    headers = item.data.listData,
                    rows = item.data.nestedData.map { it.cells },
                    style = item.styling,
                    documentStyle = documentStyle
                )
            } else {
                PdfTableGeneric(item, documentStyle)
            }
        }
        com.androidstuff.adaptivepdfpro.data.PdfContentType.KEY_VALUE_PAIRS -> {
            // Use DataCard for bordered key-value pairs
            if (item.styling.borderWidth > 0 || item.styling.backgroundColor != 0x00000000) {
                DataCard(
                    data = item.data.nestedData.filter { it.cells.size >= 2 }.map { it.cells[0] to it.cells[1] },
                    style = item.styling,
                    documentStyle = documentStyle
                )
            } else {
                PdfKeyValuePairsGeneric(item, documentStyle)
            }
        }
        com.androidstuff.adaptivepdfpro.data.PdfContentType.DIVIDER -> {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(documentStyle.colors.borderColor)
            )
        }
        com.androidstuff.adaptivepdfpro.data.PdfContentType.SPACER -> {
            val height = item.data.numericValues["height"] ?: 16f
            Spacer(modifier = Modifier.height(height.dp))
        }
        else -> {
            Text(
                text = "[${item.type}]",
                fontSize = documentStyle.typography.bodyFontSize.sp,
                fontFamily = PoppinsFontFamily,
                color = Color(documentStyle.colors.secondaryColor),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun PdfTableGeneric(
    item: com.androidstuff.adaptivepdfpro.data.PdfContentItem,
    documentStyle: PdfDocumentStyle
) {
    val headers = item.data.listData
    val rows = item.data.nestedData
    
    if (headers.isNotEmpty() && rows.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Color(documentStyle.colors.borderColor).copy(alpha = 0.3f)
                )
        ) {
            Column {
                // Enhanced header row with better styling
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(documentStyle.colors.accentColor).copy(alpha = 0.1f),
                                    Color(documentStyle.colors.headerColor)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    headers.forEach { header ->
                        Text(
                            text = header,
                            fontSize = documentStyle.typography.headerFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.primaryColor),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Enhanced data rows with alternating colors
                rows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 0) Color.White
                                else Color(documentStyle.colors.backgroundColor).copy(alpha = 0.03f)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        row.cells.forEach { cell ->
                            Text(
                                text = cell,
                                fontSize = documentStyle.typography.bodyFontSize.sp,
                                fontFamily = PoppinsFontFamily,
                                color = Color(documentStyle.colors.primaryColor),
                                modifier = Modifier.weight(1f),
                                maxLines = 2
                            )
                        }
                    }
                    
                    // Add subtle divider between rows
                    if (index < rows.size - 1) {
                        HorizontalDivider(
                            color = Color(documentStyle.colors.borderColor).copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfKeyValuePairsGeneric(
    item: com.androidstuff.adaptivepdfpro.data.PdfContentItem,
    documentStyle: PdfDocumentStyle
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            item.data.nestedData.forEachIndexed { index, row ->
                if (row.cells.size >= 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.cells[0],
                            fontSize = documentStyle.typography.bodyFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.secondaryColor),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = row.cells[1],
                            fontSize = documentStyle.typography.bodyFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.primaryColor),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    if (index < item.data.nestedData.size - 1) {
                        HorizontalDivider(
                            color = Color(documentStyle.colors.borderColor).copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfFooterSectionGeneric(
    footerData: com.androidstuff.adaptivepdfpro.data.PdfFooterSection,
    documentStyle: PdfDocumentStyle
) {
    Column {
        HorizontalDivider(
            color = Color(documentStyle.colors.borderColor).copy(alpha = 0.4f),
            thickness = 1.dp
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(documentStyle.colors.backgroundColor).copy(alpha = 0.01f)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section
                Box(modifier = Modifier.weight(1f)) {
                    footerData.leftText?.let { text ->
                        Text(
                            text = text,
                            fontSize = documentStyle.typography.captionFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.secondaryColor),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Center section
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    footerData.centerText?.let { text ->
                        Text(
                            text = text,
                            fontSize = documentStyle.typography.captionFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.secondaryColor),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Right section
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        footerData.rightText?.let { text ->
                            Text(
                                text = text,
                                fontSize = documentStyle.typography.captionFontSize.sp,
                                fontFamily = PoppinsFontFamily,
                                color = Color(documentStyle.colors.secondaryColor),
                                textAlign = TextAlign.End
                            )
                        }
                        
                        if (footerData.showPageNumbers) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Page 1",
                                fontSize = (documentStyle.typography.captionFontSize - 1).sp,
                                fontFamily = PoppinsFontFamily,
                                color = Color(documentStyle.colors.secondaryColor).copy(alpha = 0.8f),
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }
}

