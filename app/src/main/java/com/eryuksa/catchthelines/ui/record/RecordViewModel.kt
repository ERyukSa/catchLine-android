package com.eryuksa.catchthelines.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: ContentRepository) : ViewModel() {

    private val _uiState = MutableLiveData<RecordUiState>()
    val uiState: LiveData<RecordUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            val caughtContents = repository.getCaughtContents(size = CAUGHT_CONTENTS_CHUNK, offset = 0)
            _uiState.value = RecordUiState(2, 4, caughtContents)
        }
    }

    companion object {
        private const val CAUGHT_CONTENTS_CHUNK = 6
    }
}
