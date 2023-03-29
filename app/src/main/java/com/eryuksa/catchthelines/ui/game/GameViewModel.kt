package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.game.uistate.ContentItem
import com.eryuksa.catchthelines.ui.game.uistate.GameMode
import com.eryuksa.catchthelines.ui.game.uistate.GameUiState
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val hintCountRepository: HintCountRepository
) : ViewModel() {

    private val _currentPage = MutableStateFlow<Int>(0)
    private val _contentItems = MutableStateFlow<List<ContentItem>>(emptyList())
    private val _selectedLine = MutableStateFlow<Int>(0)
    private val _usedHints = MutableStateFlow<Set<Hint>>(emptySet())
    private val _resultText = MutableStateFlow<String>("")
    private val _gameMode = MutableStateFlow<GameMode>(GameMode.WATCHING)
    private val _availableHintCount = MutableStateFlow<Int>(10)

    private val _isHintOpen = MutableStateFlow<Boolean>(false)
    val isHintOpen: StateFlow<Boolean> get() = _isHintOpen

    private val _hideKeyboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val hideKeyboard: SharedFlow<Unit> get() = _hideKeyboard

    private val _showGameStateResetMessage = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val showGameStateResetMessage: SharedFlow<Unit> get() = _showGameStateResetMessage

    val userInputTitle = MutableStateFlow<String>("")

    var audioPlaybackPosition = 0L
    var audioWasBeingPlayed = false

    val uiState: StateFlow<GameUiState> = combine(
        _currentPage,
        _contentItems,
        _selectedLine,
        _usedHints,
        _resultText,
        _availableHintCount,
        _gameMode
    ) { array: Array<Any> ->
        val currentPage = array[0] as Int
        val contentItems = array[1] as List<ContentItem>
        val selectedLine = array[2] as Int
        val currentContentItem: ContentItem? = contentItems.getOrNull(currentPage)

        GameUiState(
            currentPage = currentPage,
            contentItems = contentItems,
            audioIndex = 2 * currentPage + selectedLine,
            usedHints = array[3] as Set<Hint>,
            firstCharacterHint = currentContentItem?.title?.first().toString(),
            characterCountHint = currentContentItem?.title?.length,
            resultText = array[4] as String,
            hintCount = array[5] as Int,
            gameMode = array[6] as GameMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = GameUiState()
    )

    init {
        viewModelScope.launch {
            contentRepository.getContents().also { contents ->
                _contentItems.value = contents.map { ContentItem.from(it) }
            }
            hintCountRepository.availableHintCount.collectLatest { _availableHintCount.value = it }
        }
    }

    fun changeHintOpenState() {
        _isHintOpen.update { !it }
    }

    fun changeSelectedLine(selectedLine: Int) {
        _selectedLine.value = selectedLine
    }

    fun tryToMovePageTo(position: Int) {
        if (_gameMode.value == GameMode.IN_GAME) {
            _showGameStateResetMessage.tryEmit(Unit)
        } else {
            _currentPage.value = position
        }
    }

    fun forceToMovePageTo(position: Int) {
        setWatchingMode()
        _currentPage.update { position }
    }

    fun useHint(hint: Hint) {
        if (_availableHintCount.value == 0) {
            return
        }
        viewModelScope.launch { hintCountRepository.decreaseHintCount() }
        changeHintOpenState()

        if (_gameMode.value == GameMode.WATCHING) {
            setInGameMode()
        }

        when (hint) {
            Hint.CLEARER_POSTER -> useClearerPosterHint()
            Hint.FIRST_CHARACTER -> Unit
            Hint.CHARACTER_COUNT -> Unit
        }
        _usedHints.update { usedHints ->
            usedHints.toMutableSet().apply { add(hint) }
        }
    }

    fun checkUserCatchTheLine() {
        if (userInputTitle.value.isBlank()) return
        if (_gameMode.value == GameMode.WATCHING) {
            setInGameMode()
        }

        val contentItem = _contentItems.value[_currentPage.value]
        if (doesUserCatch(contentItem.title)) {
            viewModelScope.launch {
                contentRepository.saveCaughtContent(contentItem.id)
            }
            setCatchMode(contentItem)
        } else {
            _resultText.update { userInputTitle.value }
        }

        _hideKeyboard.tryEmit(Unit)
        userInputTitle.update { "" }
    }

    fun removeCaughtContent() {
        setWatchingMode()
        removeCurrentContent()
    }

    fun saveAudioState(playPosition: Long, wasBeingPlayed: Boolean) {
        this.audioPlaybackPosition = playPosition
        this.audioWasBeingPlayed = wasBeingPlayed
    }

    private fun useClearerPosterHint() {
        _contentItems.update { contentItems ->
            val clearerPosterContent = contentItems[_currentPage.value].toClearerPosterContent()
            contentItems.replaceOldItemAt(i = _currentPage.value, newItem = clearerPosterContent)
        }
    }

    private fun setInGameMode() {
        viewModelScope.launch {
            _gameMode.update { GameMode.IN_GAME }
            contentRepository.saveTriedContent(_contentItems.value[_currentPage.value].id)
        }
    }

    private fun setCatchMode(currentContentItem: ContentItem) {
        _gameMode.update { GameMode.CATCH }
        _resultText.update { currentContentItem.title }
        _contentItems.update { contentItems ->
            val noBlurPosterContent = currentContentItem.toNoBlurPosterContent()
            contentItems.replaceOldItemAt(i = _currentPage.value, newItem = noBlurPosterContent)
        }
    }

    private fun setWatchingMode() {
        _gameMode.update { GameMode.WATCHING }
        _resultText.update { "" }
        _usedHints.update { emptySet() }
    }

    private fun removeCurrentContent() {
        val currentPage = _currentPage.value
        if (currentPage == 0 && _contentItems.value.size == 1) {
            _contentItems.update { emptyList() }
            _gameMode.update { GameMode.ALL_CATCH }
        } else if (currentPage == _contentItems.value.lastIndex) {
            _currentPage.update { it - 1 }
            _contentItems.update { it.subList(0, it.lastIndex) }
        } else {
            _contentItems.update {
                it.subList(0, currentPage) + it.subList(currentPage + 1, it.size)
            }
        }
    }

    private fun doesUserCatch(contentTitle: String): Boolean =
        userInputTitle.value.contains(contentTitle)

    private fun <T> List<T>.replaceOldItemAt(i: Int, newItem: T): List<T> =
        this.toMutableList().also { newList ->
            newList[i] = newItem
        }
}
