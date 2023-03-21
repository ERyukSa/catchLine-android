package com.eryuksa.catchthelines.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordUiState>(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = RecordUiState(
                caughtContents = repository.getCaughtContents(size = CAUGHT_CONTENTS_CHUNK, offset = 0),
                caughtContentsCount = repository.getCaughtContentsCount(),
                encounteredContentsCount = repository.getEncounteredContentsCount()
            )
        }
    }

    companion object {
        private const val CAUGHT_CONTENTS_CHUNK = 6
    }
}
