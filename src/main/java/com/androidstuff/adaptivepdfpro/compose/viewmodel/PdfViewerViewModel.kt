package com.androidstuff.adaptivepdfpro.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for PDF Viewer with Compose StateFlow
 */
class PdfViewerViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()
    
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isNightMode = MutableStateFlow(false)
    val isNightMode: StateFlow<Boolean> = _isNightMode.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()
    
    private val _annotations = MutableStateFlow<List<Annotation>>(emptyList())
    val annotations: StateFlow<List<Annotation>> = _annotations.asStateFlow()
    
    private val _zoomLevel = MutableStateFlow(1f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()
    
    private val _rotation = MutableStateFlow(0f)
    val rotation: StateFlow<Float> = _rotation.asStateFlow()
    
    fun setCurrentPage(page: Int) {
        _currentPage.value = page
        updateUiState { it.copy(currentPage = page) }
    }
    
    fun setPageCount(count: Int) {
        _pageCount.value = count
        updateUiState { it.copy(pageCount = count) }
    }
    
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
        updateUiState { it.copy(isLoading = loading) }
    }
    
    fun toggleNightMode() {
        _isNightMode.value = !_isNightMode.value
        updateUiState { it.copy(isNightMode = _isNightMode.value) }
    }
    
    fun setZoomLevel(zoom: Float) {
        _zoomLevel.value = zoom.coerceIn(0.5f, 3f)
        updateUiState { it.copy(zoomLevel = _zoomLevel.value) }
    }
    
    fun zoomIn() {
        setZoomLevel(_zoomLevel.value + 0.25f)
    }
    
    fun zoomOut() {
        setZoomLevel(_zoomLevel.value - 0.25f)
    }
    
    fun resetZoom() {
        setZoomLevel(1f)
    }
    
    fun rotatePage(degrees: Float = 90f) {
        _rotation.value = (_rotation.value + degrees) % 360
        updateUiState { it.copy(rotation = _rotation.value) }
    }
    
    fun searchInPdf(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val results = performSearch(query)
            _searchResults.value = results
            updateUiState { it.copy(searchQuery = query, searchResults = results) }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        updateUiState { it.copy(searchQuery = "", searchResults = emptyList()) }
    }
    
    private suspend fun performSearch(query: String): List<SearchResult> {
        // Placeholder for actual PDF search implementation
        return emptyList()
    }
    
    fun addBookmark(pageNumber: Int = _currentPage.value, title: String? = null) {
        val bookmark = Bookmark(
            id = System.currentTimeMillis().toString(),
            pageNumber = pageNumber,
            title = title ?: "Page ${pageNumber + 1}",
            timestamp = System.currentTimeMillis()
        )
        _bookmarks.value = _bookmarks.value + bookmark
        updateUiState { it.copy(bookmarks = _bookmarks.value) }
    }
    
    fun removeBookmark(bookmarkId: String) {
        _bookmarks.value = _bookmarks.value.filter { it.id != bookmarkId }
        updateUiState { it.copy(bookmarks = _bookmarks.value) }
    }
    
    fun toggleBookmark(pageNumber: Int = _currentPage.value) {
        val existingBookmark = _bookmarks.value.find { it.pageNumber == pageNumber }
        if (existingBookmark != null) {
            removeBookmark(existingBookmark.id)
        } else {
            addBookmark(pageNumber)
        }
    }
    
    fun isPageBookmarked(pageNumber: Int): Boolean {
        return _bookmarks.value.any { it.pageNumber == pageNumber }
    }
    
    fun addAnnotation(annotation: Annotation) {
        _annotations.value = _annotations.value + annotation
        updateUiState { it.copy(annotations = _annotations.value) }
    }
    
    fun removeAnnotation(annotationId: String) {
        _annotations.value = _annotations.value.filter { it.id != annotationId }
        updateUiState { it.copy(annotations = _annotations.value) }
    }
    
    fun updateAnnotation(annotationId: String, updater: (Annotation) -> Annotation) {
        _annotations.value = _annotations.value.map { annotation ->
            if (annotation.id == annotationId) updater(annotation) else annotation
        }
        updateUiState { it.copy(annotations = _annotations.value) }
    }
    
    fun jumpToPage(page: Int) {
        val validPage = page.coerceIn(0, (_pageCount.value - 1).coerceAtLeast(0))
        setCurrentPage(validPage)
    }
    
    fun nextPage() {
        jumpToPage(_currentPage.value + 1)
    }
    
    fun previousPage() {
        jumpToPage(_currentPage.value - 1)
    }
    
    fun firstPage() {
        jumpToPage(0)
    }
    
    fun lastPage() {
        jumpToPage(_pageCount.value - 1)
    }
    
    private fun updateUiState(update: (PdfViewerUiState) -> PdfViewerUiState) {
        _uiState.value = update(_uiState.value)
    }
}

/**
 * UI State for PDF Viewer
 */
data class PdfViewerUiState(
    val currentPage: Int = 0,
    val pageCount: Int = 0,
    val isLoading: Boolean = true,
    val isNightMode: Boolean = false,
    val zoomLevel: Float = 1f,
    val rotation: Float = 0f,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val annotations: List<Annotation> = emptyList(),
    val error: String? = null,
    val showNavigationBar: Boolean = true,
    val showThumbnails: Boolean = false,
    val showSearch: Boolean = false,
    val showBookmarks: Boolean = false,
    val isFullscreen: Boolean = false
)

/**
 * Search result data class
 */
data class SearchResult(
    val pageNumber: Int,
    val text: String,
    val position: Int,
    val snippet: String = ""
)

/**
 * Bookmark data class
 */
data class Bookmark(
    val id: String,
    val pageNumber: Int,
    val title: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Annotation data class
 */
data class Annotation(
    val id: String,
    val pageNumber: Int,
    val type: AnnotationType,
    val content: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Annotation types
 */
enum class AnnotationType {
    HIGHLIGHT,
    NOTE,
    UNDERLINE,
    STRIKETHROUGH,
    DRAWING,
    TEXT,
    RECTANGLE,
    CIRCLE,
    ARROW
}