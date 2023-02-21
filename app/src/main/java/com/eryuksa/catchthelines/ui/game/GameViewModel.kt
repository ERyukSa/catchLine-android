package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.GameItem
import com.eryuksa.catchthelines.data.dto.MediaContent
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.uistate.FeedbackUiState
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _currentPagePosition = MutableLiveData(0)
    val currentPagePosition: LiveData<Int>
        get() = _currentPagePosition

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess: List<GameItem>
        get() = _gameItems.value ?: emptyList()

    private val _feedbackUiState = MediatorLiveData<FeedbackUiState>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _gameItems.value ?: return@addSource
            value = gameItems.findFeedbackUiStateAt(position)
        }
        addSource(_gameItems) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findFeedbackUiStateAt(position)
        }
    }
    val feedbackUiState: LiveData<FeedbackUiState>
        get() = _feedbackUiState

    private val _availableHintCount = MutableLiveData<Int>(10)
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getMediaContents().map(MediaContent::toGameItem)
            repository.getAvailableHintCount().collectLatest { _availableHintCount.value = it }
        }
    }

    fun movePagePosition(position: Int) {
        _currentPagePosition.value = position
    }

    fun useHintClearerPoster() {
        val currentPosition = currentPagePosition.value ?: return
        val changedGameItem =
            gameItemsForEasyAccess[currentPosition].copy(blurDegree = CLEARER_BLUR_DEGREE)
        _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
    }

    fun checkUserCatchTheLine(userInput: String) {
        val gameItem = gameItemsForEasyAccess[currentPagePosition.value ?: return]
        val changedGameItem = if (userInput.contains(gameItem.title)) {
            gameItem.copy(blurDegree = 0, feedbackUiState = UserCaughtTheLine(gameItem.title))
        } else {
            gameItem.copy(feedbackUiState = UserInputWrong(userInput))
        }
        _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
    }

    private fun List<GameItem>.replaceOldItem(newItem: GameItem): List<GameItem> =
        this.map { oldItem -> if (oldItem.id == newItem.id) newItem else oldItem }

    private fun List<GameItem>.findFeedbackUiStateAt(index: Int): FeedbackUiState =
        this.asSequence()
            .filterIndexed { i, _ -> i == index }
            .map { it.feedbackUiState }
            .toList()[0]

    companion object {
        private const val CLEARER_BLUR_DEGREE = 2
    }
}
