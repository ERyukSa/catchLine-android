package com.eryuksa.catchthelines.ui.game

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eryuksa.catchthelines.model.Content
import com.eryuksa.catchthelines.repository.GameRepository

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _challengeNumber = MutableLiveData<Int>()
    val challengeNumber: LiveData<Int>
        get() = _challengeNumber
    private val _caughtNumber = MutableLiveData(0)
    val caughtNumber: LiveData<Int>
        get() = _caughtNumber

    // 현재 보여주고 있는 컨텐츠
    private val _currentContent = MutableLiveData<Content>()
    val currentContent: LiveData<Content>
        get() = _currentContent

    // 0: 기본, 1: 힌트1(글자 수 공개), 2: 힌트2(첫 글자 공개), 3: 힌트3(썸네일 덜 흐릿하게), 4: 정답 캐치
    private val _gameState = MutableLiveData<Int>(0)
    val gameState: LiveData<Int> = _gameState

    private val _audioPlaying = MutableLiveData(false)
    val audioPlaying: LiveData<Boolean>
        get() = _audioPlaying

    private val contentList = mutableListOf<Content>()
    private var mediaPlayer: MediaPlayer? = null

    init {
        loadGameData()
        moveToNextContent()
    }

    // TODO 1. 전적 텍스트 뷰를 하나로 설정해놔서, 둘 중 하나를 변경할 때마다 전체 text가 변경된다.
    //  2. _challengeNumber를 2번 변경한다.
    /**
     * 전적 기록, 출제할 Content 리스트, 실행시킬 MediaPlayer를 로드합니다.
     */
    private fun loadGameData() {
        _challengeNumber.value = repository.getChallengeNumber()
        _challengeNumber.postValue(repository.getChallengeNumber())
        _caughtNumber.value = repository.getCaughtNumber()
        contentList.addAll(repository.getUncaughtContents().shuffled())
    }

    private fun moveToNextContent() {
        mediaPlayer?.release()

        // 다음 컨텐츠가 존재한다면
        contentList.removeLastOrNull()?.let { nextContent ->
            _currentContent.value = nextContent
            mediaPlayer = getMediaPlayer(nextContent)
            if (_gameState.value != 0) _gameState.value = 0
            // 처음 보는 문제인 경우
            if (!nextContent.challenged) doNewChallengedProcess(nextContent)
        }
    }

    /**
     * 새로 나온 문제가 처음 도전하는 문제일 경우에 필요한 처리를 한다.
     */
    private fun doNewChallengedProcess(content: Content) {
        _challengeNumber.value = _challengeNumber.value!! + 1 // 도전 카운트
        content.challenged = true // 도전 표시
    }

    private fun getMediaPlayer(content: Content): MediaPlayer {
        Log.d("로그", "$content")
        return repository.getMediaPlayer(content.audioName).apply {
            setOnCompletionListener { _audioPlaying.value = false } // 재생 완료되면 재생 상태를 변경한다
        }
    }

    fun audioBtnClicked() {
        mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                _audioPlaying.value = false
                mediaPlayer.pause()
            } else {
                _audioPlaying.value = true
                mediaPlayer.start()
            }
        }
    }

    fun hintOrNextBtnClicked() {
        with(_gameState) {
            if (value!! < 3) value = value!! + 1
            else if (value!! >= 4) moveToNextContent()
        }
    }

    /**
     * 유저가 입력한 값이 정답을 순서에 맞게 포함하고 있으면 정답으로 인정한다.
     */
    fun isAnswer(userInput: String): Boolean {
        // 정답 구분
        val answer = _currentContent.value?.title ?: return false

        return if (userInput.startsWith(answer)) {
            doAnswerProcess()
            true
        } else {
            false
        }
    }

    /**
     * 정답일 때 처리 ->
     * 1. 캐치 카운트 증가
     * 2. gameState = 4(정답)
     * -> (BindingAdapter) 힌트 텍스트에 정답 공개, 썸네일 공개, 힌트 버튼의 이미지 -> 다음 버튼으로 변경
     * ※ gameState - 0: 기본, 1: 힌트1(글자 수 공개), 2: 힌트2(첫 글자 공개), 3: 힌트3(썸네일 덜 흐릿하게), 4: 정답 캐치
     */
    private fun doAnswerProcess() {
        _gameState.value = 4 // gameState: 4 -> 정답
        _caughtNumber.value = _caughtNumber.value!! + 1
        _currentContent.value!!.caught = true
    }

    override fun onCleared() {
        mediaPlayer?.release()
        super.onCleared()
    }
}
