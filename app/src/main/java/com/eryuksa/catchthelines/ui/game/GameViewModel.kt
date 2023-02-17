package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.MediaContent
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.model.GameItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    var currentPagePosition = 0

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess: List<GameItem>
        get() = _gameItems.value ?: emptyList()

    private val _feedbackText = MutableLiveData<String>()
    val feedbackText: LiveData<String>
        get() = _feedbackText

    private val _availableHintCount = MutableLiveData<Int>(10)
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getMediaContents().map(MediaContent::toGameItem)
            repository.getAvailableHintCount().collectLatest { _availableHintCount.value = it }
        }
    }

    fun useHintClearerPoster() {
        availableHintCount.value?.let { hintCount ->
            if (hintCount <= 0) return

            viewModelScope.launch { repository.decreaseHintCount() }
            val changedGameItem =
                gameItemsForEasyAccess[currentPagePosition].copy(blurDegree = CLEARER_BLUR_DEGREE)
            _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
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
