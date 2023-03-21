package com.eryuksa.catchthelines.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContentDetailUiState>(ContentDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun getDetailUiState(id: Int, audioUrls: Array<String>) {
        viewModelScope.launch {
            _uiState.value = repository.getContentDetail(id)?.run {
                ContentDetailUiState(
                    title = title,
                    backdropPosterUrl = backdropPosterUrl,
                    mainPosterPathUrl = mainPosterPathUrl,
                    genres = genres,
                    overview = overview,
                    shortSummary = shortSummary,
                    releaseDate = releaseDate,
                    runningTime = runningTime,
                    audioUrls = audioUrls.toList()
                )
            } ?: return@launch
        }
    }
}
