package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchline_android.R
import com.eryuksa.catchthelines.data.dto.GameItem
import com.eryuksa.catchthelines.data.dto.MediaContent
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.uistate.FeedbackUiState
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _currentPagePosition = MutableLiveData<Int>(0)
    val currentPagePosition: LiveData<Int>
        get() = _currentPagePosition

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess: List<GameItem>
        get() = _gameItems.value ?: emptyList()

    private val _feedbackUiState = MutableLiveData<FeedbackUiState>()
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

    private fun List<GameItem>.replaceOldItem(newItem: GameItem): List<GameItem> =
        this.map { oldItem -> if (oldItem.id == newItem.id) newItem else oldItem }

    fun checkUserCatchTheLine(userInput: String) {
        val gameItem = gameItemsForEasyAccess[currentPagePosition.value ?: return]
        _feedbackUiState.value = if (userInput.contains(gameItem.title)) {
            UserCaughtTheLine(R.string.game_feedback_catch_the_line, gameItem.title)
        } else {
            UserInputWrong(R.string.game_feedback_wrong, userInput)
        }
    }

    companion object {
        private const val CLEARER_BLUR_DEGREE = 2
    }
}
