package com.civicfix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicfix.data.repository.AuthRepository
import com.civicfix.data.repository.IssueRepository
import com.civicfix.domain.model.Issue
import com.civicfix.domain.model.TimelineEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val repo:     IssueRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    val userId = authRepo.userId
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val allIssues: StateFlow<List<Issue>> = repo.allIssues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _myIssues      = MutableStateFlow<List<Issue>>(emptyList())
    val myIssues = _myIssues.asStateFlow()

    private val _selectedIssue = MutableStateFlow<Issue?>(null)
    val selectedIssue = _selectedIssue.asStateFlow()

    private val _timeline      = MutableStateFlow<List<TimelineEntry>>(emptyList())
    val timeline = _timeline.asStateFlow()

    private val _isLoading     = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing  = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error         = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _reportSuccess = MutableStateFlow(false)
    val reportSuccess = _reportSuccess.asStateFlow()

    val searchQuery      = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)
    val selectedStatus   = MutableStateFlow<String?>(null)

    val filteredIssues = combine(
        allIssues, searchQuery, selectedCategory, selectedStatus
    ) { issues, q, cat, stat ->
        issues.filter { issue ->
            val matchQ    = q.isBlank() ||
                issue.title.contains(q, ignoreCase = true) ||
                issue.description.contains(q, ignoreCase = true)
            val matchCat  = cat == null || issue.category == cat
            val matchStat = stat == null || issue.status == stat
            matchQ && matchCat && matchStat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _isRefreshing.value = true
        try { repo.refreshIssues() } catch (e: Exception) { _error.value = e.message }
        _isRefreshing.value = false
    }

    fun loadMyIssues(uid: Int) = viewModelScope.launch {
        try { repo.refreshMyIssues() } catch (e: Exception) { /* use cache */ }
        repo.myIssues(uid).collect { _myIssues.value = it }
    }

    fun loadIssueDetail(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        repo.getIssueDetail(id)
            .onSuccess { _selectedIssue.value = it }
            .onFailure { _error.value = it.message }
        repo.getTimeline(id)
            .onSuccess { _timeline.value = it }
        _isLoading.value = false
    }

    fun reportIssue(
        title:       String,
        description: String,
        category:    String,
        lat:         Double,
        lng:         Double,
        address:     String,
        imageFile:   File?
    ) = viewModelScope.launch {
        _isLoading.value = true
        val result = repo.createIssue(
            title, description, category, lat, lng, address, imageFile
        )
        if (result.isSuccess) {
            _reportSuccess.value = true
            refresh()
        } else {
            _error.value = result.exceptionOrNull()?.message ?: "Failed to submit"
        }
        _isLoading.value = false
    }

    fun voteIssue(id: Int) = viewModelScope.launch {
        repo.voteIssue(id)
        refresh()
    }

    fun addComment(issueId: Int, content: String) = viewModelScope.launch {
        repo.addComment(issueId, content)
        loadIssueDetail(issueId)
    }

    fun clearReportSuccess() { _reportSuccess.value = false }
    fun clearError()         { _error.value = null }
}
