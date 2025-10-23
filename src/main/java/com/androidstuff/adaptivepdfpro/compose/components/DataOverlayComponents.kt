package com.androidstuff.adaptivepdfpro.compose.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.androidstuff.adaptivepdfpro.data.*
import kotlin.math.roundToInt

/**
 * Container for all data overlays
 */
@Composable
fun DataOverlayContainer(
    config: PageDataConfig,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Data Overlays
        config.overlays.forEach { overlay ->
            when (overlay) {
                is DataOverlay.Table -> TableOverlay(
                    overlay = overlay,
                    currentPage = currentPage
                )
                is DataOverlay.Chart -> ChartOverlay(
                    overlay = overlay,
                    currentPage = currentPage
                )
                is DataOverlay.Custom -> {
                    // Custom overlay would be handled here
                }
            }
        }
        
        // Info Panels
        config.infoPanels.forEach { panel ->
            if (panel.pageNumbers.isEmpty() || currentPage in panel.pageNumbers) {
                InfoPanelOverlay(panel = panel)
            }
        }
        
        // Floating Cards
        config.floatingCards.forEach { card ->
            if (card.pageNumber == currentPage) {
                FloatingCardOverlay(card = card)
            }
        }
        
        // Tooltips
        config.tooltips.forEach { tooltip ->
            if (tooltip.pageNumber == currentPage) {
                TooltipOverlay(tooltip = tooltip)
            }
        }
        
        // Annotations
        config.annotations.forEach { annotation ->
            if (annotation.pageNumber == currentPage) {
                AnnotationOverlay(annotation = annotation)
            }
        }
    }
}

/**
 * Table overlay for displaying tabular data
 */
@Composable
fun TableOverlay(
    overlay: DataOverlay.Table,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val relevantData = overlay.data.filter { it.pageNumber == currentPage }
    
    if (relevantData.isNotEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(overlay.style.headerBackground))
                        .padding(overlay.style.cellPadding.dp)
                ) {
                    Text(
                        text = "Page Data",
                        color = Color(overlay.style.headerTextColor),
                        fontSize = overlay.style.headerTextSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Table Rows
                relevantData.forEach { pageData ->
                    DataRow(
                        data = pageData,
                        style = overlay.style
                    )
                }
            }
        }
    }
}

@Composable
private fun DataRow(
    data: PageData,
    style: TableStyle
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = style.borderWidth.dp,
                color = Color(style.borderColor)
            )
            .clickable {
                if (data.isExpandable) expanded = !expanded
                data.clickAction?.invoke()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(style.rowBackground))
                .padding(style.cellPadding.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.title,
                    color = Color(style.textColor),
                    fontSize = style.cellTextSize.sp,
                    fontWeight = FontWeight.Medium
                )
                data.subtitle?.let {
                    Text(
                        text = it,
                        color = Color(style.textColor).copy(alpha = 0.7f),
                        fontSize = (style.cellTextSize - 2).sp
                    )
                }
            }
            
            if (data.isExpandable) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color(style.textColor)
                )
            }
        }
        
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(style.alternateRowBackground))
                    .padding(style.cellPadding.dp)
            ) {
                data.data.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = key,
                            color = Color(style.textColor),
                            fontSize = style.cellTextSize.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = value.toString(),
                            color = Color(style.textColor).copy(alpha = 0.8f),
                            fontSize = style.cellTextSize.sp
                        )
                    }
                }
                
                if (data.children.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    data.children.forEach { child ->
                        DataRow(data = child, style = style)
                    }
                }
            }
        }
    }
}

/**
 * Chart overlay for displaying charts
 */
@Composable
fun ChartOverlay(
    overlay: DataOverlay.Chart,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(
                width = when (overlay.size) {
                    OverlaySize.SMALL -> 150.dp
                    OverlaySize.MEDIUM -> 200.dp
                    OverlaySize.LARGE -> 300.dp
                    OverlaySize.FULL_WIDTH -> 400.dp
                    else -> 200.dp
                },
                height = when (overlay.size) {
                    OverlaySize.SMALL -> 150.dp
                    OverlaySize.MEDIUM -> 200.dp
                    OverlaySize.LARGE -> 300.dp
                    OverlaySize.FULL_HEIGHT -> 400.dp
                    else -> 200.dp
                }
            )
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Chart rendering would go here based on overlay.chartData.type
            Text(
                text = "${overlay.chartData.type} Chart",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Info panel overlay
 */
@Composable
fun InfoPanelOverlay(
    panel: InfoPanel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(panel.isExpandedByDefault) }
    
    val alignment = when (panel.position) {
        InfoPanelPosition.LEFT -> Alignment.CenterStart
        InfoPanelPosition.RIGHT -> Alignment.CenterEnd
        InfoPanelPosition.TOP -> Alignment.TopCenter
        InfoPanelPosition.BOTTOM -> Alignment.BottomCenter
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .align(alignment)
                .widthIn(max = 300.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(panel.backgroundColor)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = panel.title,
                        color = Color(panel.textColor),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (panel.isCollapsible) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = Color(panel.textColor)
                            )
                        }
                    }
                }
                
                AnimatedVisibility(visible = isExpanded) {
                    Text(
                        text = panel.content,
                        color = Color(panel.textColor).copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Floating card overlay
 */
@Composable
fun FloatingCardOverlay(
    card: FloatingCard,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isVisible by remember { mutableStateOf(true) }
    
    if (isVisible) {
        val density = LocalDensity.current
        
        val cardModifier = if (card.isDraggable) {
            Modifier
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offset = Offset(
                            offset.x + dragAmount.x,
                            offset.y + dragAmount.y
                        )
                    }
                }
        } else {
            Modifier
        }
        
        Card(
            modifier = cardModifier
                .size(width = card.width.dp, height = card.height.dp)
                .padding(16.dp)
                .shadow(
                    elevation = card.elevation.dp,
                    shape = RoundedCornerShape(card.cornerRadius.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(card.backgroundColor)
            ),
            shape = RoundedCornerShape(card.cornerRadius.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = card.title,
                        color = Color(card.titleColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (card.isCloseable) {
                        IconButton(
                            onClick = { isVisible = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(card.titleColor)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = card.content,
                    color = Color(card.contentColor),
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Tooltip overlay
 */
@Composable
fun TooltipOverlay(
    tooltip: Tooltip,
    modifier: Modifier = Modifier
) {
    var showTooltip by remember { mutableStateOf(false) }
    
    LaunchedEffect(tooltip.autoDismiss) {
        if (tooltip.autoDismiss) {
            showTooltip = true
            kotlinx.coroutines.delay(tooltip.dismissDelay)
            showTooltip = false
        }
    }
    
    if (showTooltip || !tooltip.autoDismiss) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(tooltip.x.toInt(), tooltip.y.toInt())
        ) {
            Card(
                modifier = Modifier
                    .width(tooltip.width.dp)
                    .height(tooltip.height.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(tooltip.backgroundColor)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tooltip.text,
                        color = Color(tooltip.textColor),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Annotation overlay
 */
@Composable
fun AnnotationOverlay(
    annotation: PageAnnotation,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(x = annotation.x.dp, y = annotation.y.dp)
            .size(width = annotation.width.dp, height = annotation.height.dp)
            .then(
                when (annotation.type) {
                    AnnotationType.HIGHLIGHT -> Modifier.background(Color(annotation.color))
                    AnnotationType.RECTANGLE -> Modifier.border(
                        width = annotation.strokeWidth.dp,
                        color = Color(annotation.color)
                    )
                    else -> Modifier
                }
            )
            .clickable(enabled = annotation.isInteractive) {
                annotation.clickAction?.invoke()
            }
    ) {
        annotation.content?.let { content ->
            Text(
                text = content,
                color = Color(annotation.color),
                fontSize = 12.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}