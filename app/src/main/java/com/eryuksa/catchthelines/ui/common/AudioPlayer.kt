package com.eryuksa.catchthelines.ui.common

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView

class AudioPlayer(private val audioPlayer: ExoPlayer) {

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private lateinit var callBackOnPlay: () -> Unit
    private lateinit var callBackOnPause: () -> Unit

    private val audioPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            when (isPlaying) {
                true -> callBackOnPlay()
                false -> callBackOnPause()
            }
        }
    }

    fun initializePlayer(
        playerView: PlayerControlView,
        onPlay: () -> Unit,
        onPause: () -> Unit
    ) {
        playerView.player = audioPlayer.also { exoPlayer ->
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(currentItem, playbackPosition)
            exoPlayer.pauseAtEndOfMediaItems = true
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            exoPlayer.addListener(audioPlayerListener)
            exoPlayer.prepare()
        }

        this.callBackOnPlay = onPlay
        this.callBackOnPause = onPause
    }

    fun releasePlayer() {
        playWhenReady = audioPlayer.playWhenReady
        currentItem = audioPlayer.currentMediaItemIndex
        playbackPosition = audioPlayer.currentPosition
        audioPlayer.removeListener(audioPlayerListener)
        audioPlayer.release()
    }

    fun setUpAudio(uris: List<List<String>>) {
        audioPlayer.setMediaItems(
            uris.flatten().map { uri -> MediaItem.fromUri(uri) }
        )
    }

    fun switchPlayingState() {
        when (audioPlayer.isPlaying) {
            true -> audioPlayer.pause()

            false -> {
                if (audioPlayer.currentPosition == audioPlayer.duration) {
                    audioPlayer.seekTo(0)
                }
                audioPlayer.play()
            }
        }
    }

    fun moveTo(mediaItemIndex: Int) {
        audioPlayer.pause()
        audioPlayer.seekTo(mediaItemIndex, 0)
    }
}
