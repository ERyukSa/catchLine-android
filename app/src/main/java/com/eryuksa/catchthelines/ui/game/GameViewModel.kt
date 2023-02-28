package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.game.uistate.AllKilled
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.FeedbackUiState
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
import com.eryuksa.catchthelines.ui.game.uistate.GameUiState
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import com.eryuksa.catchthelines.ui.game.uistate.NoHint
import com.eryuksa.catchthelines.ui.game.uistate.NoInput
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import com.eryuksa.catchthelines.ui.game.uistate.UserInputWrong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(
    private val contentRepository: ContentRepository,
    private val hintRepository: HintCountRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _currentPagePosition = MutableLiveData(0)
    val currentPagePosition: LiveData<Int>
        get() = _currentPagePosition

    private val _uiStates = MutableLiveData<List<GameUiState>>()
    val uiStates: LiveData<List<GameUiState>>
        get() = _uiStates
    private val uiStatesForEasyAccess: List<GameUiState>
        get() = _uiStates.value ?: emptyList()

    val groupedLineAudioUrls: LiveData<List<List<String>>> = run {
        Transformations.map(_uiStates) { uiStates ->
            uiStates.map { it.mediaContent.lineAudioUrls }
        }.also { audioUrls ->
            Transformations.distinctUntilChanged(audioUrls)
        }
    }

    private val _feedbackUiState = MediatorLiveData<FeedbackUiState>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _uiStates.value ?: return@addSource
            value = gameItems.findFeedbackUiStateAt(position)
        }
        addSource(_uiStates) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findFeedbackUiStateAt(position)
        }
    }
    val feedbackUiState: LiveData<FeedbackUiState> =
        Transformations.distinctUntilChanged(_feedbackUiState)

    private val _usedHints = MediatorLiveData<Set<Hint>>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _uiStates.value ?: return@addSource
            value = gameItems.findUsedHintStateAt(position)
        }
        addSource(_uiStates) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findUsedHintStateAt(position)
        }
    }
    val usedHints: LiveData<Set<Hint>> = Transformations.distinctUntilChanged(_usedHints)

    private val _hintText = MediatorLiveData<String>().apply {
        addSource(_currentPagePosition) { position ->
            val gameItems = _uiStates.value ?: return@addSource
            value = gameItems.findHintTextAt(position)
        }
        addSource(_uiStates) { items ->
            val position = _currentPagePosition.value ?: return@addSource
            value = items.findHintTextAt(position)
        }
    }
    val hintText: LiveData<String> = Transformations.distinctUntilChanged(_hintText)

    private val _availableHintCount = MutableLiveData<Int>()
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    init {
        viewModelScope.launch {
            _uiStates.value = contentRepository.getMediaContents().map(::mapToGameItem)
            hintRepository.availableHintCount.collectLatest { _availableHintCount.value = it }
        }
    }

    fun movePagePosition(position: Int) {
        _currentPagePosition.value = position
    }

    fun useHint(hint: Hint) {
        val currentPosition = currentPagePosition.value ?: return
        viewModelScope.launch { hintRepository.decreaseHintCount() }

        val changedGameItem = when (hint) {
            is ClearerPosterHint -> with(uiStatesForEasyAccess[currentPosition]) {
                copy(usedHints = this.usedHints.toMutableSet().apply { add(hint) })
            }
            is FirstCharacterHint -> with(uiStatesForEasyAccess[currentPosition]) {
                copy(
                    usedHints = this.usedHints.toMutableSet().apply { add(hint) },
                    hintText = stringProvider.getString(
                        FirstCharacterHint.stringResId,
                        uiStatesForEasyAccess[currentPosition].mediaContent.title.first()
                    )
                )
            }
            is CharacterCountHint -> with(uiStatesForEasyAccess[currentPosition]) {
                copy(
                    usedHints = this.usedHints.toMutableSet().apply { add(hint) },
                    hintText = stringProvider.getString(
                        CharacterCountHint.stringResId,
                        uiStatesForEasyAccess[currentPosition].mediaContent.title.length
                    )
                )
            }
            else -> uiStatesForEasyAccess[currentPosition]
        }
        _uiStates.value = uiStatesForEasyAccess.replaceOldItem(changedGameItem)
    }

    fun checkUserCatchTheLine(userInput: String) {
        val uiState = uiStatesForEasyAccess[currentPagePosition.value ?: return]
        val changedGameItem = if (userInput.contains(uiState.mediaContent.title)) {
            viewModelScope.launch {
                contentRepository.saveCaughtContent(uiState.mediaContent)
            }
            uiState.copy(feedbackUiState = UserCaughtTheLine(uiState.mediaContent.title))
        } else {
            uiState.copy(feedbackUiState = UserInputWrong(userInput))
        }
        _uiStates.value = uiStatesForEasyAccess.replaceOldItem(changedGameItem)
    }

    fun removeCaughtContent() {
        val currentPage = currentPagePosition.value ?: throw IllegalStateException()
        if (currentPage == 0 && uiStatesForEasyAccess.size == 1) {
            _uiStates.value = emptyList()
            _feedbackUiState.value = AllKilled
        } else if (currentPage == uiStatesForEasyAccess.lastIndex) {
            _currentPagePosition.value = currentPage - 1
            _uiStates.value = uiStatesForEasyAccess.subList(0, uiStatesForEasyAccess.size - 1)
        } else {
            _uiStates.value = uiStatesForEasyAccess.subList(0, currentPage) +
                uiStatesForEasyAccess.subList(currentPage + 1, uiStatesForEasyAccess.size)
        }
    }

    private fun mapToGameItem(mediaContent: Content): GameUiState =
        GameUiState(
            mediaContent = mediaContent,
            feedbackUiState = NoInput,
            usedHints = emptySet(),
            hintText = stringProvider.getString(NoHint.stringResId)
        )
}

private fun List<GameUiState>.replaceOldItem(newUiState: GameUiState): List<GameUiState> =
    this.map { uiState ->
        if (uiState.mediaContent.id == newUiState.mediaContent.id) newUiState else uiState
    }

private fun List<GameUiState>.findFeedbackUiStateAt(index: Int): FeedbackUiState {
    if (this.isEmpty()) return NoInput
    return this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.feedbackUiState }
        .toList()[0]
}

private fun List<GameUiState>.findUsedHintStateAt(index: Int): Set<Hint> {
    if (this.isEmpty()) return emptySet()
    return this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.usedHints }
        .toList()[0]
}

private fun List<GameUiState>.findHintTextAt(index: Int): String {
    if (this.isEmpty()) return ""
    return this.asSequence()
        .filterIndexed { i, _ -> i == index }
        .map { it.hintText }
        .toList()[0]
}
