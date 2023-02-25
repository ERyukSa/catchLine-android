package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.MediaContent
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.FeedbackUiState
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
import com.eryuksa.catchthelines.ui.game.uistate.GameItem
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import com.eryuksa.catchthelines.ui.game.uistate.NoHint
import com.eryuksa.catchthelines.ui.game.uistate.NoInput
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: ContentRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _currentPagePosition = MutableLiveData(0)
    val currentPagePosition: LiveData<Int>
        get() = _currentPagePosition

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems
    private val gameItemsForEasyAccess: List<GameItem>
        get() = _gameItems.value ?: emptyList()

    val lineAudioUrls: LiveData<List<List<String>>> = run {
        Transformations.map(_gameItems) { items ->
            items.map { it.lineAudioUrls }
        }.also { audioUrls ->
            Transformations.distinctUntilChanged(audioUrls)
        }
    }

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
    val feedbackUiState: LiveData<FeedbackUiState> = Transformations.distinctUntilChanged(_feedbackUiState)

    private val _usedHintState = MediatorLiveData<Set<Hint>>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _gameItems.value ?: return@addSource
            value = gameItems.findUsedHintStateAt(position)
        }
        addSource(_gameItems) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findUsedHintStateAt(position)
        }
    }
    val usedHintState: LiveData<Set<Hint>> = Transformations.distinctUntilChanged(_usedHintState)

    private val _hintText = MediatorLiveData<String>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _gameItems.value ?: return@addSource
            value = gameItems.findHintTextAt(position)
        }
        addSource(_gameItems) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findHintTextAt(position)
        }
    }
    val hintText: LiveData<String> = Transformations.distinctUntilChanged(_hintText)

    private val _availableHintCount = MutableLiveData<Int>(10)
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getMediaContents().map(::mapToGameItem)
            repository.getAvailableHintCount().collectLatest { _availableHintCount.value = it }
        }
    }

    fun movePagePosition(position: Int) {
        _currentPagePosition.value = position
    }

    fun useHint(hint: Hint) {
        val currentPosition = currentPagePosition.value ?: return
        val changedGameItem = when (hint) {
            is ClearerPosterHint -> with(gameItemsForEasyAccess[currentPosition]) {
                copy(usedHints = this.usedHints.toMutableSet().apply { add(hint) })
            }
            is FirstCharacterHint -> with(gameItemsForEasyAccess[currentPosition]) {
                copy(
                    usedHints = this.usedHints.toMutableSet().apply { add(hint) },
                    hintText = stringProvider.getString(
                        FirstCharacterHint.stringResId,
                        gameItemsForEasyAccess[currentPosition].title.first()
                    )
                )
            }
            is CharacterCountHint -> with(gameItemsForEasyAccess[currentPosition]) {
                copy(
                    usedHints = this.usedHints.toMutableSet().apply { add(hint) },
                    hintText = stringProvider.getString(
                        CharacterCountHint.stringResId,
                        gameItemsForEasyAccess[currentPosition].title.length
                    )
                )
            }
            else -> gameItemsForEasyAccess[currentPosition]
        }
        _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
    }

    fun checkUserCatchTheLine(userInput: String) {
        val gameItem = gameItemsForEasyAccess[currentPagePosition.value ?: return]
        val changedGameItem = if (userInput.contains(gameItem.title)) {
            gameItem.copy(feedbackUiState = UserCaughtTheLine(gameItem.title))
        } else {
            gameItem.copy(feedbackUiState = UserInputWrong(userInput))
        }
        _gameItems.value = gameItemsForEasyAccess.replaceOldItem(changedGameItem)
    }

    fun removeCaughtContent() {
        val gameItem = gameItemsForEasyAccess[currentPagePosition.value ?: return]
        _gameItems.value = gameItemsForEasyAccess.filterNot { it.id == gameItem.id }
    }

    private fun mapToGameItem(mediaContent: MediaContent): GameItem =
        GameItem(
            mediaContent.id,
            mediaContent.title,
            mediaContent.posterUrl,
            mediaContent.lineAudioUrls,
            feedbackUiState = NoInput,
            usedHints = emptySet(),
            hintText = stringProvider.getString(NoHint.stringResId)
        )
}

private fun List<GameItem>.replaceOldItem(newItem: GameItem): List<GameItem> =
    this.map { oldItem -> if (oldItem.id == newItem.id) newItem else oldItem }

private fun List<GameItem>.findFeedbackUiStateAt(index: Int): FeedbackUiState =
    this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.feedbackUiState }
        .toList()[0]

private fun List<GameItem>.findUsedHintStateAt(index: Int): Set<Hint> =
    this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.usedHints }
        .toList()[0]

private fun List<GameItem>.findHintTextAt(index: Int): String =
    this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.hintText }
        .toList()[0]
