package com.androidstuff.adaptivepdfpro.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PdfViewerViewModel : ViewModel() {
    
    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage
    
    private val _pageCount = MutableLiveData(0)
    val pageCount: LiveData<Int> = _pageCount
    
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isNightMode = MutableLiveData(false)
    val isNightMode: LiveData<Boolean> = _isNightMode
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _searchResults = MutableLiveData<List<SearchResult>>()
    val searchResults: LiveData<List<SearchResult>> = _searchResults
    
    private val _bookmarks = MutableLiveData<List<Bookmark>>(emptyList())
    val bookmarks: LiveData<List<Bookmark>> = _bookmarks
    
    private val _annotations = MutableLiveData<List<Annotation>>(emptyList())
    val annotations: LiveData<List<Annotation>> = _annotations
    
    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }
    
    fun setPageCount(count: Int) {
        _pageCount.value = count
    }
    
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    
    fun toggleNightMode() {
        _isNightMode.value = !(_isNightMode.value ?: false)
    }
    
    fun searchInPdf(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            // Perform search operation
            // This would integrate with the PDF library to search text
            val results = performSearch(query)
            _searchResults.value = results
        }
    }
    
    private suspend fun performSearch(query: String): List<SearchResult> {
        // Placeholder for search implementation
        return emptyList()
    }
    
    fun addBookmark(pageNumber: Int, title: String? = null) {
        val currentBookmarks = _bookmarks.value ?: emptyList()
        val bookmark = Bookmark(
            id = System.currentTimeMillis().toString(),
            pageNumber = pageNumber,
            title = title ?: "Page $pageNumber"
        )
        _bookmarks.value = currentBookmarks + bookmark
    }
    
    fun removeBookmark(bookmarkId: String) {
        val currentBookmarks = _bookmarks.value ?: emptyList()
        _bookmarks.value = currentBookmarks.filter { it.id != bookmarkId }
    }
    
    fun addAnnotation(annotation: Annotation) {
        val currentAnnotations = _annotations.value ?: emptyList()
        _annotations.value = currentAnnotations + annotation
    }
    
    fun removeAnnotation(annotationId: String) {
        val currentAnnotations = _annotations.value ?: emptyList()
        _annotations.value = currentAnnotations.filter { it.id != annotationId }
    }
    
    fun jumpToPage(page: Int) {
        if (page in 0 until (_pageCount.value ?: 0)) {
            _currentPage.value = page
        }
    }
    
    fun nextPage() {
        val current = _currentPage.value ?: 0
        val total = _pageCount.value ?: 0
        if (current < total - 1) {
            _currentPage.value = current + 1
        }
    }
    
    fun previousPage() {
        val current = _currentPage.value ?: 0
        if (current > 0) {
            _currentPage.value = current - 1
        }
    }
    
    fun firstPage() {
        _currentPage.value = 0
    }
    
    fun lastPage() {
        _pageCount.value?.let {
            _currentPage.value = it - 1
        }
    }
}

data class SearchResult(
    val pageNumber: Int,
    val text: String,
    val position: Int
)

data class Bookmark(
    val id: String,
    val pageNumber: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Annotation(
    val id: String,
    val pageNumber: Int,
    val type: AnnotationType,
    val content: String,
    val x: Float,
    val y: Float,
    val color: Int,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnnotationType {
    HIGHLIGHT,
    NOTE,
    UNDERLINE,
    STRIKETHROUGH
}