package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.game.uistate.ContentUiState
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
    private val hintCountHandler: HintCountRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _currentPage = MutableStateFlow<Int>(0)
    private val _contentUiStates = MutableStateFlow<List<ContentUiState>>(emptyList())
    private val _groupedUsedHints = MutableStateFlow<List<Set<Hint>>>(listOf(emptySet()))
    private val _hintTexts = MutableStateFlow<List<String>>(
        listOf(stringProvider.getString(R.string.game_listen_and_guess))
    )
    private val _feedbackTexts = MutableStateFlow<List<String>>(listOf(""))
    private val _didUserCatchTheLine = MutableStateFlow<Boolean>(false)
    private val _availableHintCount = MutableStateFlow<Int>(10)
    private val _lineNumbers = MutableStateFlow<List<Int>>(emptyList())
    private val _groupedAudioUrls = MutableStateFlow<List<List<String>>>(emptyList())

    private val _isHintOpen = MutableStateFlow<Boolean>(false)
    val isHintOpen: StateFlow<Boolean> get() = _isHintOpen

    val uiState: StateFlow<GameUiState> = combine(
        _currentPage,
        _contentUiStates,
        _hintTexts,
        _feedbackTexts,
        _didUserCatchTheLine,
        _availableHintCount,
        _lineNumbers,
        _groupedAudioUrls
    ) { array: Array<Any> ->
        val currentPage = array[0] as Int
        GameUiState(
            currentPage = currentPage,
            contentUiStates = array[1] as List<ContentUiState>,
            hintText = (array[2] as List<String>)[currentPage],
            feedbackText = (array[3] as List<String>)[currentPage],
            didUserCatchTheLine = array[4] as Boolean,
            hintCount = array[5] as Int,
            audioIndex = 0, // 2 * currentPage + (array[7] as List<Int>)[currentPage],
            groupedAudioUrls = array[7] as List<List<String>>
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = GameUiState()
    )

    init {
        viewModelScope.launch {
            contentRepository.getContents().also { contents ->
                _contentUiStates.value = contents.map {
                    ContentUiState(
                        id = it.id,
                        title = it.title,
                        posterUrl = it.posterUrl,
                        blurDegree = DEFAULT_BLUR_DEGREE
                    )
                }
                _lineNumbers.value = List(contents.size) { 0 }
                _groupedUsedHints.value = contents.map { emptySet() }
                _groupedAudioUrls.value = contents.map { it.lineAudioUrls }
                _hintTexts.value = contents.map { stringProvider.getString(R.string.game_listen_and_guess) }
                _feedbackTexts.value = contents.map { "" }
            }
            hintCountHandler.availableHintCount.collectLatest { _availableHintCount.value = it }
        }
    }

    fun changeHintOpenState() {
        _isHintOpen.update { !it }
    }

    fun movePageTo(position: Int) {
        _currentPage.value = position
        viewModelScope.launch {
            if (_didUserCatchTheLine.value == true) return@launch
            contentRepository.saveEncounteredContent(_contentUiStates.value[position].id)
        }
    }

    fun useHint(hint: Hint) {
        val currentPage = _currentPage.value
        val currentUsedHints = _groupedUsedHints.value[currentPage]
        if (hint !in currentUsedHints && _availableHintCount.value == 0) {
            return
        }

        when (hint) {
            is Hint.ClearerPoster -> useClearerPosterHint()
            is Hint.FirstCharacter -> useFirstCharacterHint()
            is Hint.CharacterCount -> useCharacterCountHint()
        }

        if (hint !in currentUsedHints) {
            viewModelScope.launch { hintCountHandler.decreaseHintCount() }
            _groupedUsedHints.update { groupedUsedHints ->
                val updatedUsedHints = currentUsedHints.toMutableSet().apply { add(hint) }
                groupedUsedHints.replaceOldItemAt(currentPage, updatedUsedHints)
            }
        }
    }

    private fun useClearerPosterHint() {
        _contentUiStates.update { contentUiStates ->
            contentUiStates.replaceOldItemAt(
                i = _currentPage.value,
                newItem = contentUiStates[_currentPage.value].copy(blurDegree = CLEARER_BLUR_DEGREE)
            )
        }
    }

    private fun useFirstCharacterHint() {
/*        val hintText = stringProvider.getString(
            FirstCharacterHint.stringResId,
            _contentUiStates.value[_currentPage.value].title.first()
        )
        _feedbackTexts.update { feedbackTexts ->
            feedbackTexts.replaceOldItemAt(_currentPage.value, hintText)
        }*/
    }

    private fun useCharacterCountHint() {
        /*val hintText = stringProvider.getString(
            CharacterCountHint.stringResId,
            _contentUiStates.value[_currentPage.value].title.length
        )
        _feedbackTexts.update { feedbackTexts ->
            feedbackTexts.replaceOldItemAt(_currentPage.value, hintText)
        }*/
    }

    fun switchLineOfCurrentContent() {
        val currentPage = _currentPage.value
        _lineNumbers.update { lineNumbers ->
            val lineNumberOfCurrentContent = lineNumbers[currentPage]
            lineNumbers.replaceOldItemAt(i = currentPage, newItem = 1 - lineNumberOfCurrentContent)
        }
    }

    fun checkUserCatchTheLine(userInput: String) {
        val currentContentUiState = _contentUiStates.value[_currentPage.value]
        if (doesUserCatch(userInput, currentContentUiState.title)) {
            viewModelScope.launch {
                contentRepository.saveCaughtContent(currentContentUiState.id)
            }
            _didUserCatchTheLine.update { true }
            _feedbackTexts.update { feedbackTexts ->
                feedbackTexts.replaceOldItemAt(
                    i = _currentPage.value,
                    newItem = stringProvider.getString(
                        R.string.game_feedback_catch_the_line,
                        currentContentUiState.title
                    )
                )
            }
            _contentUiStates.update { contentUiStates ->
                contentUiStates.replaceOldItemAt(
                    i = _currentPage.value,
                    newItem = _contentUiStates.value[_currentPage.value].copy(blurDegree = 0)
                )
            }
        } else {
            _feedbackTexts.update { feedbackTexts ->
                feedbackTexts.replaceOldItemAt(
                    i = _currentPage.value,
                    newItem = stringProvider.getString(R.string.game_feedback_wrong, userInput)
                )
            }
        }
    }

    fun removeCaughtContent() {
        _didUserCatchTheLine.value = false

        if (_currentPage.value == 0 && _contentUiStates.value.size == 1) {
            _contentUiStates.value = emptyList()
            _groupedUsedHints.value = listOf(emptySet())
            _hintTexts.value = listOf("")
            _feedbackTexts.value = listOf(stringProvider.getString(R.string.game_feedback_all_killed))
        } else if (_currentPage.value == _contentUiStates.value.lastIndex) {
            _currentPage.update { it - 1 }
            _contentUiStates.update { it.subList(0, it.lastIndex) }
            _groupedUsedHints.update { it.subList(0, it.lastIndex) }
            _hintTexts.update { it.subList(0, it.lastIndex) }
            _feedbackTexts.update { it.subList(0, it.lastIndex) }
        } else {
            val currentPage = _currentPage.value
            _contentUiStates.update { contentUiStates ->
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
            _feedbackTexts.update { feedbackTexts ->
                feedbackTexts.subList(0, currentPage) +
                    feedbackTexts.subList(currentPage + 1, feedbackTexts.size)
            }
        }
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
