package com.eryuksa.catchthelines.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eryuksa.catchthelines.data.dto.GameItem
import com.eryuksa.catchthelines.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _gameItems = MutableLiveData<List<GameItem>>()
    val gameItems: LiveData<List<GameItem>>
        get() = _gameItems

    private val _hintText = MutableLiveData<String>()
    val hintText: LiveData<String>
        get() = _hintText

    private val _feedbackText = MutableLiveData<String>()
    val feedbackText: LiveData<String>
        get() = _feedbackText

    private val _availableHintCount = MutableLiveData<Int>()
    val availableHintCount: LiveData<Int>
        get() = _availableHintCount

    private val _isLinePlaying = MutableLiveData<Boolean>(false)
    val isLinePlaying: LiveData<Boolean>
        get() = _isLinePlaying

    init {
        viewModelScope.launch {
            _gameItems.value = repository.getGameItems()
        }
    }

    fun checkUserTitleIsMatched() {
    }
}
