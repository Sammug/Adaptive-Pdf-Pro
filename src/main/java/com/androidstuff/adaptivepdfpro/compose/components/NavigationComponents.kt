package com.androidstuff.adaptivepdfpro.compose.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidstuff.adaptivepdfpro.navigation.*
import com.androidstuff.adaptivepdfpro.ui.viewmodels.Bookmark
import com.androidstuff.adaptivepdfpro.ui.viewmodels.SearchResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PDF Navigation Bar with all controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfNavigationBar(
    config: NavigationBarConfig,
    currentPage: Int,
    pageCount: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageJump: (Int) -> Unit,
    onToggleNightMode: () -> Unit,
    onShowThumbnails: () -> Unit,
    onShowSearch: () -> Unit,
    onShowBookmarks: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(config.autoHide) {
        if (config.autoHide) {
            delay(config.autoHideDelay)
            visible = false
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(config.backgroundColor),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Page
                IconButton(
                    onClick = onPreviousPage,
                    enabled = currentPage > 0
                ) {
                    Icon(
                        Icons.Default.NavigateBefore,
                        contentDescription = "Previous Page",
                        tint = Color(config.iconColor)
                    )
                }
                
                // Page Number
                if (config.showPageNumber) {
                    Text(
                        text = "${currentPage + 1} / $pageCount",
                        color = Color(config.iconColor),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                
                // Next Page
                IconButton(
                    onClick = onNextPage,
                    enabled = currentPage < pageCount - 1
                ) {
                    Icon(
                        Icons.Default.NavigateNext,
                        contentDescription = "Next Page",
                        tint = Color(config.iconColor)
                    )
                }
                
                // Zoom Controls
                if (config.showZoomControls) {
                    Row {
                        IconButton(onClick = { /* Zoom in */ }) {
                            Icon(
                                Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = Color(config.iconColor)
                            )
                        }
                        IconButton(onClick = { /* Zoom out */ }) {
                            Icon(
                                Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = Color(config.iconColor)
                            )
                        }
                    }
                }
                
                // More Options
                Row {
                    IconButton(onClick = onShowThumbnails) {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = "Thumbnails",
                            tint = Color(config.iconColor)
                        )
                    }
                    
                    IconButton(onClick = onShowSearch) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(config.iconColor)
                        )
                    }
                    
                    IconButton(onClick = onShowBookmarks) {
                        Icon(
                            Icons.Default.Bookmark,
                            contentDescription = "Bookmarks",
                            tint = Color(config.iconColor)
                        )
                    }
                    
                    if (config.showShareButton) {
                        IconButton(onClick = onShare) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(config.iconColor)
                            )
                        }
                    }
                    
                    if (config.showDownloadButton) {
                        IconButton(onClick = onDownload) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color(config.iconColor)
                            )
                        }
                    }
                    
                    if (config.showRotateButton) {
                        IconButton(onClick = { /* Rotate */ }) {
                            Icon(
                                Icons.Default.RotateRight,
                                contentDescription = "Rotate",
                                tint = Color(config.iconColor)
                            )
                        }
                    }
                    
                    IconButton(onClick = onToggleNightMode) {
                        Icon(
                            Icons.Default.Brightness4,
                            contentDescription = "Night Mode",
                            tint = Color(config.iconColor)
                        )
                    }
                    
                    if (config.showFullscreenButton) {
                        IconButton(onClick = { /* Fullscreen */ }) {
                            Icon(
                                Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color(config.iconColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * PDF Page Slider
 */
@Composable
fun PdfPageSlider(
    currentPage: Int,
    pageCount: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pageCount > 1) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Page ${currentPage + 1} of $pageCount",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = currentPage.toFloat(),
                    onValueChange = { onPageChange(it.toInt()) },
                    valueRange = 0f..(pageCount - 1).toFloat(),
                    steps = pageCount - 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Page Indicator
 */
@Composable
fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    config: PageIndicatorConfig,
    modifier: Modifier = Modifier
) {
    if (config.show) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(config.cornerRadius.dp),
            color = Color(config.backgroundColor)
        ) {
            Text(
                text = formatPageIndicator(currentPage, pageCount, config.format),
                color = Color(config.textColor),
                fontSize = config.textSize.sp,
                modifier = Modifier.padding(config.padding.dp)
            )
        }
    }
}

private fun formatPageIndicator(currentPage: Int, pageCount: Int, format: PageIndicatorFormat): String {
    val page = currentPage + 1
    return when (format) {
        PageIndicatorFormat.CURRENT_ONLY -> "$page"
        PageIndicatorFormat.CURRENT_OF_TOTAL -> "$page / $pageCount"
        PageIndicatorFormat.CURRENT_DASH_TOTAL -> "$page - $pageCount"
        PageIndicatorFormat.PAGE_CURRENT -> "Page $page"
        PageIndicatorFormat.PAGE_CURRENT_OF_TOTAL -> "Page $page of $pageCount"
        PageIndicatorFormat.PERCENTAGE -> "${(page * 100) / pageCount}%"
        PageIndicatorFormat.CUSTOM -> "$page / $pageCount"
    }
}

/**
 * Thumbnails Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThumbnailsBottomSheet(
    config: ThumbnailConfig,
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Pages",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(config.gridColumns),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(pageCount) { page ->
                    ThumbnailItem(
                        pageNumber = page,
                        isSelected = page == currentPage,
                        config = config,
                        onClick = { onPageSelected(page) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    pageNumber: Int,
    isSelected: Boolean,
    config: ThumbnailConfig,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(config.thumbnailSize.dp.dp)
            .clickable { onClick() },
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = config.selectedBorderWidth.dp,
                brush = androidx.compose.ui.graphics.SolidColor(Color(config.selectedBorderColor))
            )
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Thumbnail preview would go here
            if (config.showPageNumbers) {
                Text(
                    text = "${pageNumber + 1}",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Search Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    config: SearchConfig,
    onSearch: (String) -> Unit,
    onResultSelected: (SearchResult) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Search in PDF",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (config.autoSearch && it.length >= config.minSearchLength) {
                        onSearch(it)
                    }
                },
                label = { Text("Search text") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch(searchQuery) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            if (config.caseSensitive || config.wholeWord) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    if (config.caseSensitive) {
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text("Case Sensitive") }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (config.wholeWord) {
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text("Whole Word") }
                        )
                    }
                }
            }
            
            // Search results would be displayed here
            searchResults.forEach { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onResultSelected(result) }
                ) {
                    Text(
                        text = "Page ${result.pageNumber + 1}: ${result.text}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Bookmarks Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksBottomSheet(
    bookmarks: List<Bookmark>,
    onBookmarkSelected: (Bookmark) -> Unit,
    onAddBookmark: () -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bookmarks",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = onAddBookmark) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Bookmark"
                    )
                }
            }
            
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bookmarks yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                bookmarks.forEach { bookmark ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onBookmarkSelected(bookmark) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = bookmark.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Page ${bookmark.pageNumber + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onRemoveBookmark(bookmark.id) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove Bookmark",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}