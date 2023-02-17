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

    var currentPagePosition = 0

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess: List<GameItem>
        get() = _gameItems.value ?: emptyList()

    private val _hintText = MutableLiveData<String>()
    val hintText: LiveData<String>
        get() = _hintText

    private val _feedbackText = MutableLiveData<String>()
    val feedbackText: LiveData<String>
        get() = _feedbackText

    private val _availableHintCount = MutableLiveData<Int>(10)
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getGameItems().map(Contents::toGameItem)
        }
    }

    fun useHintClearerPoster() {
        availableHintCount.value?.let { hintCount ->
            if (hintCount <= 0) return

            val changedGameItem =
                gameItemsForEasyAccess[currentPagePosition].copy(blurDegree = CLEARER_BLUR_DEGREE)
            _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
            _availableHintCount.value = hintCount - 1
        }
    }

    private fun List<GameItem>.replaceOldItem(newItem: GameItem): List<GameItem> =
        this.map { oldItem -> if (oldItem.id == newItem.id) newItem else oldItem }

    fun checkUserInputMatchedWithAnswer() {
    }

    companion object {
        private const val CLEARER_BLUR_DEGREE = 2
    }
}
