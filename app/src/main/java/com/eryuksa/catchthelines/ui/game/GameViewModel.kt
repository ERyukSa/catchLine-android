package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.game.uistate.ContentInfo
import com.eryuksa.catchthelines.ui.game.uistate.GameMode
import com.eryuksa.catchthelines.ui.game.uistate.GameUiState
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _contentInfo = MutableStateFlow<List<ContentInfo>>(emptyList())
    private val _selectedLine = MutableStateFlow<Int>(0)
    private val _usedHints = MutableStateFlow<Set<Hint>>(emptySet())
    private val _firstCharacterHintText = MutableStateFlow<String>("")
    private val _characterCountHint = MutableStateFlow<Int>(0)
    private val _resultText = MutableStateFlow<String>("")
    private val _gameMode = MutableStateFlow<GameMode>(GameMode.WATCHING)
    private val _availableHintCount = MutableStateFlow<Int>(10)

    private val _isHintOpen = MutableStateFlow<Boolean>(false)
    val isHintOpen: StateFlow<Boolean> get() = _isHintOpen

    val uiState: StateFlow<GameUiState> = combine(
        _currentPage,
        _contentInfo,
        _selectedLine,
        _usedHints,
        _firstCharacterHintText,
        _characterCountHint,
        _resultText,
        _availableHintCount,
        _gameMode
    ) { array: Array<Any> ->
        val currentPage = array[0] as Int
        val selectedLine = array[2] as Int
        GameUiState(
            currentPage = currentPage,
            contentItems = array[1] as List<ContentInfo>,
            audioIndex = 2 * currentPage + selectedLine,
            usedHints = array[3] as Set<Hint>,
            firstCharacterHint = array[4] as String,
            characterCountHint = if (array[5] == 0) null else array[5] as Int,
            resultText = array[6] as String,
            hintCount = array[7] as Int,
            gameMode = array[8] as GameMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = GameUiState()
    )

    init {
        viewModelScope.launch {
            contentRepository.getContents().also { contents ->
                _contentInfo.value = contents.map {
                    ContentInfo(
                        id = it.id,
                        title = it.title,
                        audioUrls = it.lineAudioUrls,
                        posterUrl = it.posterUrl,
                        blurDegree = DEFAULT_BLUR_DEGREE
                    )
                }
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

    fun movePageTo(position: Int) {
        _currentPage.value = position
    }

    fun useHint(hint: Hint) {
        if (_availableHintCount.value == 0) {
            return
        }
        viewModelScope.launch { hintCountRepository.decreaseHintCount() }

        if (_gameMode.value == GameMode.WATCHING) {
            setInGameMode()
        }

        when (hint) {
            Hint.CLEARER_POSTER -> useClearerPosterHint()
            Hint.FIRST_CHARACTER -> useFirstCharacterHint()
            Hint.CHARACTER_COUNT -> useCharacterCountHint()
        }
        _usedHints.update { usedHints ->
            usedHints.toMutableSet().apply { add(hint) }
        }
    }

    private fun useClearerPosterHint() {
        /*_contentInfo.update { contentUiStates ->
            contentUiStates.replaceOldItemAt(
                i = _currentPage.value,
                newItem = contentUiStates[_currentPage.value].copy(blurDegree = CLEARER_BLUR_DEGREE)
            )
        }*/
    }

    private fun useFirstCharacterHint() {
        _firstCharacterHintText.update {
            _contentInfo.value[_currentPage.value].title.first().toString()
        }
    }

    private fun useCharacterCountHint() {
        _characterCountHint.update {
            _contentInfo.value[_currentPage.value].title.length
        }
    }

    fun checkUserCatchTheLine(userInput: String) {
        if (_gameMode.value == GameMode.WATCHING) {
            setInGameMode()
        }

        val contentInfo = _contentInfo.value[_currentPage.value]
        if (doesUserCatch(userInput, contentInfo.title)) {
            viewModelScope.launch {
                contentRepository.saveCaughtContent(contentInfo.id)
            }

            _gameMode.update { GameMode.CATCH }
            _resultText.update { contentInfo.title }
        } else {
            _resultText.update { userInput }
        }
    }

    private fun setInGameMode() {
        viewModelScope.launch {
            _gameMode.update { GameMode.IN_GAME }
            contentRepository.saveTriedContent(_contentInfo.value[_currentPage.value].id)
        }
    }

    fun removeCaughtContent() {
        /*_didUserCatchTheLine.value = false

        if (_currentPage.value == 0 && _contentInfo.value.size == 1) {
            _contentInfo.value = emptyList()
            _groupedUsedHints.value = listOf(emptySet())
            _hintTexts.value = listOf("")
            _resultText.value = listOf(stringProvider.getString(R.string.game_feedback_all_killed))
        } else if (_currentPage.value == _contentInfo.value.lastIndex) {
            _currentPage.update { it - 1 }
            _contentInfo.update { it.subList(0, it.lastIndex) }
            _groupedUsedHints.update { it.subList(0, it.lastIndex) }
            _hintTexts.update { it.subList(0, it.lastIndex) }
            _resultText.update { it.subList(0, it.lastIndex) }
        } else {
            val currentPage = _currentPage.value
            _contentInfo.update { contentUiStates ->
                contentUiStates.subList(0, currentPage) +
                    contentUiStates.subList(currentPage + 1, contentUiStates.size)
            }
            _groupedUsedHints.update { usedHintsList ->
                usedHintsList.subList(0, currentPage) +
                    usedHintsList.subList(currentPage + 1, usedHintsList.size)
            }
            _hintTexts.update { hintText2 ->
                hintText2.subList(0, currentPage) +
                    hintText2.subList(currentPage + 1, hintText2.size)
            }
            _resultText.update { feedbackTexts ->
                feedbackTexts.subList(0, currentPage) +
                    feedbackTexts.subList(currentPage + 1, feedbackTexts.size)
            }
        }*/
    }

    private fun doesUserCatch(userInput: String, contentTitle: String): Boolean =
        userInput.contains(contentTitle)

    companion object {
        private const val CLEARER_BLUR_DEGREE = 3
        private const val DEFAULT_BLUR_DEGREE = 6
    }
}

private fun <T> List<T>.replaceOldItemAt(i: Int, newItem: T): List<T> =
    this.toMutableList().also { newList ->
        newList[i] = newItem
    }
