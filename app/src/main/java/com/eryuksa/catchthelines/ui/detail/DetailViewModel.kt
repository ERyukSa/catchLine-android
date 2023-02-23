package com.eryuksa.catchthelines.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.repository.ContentRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: ContentRepository) : ViewModel() {

    private val _contentDetail = MutableLiveData<ContentDetail>()
    val contentDetail: LiveData<ContentDetail>
        get() = _contentDetail

    init {
        viewModelScope.launch {
            _contentDetail.value = repository.getContentDetail(343611) ?: return@launch
        }
    }
}
