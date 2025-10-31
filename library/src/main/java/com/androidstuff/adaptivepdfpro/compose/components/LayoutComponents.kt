package com.androidstuff.adaptivepdfpro.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidstuff.adaptivepdfpro.compose.theme.PoppinsFontFamily
import com.androidstuff.adaptivepdfpro.data.*

/**
 * BoxLayout component for creating bordered sections with optional shadow and corner radius
 */
@Composable
fun BoxLayout(
    modifier: Modifier = Modifier,
    style: PdfItemStyle = PdfItemStyle(),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = if (style.cornerRadius > 0) {
        RoundedCornerShape(style.cornerRadius.dp)
    } else {
        RoundedCornerShape(0.dp)
    }
    
    var boxModifier = modifier
        .padding(style.margin.dp)
    
    // Apply elevation/shadow if specified
    if (style.elevation > 0) {
        boxModifier = boxModifier.shadow(
            elevation = style.elevation.dp,
            shape = shape
        )
    }
    
    // Apply background color
    if (style.backgroundColor != 0x00000000) {
        boxModifier = boxModifier
            .clip(shape)
            .background(Color(style.backgroundColor))
    }
    
    // Apply border if specified
    if (style.borderWidth > 0 && style.borderStyle != BorderStyle.NONE) {
        boxModifier = when (style.borderStyle) {
            BorderStyle.SOLID -> boxModifier.border(
                width = style.borderWidth.dp,
                color = Color(style.borderColor),
                shape = shape
            )
            BorderStyle.DASHED -> boxModifier.border(
                width = style.borderWidth.dp,
                color = Color(style.borderColor),
                shape = shape
            )
            BorderStyle.DOTTED -> boxModifier.border(
                width = style.borderWidth.dp,
                color = Color(style.borderColor),
                shape = shape
            )
            BorderStyle.DOUBLE -> boxModifier
                .border(
                    width = style.borderWidth.dp,
                    color = Color(style.borderColor),
                    shape = shape
                )
                .padding(2.dp)
                .border(
                    width = style.borderWidth.dp,
                    color = Color(style.borderColor),
                    shape = shape
                )
            else -> boxModifier
        }
    }
    
    Box(
        modifier = boxModifier.padding(style.padding.dp),
        content = content
    )
}

/**
 * GridLayout for creating multi-column layouts with consistent spacing
 */
@Composable
fun GridLayout(
    columns: Int = 2,
    items: List<@Composable () -> Unit>,
    spacing: Float = 8f,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        item()
                    }
                }
                // Fill empty cells if row is incomplete
                repeat(columns - rowItems.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(spacing.dp))
        }
    }
}

/**
 * InlineStyledText for creating badges, tags, or highlighted text segments
 */
@Composable
fun InlineStyledText(
    text: String,
    modifier: Modifier = Modifier,
    style: PdfItemStyle = PdfItemStyle(),
    documentStyle: PdfDocumentStyle = PdfDocumentStyle()
) {
    val shape = if (style.cornerRadius > 0) {
        RoundedCornerShape(style.cornerRadius.dp)
    } else {
        RoundedCornerShape(4.dp)
    }
    
    var boxModifier = modifier
        .padding(style.margin.dp)
    
    // Apply background color
    if (style.backgroundColor != 0x00000000) {
        boxModifier = boxModifier
            .clip(shape)
            .background(Color(style.backgroundColor))
    }
    
    // Apply border if specified
    if (style.borderWidth > 0 && style.borderColor != 0x00000000) {
        boxModifier = boxModifier.border(
            width = style.borderWidth.dp,
            color = Color(style.borderColor),
            shape = shape
        )
    }
    
    Box(
        modifier = boxModifier.padding(
            horizontal = (style.padding * 1.5f).dp,
            vertical = style.padding.dp
        )
    ) {
        Text(
            text = text,
            fontSize = style.fontSize.sp,
            color = Color(style.textColor),
            fontWeight = if (style.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (style.isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
            fontFamily = PoppinsFontFamily,
            textAlign = when (style.alignment) {
                PdfAlignment.LEFT -> TextAlign.Left
                PdfAlignment.CENTER -> TextAlign.Center
                PdfAlignment.RIGHT -> TextAlign.Right
                PdfAlignment.JUSTIFY -> TextAlign.Justify
            }
        )
    }
}

/**
 * HeaderBox for creating styled header sections with company info
 */
@Composable
fun HeaderBox(
    title: String,
    companyInfo: List<String>,
    style: PdfItemStyle = PdfItemStyle(),
    documentStyle: PdfDocumentStyle = PdfDocumentStyle()
) {
    BoxLayout(
        style = style.copy(
            backgroundColor = if (style.backgroundColor != 0x00000000) style.backgroundColor else 0xFFF5F5F5.toInt(),
            borderWidth = if (style.borderWidth > 0) style.borderWidth else 1f,
            borderColor = if (style.borderColor != 0x00000000) style.borderColor else documentStyle.colors.borderColor,
            cornerRadius = if (style.cornerRadius > 0) style.cornerRadius else 8f,
            padding = if (style.padding > 0) style.padding else 16f
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Title section
            Text(
                text = title,
                fontSize = documentStyle.typography.titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFontFamily,
                color = Color(documentStyle.colors.primaryColor),
                modifier = Modifier.weight(1f)
            )
            
            // Company info section
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                companyInfo.forEach { info ->
                    Text(
                        text = info,
                        fontSize = documentStyle.typography.bodyFontSize.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color(documentStyle.colors.secondaryColor),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

/**
 * DataCard for displaying key-value pairs in a card layout
 */
@Composable
fun DataCard(
    title: String? = null,
    data: List<Pair<String, String>>,
    style: PdfItemStyle = PdfItemStyle(),
    documentStyle: PdfDocumentStyle = PdfDocumentStyle()
) {
    BoxLayout(
        style = style.copy(
            backgroundColor = if (style.backgroundColor != 0x00000000) style.backgroundColor else 0xFFFFFFFF.toInt(),
            borderWidth = if (style.borderWidth > 0) style.borderWidth else 1f,
            borderColor = if (style.borderColor != 0x00000000) style.borderColor else documentStyle.colors.borderColor,
            cornerRadius = if (style.cornerRadius > 0) style.cornerRadius else 4f,
            elevation = if (style.elevation > 0) style.elevation else 2f,
            padding = if (style.padding > 0) style.padding else 12f
        )
    ) {
        Column {
            title?.let {
                Text(
                    text = it,
                    fontSize = documentStyle.typography.headerFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFontFamily,
                    color = Color(documentStyle.colors.primaryColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            data.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = documentStyle.typography.bodyFontSize.sp,
                        fontFamily = PoppinsFontFamily,
                        color = Color(documentStyle.colors.secondaryColor),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        fontSize = documentStyle.typography.bodyFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = PoppinsFontFamily,
                        color = Color(documentStyle.colors.primaryColor),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * StyledTable with enhanced border and alternating row colors
 */
@Composable
fun StyledTable(
    headers: List<String>,
    rows: List<List<String>>,
    style: PdfItemStyle = PdfItemStyle(),
    documentStyle: PdfDocumentStyle = PdfDocumentStyle()
) {
    BoxLayout(
        style = style.copy(
            borderWidth = if (style.borderWidth > 0) style.borderWidth else 1f,
            borderColor = if (style.borderColor != 0x00000000) style.borderColor else documentStyle.colors.borderColor,
            cornerRadius = if (style.cornerRadius > 0) style.cornerRadius else 4f,
            padding = 0f
        )
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(documentStyle.colors.headerColor))
                    .padding(12.dp)
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
            
            // Data rows with alternating colors
            rows.forEachIndexed { index, row ->
                val backgroundColor = if (style.alternateRowColor != 0x00000000 && index % 2 == 1) {
                    Color(style.alternateRowColor)
                } else {
                    Color.White
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(12.dp)
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell,
                            fontSize = documentStyle.typography.bodyFontSize.sp,
                            fontFamily = PoppinsFontFamily,
                            color = Color(documentStyle.colors.primaryColor),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Divider between rows
                if (index < rows.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(documentStyle.colors.borderColor).copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}