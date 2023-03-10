package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.game.uistate.AnotherLineHint
import com.eryuksa.catchthelines.ui.game.uistate.CharacterCountHint
import com.eryuksa.catchthelines.ui.game.uistate.ClearerPosterHint
import com.eryuksa.catchthelines.ui.game.uistate.ContentUiState
import com.eryuksa.catchthelines.ui.game.uistate.FirstCharacterHint
import com.eryuksa.catchthelines.ui.game.uistate.GameUiState
import com.eryuksa.catchthelines.ui.game.uistate.Hint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val contentRepository: ContentRepository,
    private val hintRepository: HintCountRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _currentPage = MutableStateFlow<Int>(0)
    private val _contentUiStates = MutableStateFlow<List<ContentUiState>>(emptyList())
    private var _usedHints = MutableStateFlow<List<Set<Hint>>>(listOf(emptySet()))
    private var _hintTexts = MutableStateFlow<List<String>>(
        listOf(stringProvider.getString(R.string.game_listen_and_guess))
    )
    private var _feedbackTexts = MutableStateFlow<List<String>>(listOf(""))
    private val _didUserCatchTheLine = MutableStateFlow<Boolean>(false)
    private val _availableHintCount = MutableStateFlow<Int>(10)
    private val _audioIndex = MutableStateFlow<Int>(0)

    val uiState: StateFlow<GameUiState> = combine(
        _currentPage,
        _contentUiStates,
        _usedHints,
        _hintTexts,
        _feedbackTexts,
        _didUserCatchTheLine,
        _availableHintCount,
        _audioIndex
    ) { array: Array<Any> ->
        val currentPage = array[0] as Int
        GameUiState(
            currentPage = currentPage,
            contentUiStates = array[1] as List<ContentUiState>,
            usedHints = (array[2] as List<Set<Hint>>)[currentPage],
            hintText = (array[3] as List<String>)[currentPage],
            feedbackText = (array[4] as List<String>)[currentPage],
            didUserCatchTheLine = array[5] as Boolean,
            hintCount = array[6] as Int,
            audioIndex = array[7] as Int
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = GameUiState()
    )

    init {
        viewModelScope.launch {
            contentRepository.getContents().also { contents ->
                _contentUiStates.value = contents.map { eachContent ->
                    ContentUiState(content = eachContent, blurDegree = DEFAULT_BLUR_DEGREE)
                }
                _usedHints.value = contents.map { emptySet() }
                _hintTexts.value = contents.map { stringProvider.getString(R.string.game_listen_and_guess) }
                _feedbackTexts.value = contents.map { "" }

                contentRepository.saveEncounteredContent(contents.first())
            }
            hintRepository.availableHintCount.collectLatest { _availableHintCount.value = it }
        }
    }

    fun movePageTo(position: Int) {
        _currentPage.value = position
        _audioIndex.value = 2 * position
        viewModelScope.launch {
            contentRepository.saveEncounteredContent(_contentUiStates.value[position].content)
        }
    }

    fun useHint(hint: Hint) {
        val currentPage = _currentPage.value
        val currentUsedHints = uiState.value.usedHints

        if (hint !in currentUsedHints) {
            viewModelScope.launch { hintRepository.decreaseHintCount() }
        }

        val updatedUsedHints = if (hint !in currentUsedHints) {
            currentUsedHints.toMutableSet().also { it.add(hint) }
        } else {
            currentUsedHints
        }
        val updatedFeedbackText = when (hint) {
            is FirstCharacterHint -> stringProvider.getString(
                FirstCharacterHint.stringResId,
                _contentUiStates.value[_currentPage.value].content.title.first()
            )

            is CharacterCountHint -> stringProvider.getString(
                CharacterCountHint.stringResId,
                _contentUiStates.value[_currentPage.value].content.title.length
            )

            else -> uiState.value.feedbackText
        }
        val updatedBlurDegree = if (hint is ClearerPosterHint && hint !in currentUsedHints) {
            CLEARER_BLUR_DEGREE
        } else {
            _contentUiStates.value[currentPage].blurDegree
        }

        _usedHints.value = _usedHints.value.replaceOldItemAt(currentPage, updatedUsedHints)
        _feedbackTexts.value = _feedbackTexts.value.replaceOldItemAt(currentPage, updatedFeedbackText)
        _contentUiStates.value = _contentUiStates.value.replaceOldItemAt(
            i = currentPage,
            newItem = _contentUiStates.value[currentPage].copy(blurDegree = updatedBlurDegree)
        )
        if (hint is AnotherLineHint) {
            _audioIndex.value += 1
        }
    }

    fun checkUserCatchTheLine(userInput: String) {
        val currentContent = _contentUiStates.value[_currentPage.value].content
        if (doesUserCatch(userInput, currentContent.title)) {
            viewModelScope.launch {
                contentRepository.saveCaughtContent(currentContent)
            }
            _didUserCatchTheLine.update { true }
            _feedbackTexts.update { feedbackTexts ->
                feedbackTexts.replaceOldItemAt(
                    i = _currentPage.value,
                    newItem =
                    stringProvider.getString(R.string.game_feedback_catch_the_line, currentContent.title)
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
            _usedHints.value = listOf(emptySet())
            _hintTexts.value = listOf("")
            _feedbackTexts.value = listOf(stringProvider.getString(R.string.game_feedback_all_killed))
        } else if (_currentPage.value == _contentUiStates.value.lastIndex) {
            _currentPage.update { it - 1 }
            _contentUiStates.update { it.subList(0, it.lastIndex) }
            _usedHints.update { it.subList(0, it.lastIndex) }
            _hintTexts.update { it.subList(0, it.lastIndex) }
            _feedbackTexts.update { it.subList(0, it.lastIndex) }
        } else {
            val currentPage = _currentPage.value
            _contentUiStates.update { contentUiStates ->
                contentUiStates.subList(0, currentPage) +
                    contentUiStates.subList(currentPage + 1, contentUiStates.size)
            }
            _usedHints.update { usedHintsList ->
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

    fun switchLineOfCurrentContent() {
        if (_audioIndex.value % 2 == 0) {
            if (_usedHints.value[_currentPage.value].contains(AnotherLineHint)) {
                _audioIndex.value += 1
            }
        } else {
            _audioIndex.value -= 1
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
