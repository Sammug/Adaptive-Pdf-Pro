package com.androidstuff.adaptivepdfpro.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidstuff.adaptivepdfpro.branding.BrandingConfig
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfSource
import com.androidstuff.adaptivepdfpro.data.*
import com.androidstuff.adaptivepdfpro.theme.ThemeConfig
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generate PDF from data content
 */
@Composable
fun PdfFromData(
    data: PdfContentData,
    modifier: Modifier = Modifier,
    brandingConfig: BrandingConfig = BrandingConfig(),
    themeConfig: ThemeConfig = ThemeConfig.default(),
    onPageChanged: ((Int, Int) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    AdaptivePdfTheme(themeConfig = themeConfig) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = data.styling.margins.top.dp,
                        end = data.styling.margins.right.dp,
                        bottom = data.styling.margins.bottom.dp,
                        start = data.styling.margins.left.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Document Header
                data.header?.let { header ->
                    item {
                        DocumentHeaderView(header, data.styling.headerStyle)
                    }
                }
                
                // Content Items
                items(data.items) { item ->
                    ContentItemView(item, data.styling)
                }
                
                // Document Summary
                data.summary?.let { summary ->
                    item {
                        DocumentSummaryView(summary, data.styling.summaryStyle)
                    }
                }
                
                // Document Footer
                data.footer?.let { footer ->
                    item {
                        DocumentFooterView(footer)
                    }
                }
            }
            
            // Branding overlay
            if (brandingConfig.logos.isNotEmpty() || brandingConfig.watermark != null) {
                com.androidstuff.adaptivepdfpro.compose.components.BrandingOverlay(
                    config = brandingConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Document header view
 */
@Composable
private fun DocumentHeaderView(
    header: DocumentHeader,
    style: HeaderStyle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(style.backgroundColor)
        ),
        border = if (style.showBorder) {
            BorderStroke(1.dp, Color(style.borderColor))
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Business Info
                Column(modifier = Modifier.weight(1f)) {
                    // Logo
                    header.businessLogo?.let { logo ->
                        when (logo) {
                            is Int -> Image(
                                painter = painterResource(id = logo),
                                contentDescription = "Business Logo",
                                modifier = Modifier.size(style.logoSize.dp),
                                contentScale = ContentScale.Fit
                            )
                            // Handle other logo types
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Text(
                        text = header.businessName,
                        fontSize = (style.titleSize - 4).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.textColor)
                    )
                    
                    header.businessAddress?.let { address ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = address.formatted(),
                            fontSize = 10.sp,
                            color = Color(style.textColor).copy(alpha = 0.7f),
                            lineHeight = 14.sp
                        )
                    }
                    
                    header.businessContact?.let { contact ->
                        Spacer(modifier = Modifier.height(4.dp))
                        contact.phone?.let {
                            Text(
                                text = "Tel: $it",
                                fontSize = 10.sp,
                                color = Color(style.textColor).copy(alpha = 0.7f)
                            )
                        }
                        contact.email?.let {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                color = Color(style.textColor).copy(alpha = 0.7f)
                            )
                        }
                        contact.website?.let {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                color = Color(style.textColor).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Document Info
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = header.title,
                        fontSize = style.titleSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.textColor)
                    )
                    
                    header.subtitle?.let {
                        Text(
                            text = it,
                            fontSize = style.subtitleSize.sp,
                            color = Color(style.textColor).copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    header.documentNumber?.let {
                        Text(
                            text = "#$it",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(style.textColor)
                        )
                    }
                    
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(header.date),
                        fontSize = 12.sp,
                        color = Color(style.textColor).copy(alpha = 0.7f)
                    )
                    
                    header.customFields.forEach { (key, value) ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$key: $value",
                            fontSize = 11.sp,
                            color = Color(style.textColor).copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            header.description?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(style.borderColor))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(style.textColor),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Content item view dispatcher
 */
@Composable
private fun ContentItemView(
    item: ContentItem,
    styling: ContentStyling
) {
    when (item) {
        is ContentItem.Transaction -> TransactionItemView(item, styling.itemStyle)
        is ContentItem.LineItem -> LineItemView(item, styling.itemStyle)
        is ContentItem.TableData -> TableDataView(item, styling.itemStyle)
        is ContentItem.TextContent -> TextContentView(item)
        is ContentItem.ChartContent -> ChartContentView(item)
        is ContentItem.KeyValuePairs -> KeyValuePairsView(item)
        is ContentItem.ImageContent -> ImageContentView(item)
        is ContentItem.Separator -> SeparatorView(item)
        is ContentItem.CustomContent -> {
            // Custom content rendering
        }
    }
}

/**
 * Transaction item view
 */
@Composable
private fun TransactionItemView(
    transaction: ContentItem.Transaction,
    style: ItemStyle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(style.backgroundColor)
        ),
        border = if (style.showBorders) {
            BorderStroke(0.5.dp, Color(style.borderColor))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(style.padding.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = style.fontSize.sp,
                    color = Color(style.textColor),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(transaction.date),
                        fontSize = (style.fontSize - 1).sp,
                        color = Color(style.textColor).copy(alpha = 0.6f)
                    )
                    transaction.reference?.let {
                        Text(
                            text = "Ref: $it",
                            fontSize = (style.fontSize - 1).sp,
                            color = Color(style.textColor).copy(alpha = 0.6f)
                        )
                    }
                    transaction.category?.let {
                        Text(
                            text = it,
                            fontSize = (style.fontSize - 1).sp,
                            color = Color(style.textColor).copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val currencyFormat = NumberFormat.getCurrencyInstance()
                Text(
                    text = currencyFormat.format(transaction.amount),
                    fontSize = style.fontSize.sp,
                    color = Color(style.textColor),
                    fontWeight = FontWeight.Bold
                )
                
                if (transaction.quantity > 1) {
                    Text(
                        text = "Qty: ${transaction.quantity}",
                        fontSize = (style.fontSize - 1).sp,
                        color = Color(style.textColor).copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Line item view (for invoices)
 */
@Composable
private fun LineItemView(
    item: ContentItem.LineItem,
    style: ItemStyle
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(style.backgroundColor))
            .padding(style.padding.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Item details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = style.fontSize.sp,
                color = Color(style.textColor),
                fontWeight = FontWeight.Medium
            )
            item.description?.let {
                Text(
                    text = it,
                    fontSize = (style.fontSize - 1).sp,
                    color = Color(style.textColor).copy(alpha = 0.7f)
                )
            }
        }
        
        // Quantity
        Text(
            text = "${item.quantity} ${item.unit ?: ""}",
            fontSize = style.fontSize.sp,
            color = Color(style.textColor),
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.Center
        )
        
        // Unit price
        val currencyFormat = NumberFormat.getCurrencyInstance()
        Text(
            text = currencyFormat.format(item.unitPrice),
            fontSize = style.fontSize.sp,
            color = Color(style.textColor),
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
        
        // Total
        val total = item.total ?: (item.quantity.multiply(item.unitPrice))
        Text(
            text = currencyFormat.format(total),
            fontSize = style.fontSize.sp,
            color = Color(style.textColor),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
    }
    
    if (style.showBorders) {
        Divider(color = Color(style.borderColor), thickness = 0.5.dp)
    }
}

/**
 * Table data view
 */
@Composable
private fun TableDataView(
    table: ContentItem.TableData,
    style: ItemStyle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = if (style.showBorders) {
            BorderStroke(0.5.dp, Color(style.borderColor))
        } else null
    ) {
        Column {
            // Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(style.borderColor).copy(alpha = 0.1f))
                    .padding(style.padding.dp)
            ) {
                table.headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        fontSize = style.fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.textColor),
                        modifier = Modifier.weight(table.columnWidths?.get(index) ?: 1f),
                        textAlign = when (table.alignment.getOrNull(index)) {
                            ColumnAlignment.CENTER -> TextAlign.Center
                            ColumnAlignment.RIGHT -> TextAlign.End
                            else -> TextAlign.Start
                        }
                    )
                }
            }
            
            // Rows
            table.rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (style.useAlternateColors && rowIndex % 2 == 1) {
                                Color(style.alternateBackgroundColor)
                            } else {
                                Color(style.backgroundColor)
                            }
                        )
                        .padding(style.padding.dp)
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        Text(
                            text = cell,
                            fontSize = style.fontSize.sp,
                            color = Color(style.textColor),
                            modifier = Modifier.weight(table.columnWidths?.get(colIndex) ?: 1f),
                            textAlign = when (table.alignment.getOrNull(colIndex)) {
                                ColumnAlignment.CENTER -> TextAlign.Center
                                ColumnAlignment.RIGHT -> TextAlign.End
                                else -> TextAlign.Start
                            }
                        )
                    }
                }
                
                if (style.showBorders && rowIndex < table.rows.size - 1) {
                    Divider(color = Color(style.borderColor), thickness = 0.5.dp)
                }
            }
            
            // Totals
            if (table.showTotals && table.totals != null) {
                Divider(color = Color(style.borderColor), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(style.borderColor).copy(alpha = 0.05f))
                        .padding(style.padding.dp)
                ) {
                    table.totals.forEachIndexed { index, total ->
                        Text(
                            text = total,
                            fontSize = style.fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(style.textColor),
                            modifier = Modifier.weight(table.columnWidths?.get(index) ?: 1f),
                            textAlign = when (table.alignment.getOrNull(index)) {
                                ColumnAlignment.CENTER -> TextAlign.Center
                                ColumnAlignment.RIGHT -> TextAlign.End
                                else -> TextAlign.Start
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Document summary view
 */
@Composable
private fun DocumentSummaryView(
    summary: DocumentSummary,
    style: SummaryStyle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(style.backgroundColor)
        ),
        border = if (style.showBorder) {
            BorderStroke(1.dp, Color(style.borderColor))
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val currencyFormat = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(summary.currency)
            }
            
            // Subtotal
            summary.subtotal?.let {
                SummaryRow(
                    label = "Subtotal",
                    value = currencyFormat.format(it),
                    style = style
                )
            }
            
            // Discount
            summary.discount?.let {
                SummaryRow(
                    label = summary.discountLabel,
                    value = "- ${currencyFormat.format(it)}",
                    style = style,
                    valueColor = Color(0xFFFF5722)
                )
            }
            
            // Tax
            summary.tax?.let {
                val taxLabel = if (summary.taxRate != null) {
                    "${summary.taxLabel} (${summary.taxRate}%)"
                } else {
                    summary.taxLabel
                }
                SummaryRow(
                    label = taxLabel,
                    value = currencyFormat.format(it),
                    style = style
                )
            }
            
            // Shipping
            summary.shipping?.let {
                SummaryRow(
                    label = summary.shippingLabel,
                    value = currencyFormat.format(it),
                    style = style
                )
            }
            
            // Custom calculations
            summary.customCalculations.forEach { calc ->
                val value = when (calc.type) {
                    CalculationType.ADD -> currencyFormat.format(calc.value)
                    CalculationType.SUBTRACT -> "- ${currencyFormat.format(calc.value)}"
                    CalculationType.NONE -> if (calc.showCurrency) {
                        currencyFormat.format(calc.value)
                    } else {
                        calc.value.toString()
                    }
                }
                SummaryRow(
                    label = calc.label,
                    value = value,
                    style = style,
                    valueColor = if (calc.type == CalculationType.SUBTRACT) {
                        Color(0xFFFF5722)
                    } else null
                )
            }
            
            // Total
            summary.total?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(style.borderColor))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(style.totalBackgroundColor))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = summary.totalLabel,
                        fontSize = style.totalFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.totalTextColor)
                    )
                    Text(
                        text = currencyFormat.format(it),
                        fontSize = style.totalFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.totalTextColor)
                    )
                }
            }
            
            // Amount Paid/Due
            summary.amountPaid?.let {
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(
                    label = "Amount Paid",
                    value = currencyFormat.format(it),
                    style = style,
                    valueColor = Color(0xFF4CAF50)
                )
            }
            
            summary.amountDue?.let {
                SummaryRow(
                    label = "Amount Due",
                    value = currencyFormat.format(it),
                    style = style,
                    valueColor = if (it > java.math.BigDecimal.ZERO) {
                        Color(0xFFFF5722)
                    } else {
                        Color(0xFF4CAF50)
                    }
                )
            }
            
            // Notes
            summary.notes?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(style.borderColor))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Notes:",
                    fontSize = style.fontSize.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(style.textColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = (style.fontSize - 1).sp,
                    color = Color(style.textColor).copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    style: SummaryStyle,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = style.fontSize.sp,
            color = Color(style.textColor)
        )
        Text(
            text = value,
            fontSize = style.fontSize.sp,
            color = valueColor ?: Color(style.textColor),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Other content views
 */
@Composable
private fun TextContentView(content: ContentItem.TextContent) {
    Text(
        text = content.text,
        fontSize = content.style.size.sp,
        color = Color(content.style.color),
        fontWeight = if (content.style.isBold) FontWeight.Bold else FontWeight.Normal,
        textAlign = when (content.style.alignment) {
            ContentAlignment.CENTER -> TextAlign.Center
            ContentAlignment.RIGHT -> TextAlign.End
            ContentAlignment.JUSTIFY -> TextAlign.Justify
            else -> TextAlign.Start
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ChartContentView(content: ContentItem.ChartContent) {
    // Chart implementation would go here
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("${content.type} Chart")
        }
    }
}

@Composable
private fun KeyValuePairsView(content: ContentItem.KeyValuePairs) {
    Column {
        content.title?.let {
            Text(
                text = it,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        content.pairs.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = content.style.spacing.dp / 2),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = key,
                    fontSize = content.style.keySize.sp,
                    color = Color(content.style.keyColor)
                )
                Text(
                    text = value,
                    fontSize = content.style.valueSize.sp,
                    color = Color(content.style.valueColor)
                )
            }
            if (content.style.showSeparator) {
                Divider()
            }
        }
    }
}

@Composable
private fun ImageContentView(content: ContentItem.ImageContent) {
    Column(
        horizontalAlignment = when (content.alignment) {
            ContentAlignment.CENTER -> Alignment.CenterHorizontally
            ContentAlignment.RIGHT -> Alignment.End
            else -> Alignment.Start
        }
    ) {
        // Image would be rendered here based on source type
        Box(
            modifier = Modifier
                .then(
                    if (content.width != null && content.height != null) {
                        Modifier.size(content.width.dp, content.height.dp)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Image")
        }
        content.caption?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SeparatorView(separator: ContentItem.Separator) {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = separator.thickness.dp,
        color = Color(separator.color)
    )
}

@Composable
private fun DocumentFooterView(footer: DocumentFooter) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        footer.terms?.let {
            Text(
                text = "Terms & Conditions:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        footer.paymentInfo?.let { paymentInfo ->
            Text(
                text = "Payment Information:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            paymentInfo.bankDetails?.let { bank ->
                Column(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Bank: ${bank.bankName}",
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Account: ${bank.accountNumber}",
                        fontSize = 10.sp
                    )
                    bank.iban?.let {
                        Text(
                            text = "IBAN: $it",
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        footer.signature?.let { signature ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (signature.showLine) {
                        Divider(
                            modifier = Modifier.width(200.dp),
                            thickness = 1.dp
                        )
                    }
                    signature.signerName?.let {
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    signature.signerTitle?.let {
                        Text(
                            text = it,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        
        footer.text?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}