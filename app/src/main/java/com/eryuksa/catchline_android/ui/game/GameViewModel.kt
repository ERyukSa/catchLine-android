package com.eryuksa.catchline_android.ui.game

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.eryuksa.catchline_android.model.Content
import com.eryuksa.catchline_android.repository.GameRepository

class GameViewModel(private val repository: GameRepository): ViewModel() {

    private val _challengeNumber = MutableLiveData<Int>() // 지금까지 도전한 문제 수
    val challengeNumber: LiveData<Int>
        get() = _challengeNumber
    private val _caughtNumber = MutableLiveData(0)    // 지금까지 캐치한 문제 수
    val caughtNumber: LiveData<Int>
        get() = _caughtNumber

    // 현재 보여주고 있는 컨텐츠
    private val _currentContent = MutableLiveData<Content>()
    val currentContent: LiveData<Content>
        get() = _currentContent

    // 0: 기본, 1: 힌트1(글자 수 공개), 2: 힌트2(첫 글자 공개), 3: 힌트3(썸네일 덜 흐릿하게), 4: 정답 캐치
    private val _gameState = MutableLiveData<Int>(0)
    val gameState: LiveData<Int>
        get() = _gameState

    private val _audioPlaying = MutableLiveData(false)
    val audioPlaying: LiveData<Boolean>
        get() = _audioPlaying

    private val contentList = mutableListOf<Content>() // 출제할 컨텐츠 리스트
    private var mediaPlayer: MediaPlayer? = null       // 대사 플레이어

    init {
        loadGameData()
    }

    // TODO 1. 전적 텍스트 뷰를 하나로 설정해놔서, ~~Number.value를 변경할 때마다 text 값이 변경된다.
    //  2. _challengeNumber를 2번 변경한다.
    private fun loadGameData() {

        _challengeNumber.value = repository.getChallengeNumber()
        _caughtNumber.value = repository.getCaughtNumber()
        contentList.addAll(repository.getUncaughtContents().shuffled())
        contentList.removeLastOrNull()?.let{ content ->
            // 지금 문제를 처음 만났으면 challengeNumber에 +1
            if (!content.challenged) {
                _challengeNumber.value = _challengeNumber.value!! + 1
                content.challenged = true
            }
            _currentContent.value = content
            mediaPlayer = getMediaPlayer(content) // 해당 컨텐츠의 오디오 플레이어 할당받기
        }
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
            else { // gameState >= 4 -> 문제를 맞힌 상태이므로 현재 버튼은 next 버튼이다
                showNextContent()
            }
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
        _caughtNumber.value = _caughtNumber.value!! + 1  // 캐치 카운트 증가
        _currentContent.value!!.caught = true            // 문제 캐치 상태로 변경

    }

    private fun showNextContent() {
        _currentContent.value = contentList.removeLastOrNull() // 다음 문제로 변경

        _currentContent.value?.let { changedContent ->
            _gameState.value = 0 // 화면을 캐치 모드로 변경
            mediaPlayer?.release()
            mediaPlayer = getMediaPlayer(changedContent) // 오디오 변경

            if (!changedContent.challenged) { // 처음 본 문제면 챌린지 카운트 증가
                _challengeNumber.value = _challengeNumber.value!! + 1
            }
        }
    }

    override fun onCleared() {
        mediaPlayer?.release()
        super.onCleared()
    }
}