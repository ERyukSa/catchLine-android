package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.Contents
import com.eryuksa.catchthelines.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _currentPagePosition = MutableLiveData<Int>()
    val currentPagePosition: LiveData<Int>
        get() = _currentPagePosition

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess
        get() = _gameItems.value ?: emptyList()

    private val _hintText = MutableLiveData<String>()
    val hintText: LiveData<String>
        get() = _hintText

    private val _feedbackText = MutableLiveData<String>()
    val feedbackText: LiveData<String>
        get() = _feedbackText

    private val _availableHintCount = MutableLiveData<Int>()
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getGameItems().map(Contents::toGameItem)
        }
    }

    fun changePage(position: Int) {
        _currentPagePosition.value = position
    }

    fun changeCurrentPosterBlurDegree(degree: Int) {
        val currentPosition = _currentPagePosition.value ?: throw IllegalStateException()
        val changedGameItem = gameItemsForEasyAccess.getOrNull(currentPosition)
            ?.copy(blurDegree = degree) ?: throw IllegalStateException()
        _gameItems.value = gameItemsForEasyAccess.map { originalItem ->
            if (originalItem.id == changedGameItem.id) changedGameItem else originalItem
        }
    }

    fun checkUserInputMatchedWithAnswer() {
    }
}
