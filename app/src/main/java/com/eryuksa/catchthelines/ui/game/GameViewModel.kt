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

    private var gameItemList = emptyList<GameItem>()
    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems

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
            gameItemList = repository.getGameItems().map(Contents::toGameItem)
            _gameItems.value = gameItemList
        }
    }

    fun changePage(position: Int) {
        _currentPagePosition.value = position
    }

    fun changeCurrentPosterBlurDegree(degree: Int) {
        val currentPosition = _currentPagePosition.value ?: throw IllegalStateException()
        val changedGameItem = gameItemList.getOrNull(currentPosition)?.copy(blurDegree = degree) ?: throw IllegalStateException()
        gameItemList = gameItemList.map { item ->
            if (item.id == changedGameItem.id) changedGameItem else item
        }
        _gameItems.value = gameItemList
    }

    fun checkUserInputMatchedWithAnswer() {
    }
}
