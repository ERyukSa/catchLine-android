package com.eryuksa.catchthelines.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: ContentRepository) : ViewModel() {

    private val _uiState = MutableLiveData<ContentDetailUiState>()
    val uiState: LiveData<ContentDetailUiState>
        get() = _uiState

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
