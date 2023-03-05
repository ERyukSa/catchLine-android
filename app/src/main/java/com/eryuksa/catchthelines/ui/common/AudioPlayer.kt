package com.eryuksa.catchthelines.ui.common

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView

class AudioPlayer(private val audioPlayer: ExoPlayer) {

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private lateinit var onPlay: () -> Unit
    private lateinit var onPause: () -> Unit

    private val audioPlayerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            audioPlayer.pause()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            when (isPlaying) {
                true -> onPlay()
                false -> onPause()
            }
        }
    }

    fun initializePlayer(
        uris: List<String>,
        playerView: PlayerControlView,
        onPlay: () -> Unit,
        onPause: () -> Unit
    ) {
        playerView.player = audioPlayer.also { exoPlayer ->
            setUpAudio(uris)
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(currentItem, playbackPosition)
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            exoPlayer.addListener(audioPlayerListener)
            exoPlayer.prepare()
        }

        this.onPlay = onPlay
        this.onPause = onPause
    }

    fun releasePlayer() {
        playWhenReady = audioPlayer.playWhenReady
        currentItem = audioPlayer.currentMediaItemIndex
        playbackPosition = audioPlayer.currentPosition
        audioPlayer.removeListener(audioPlayerListener)
        audioPlayer.release()
    }

    fun setUpAudio(uris: List<String>) {
        audioPlayer.setMediaItems(uris.map { MediaItem.fromUri(it) })
    }

    fun switchPlayingState() {
        when (audioPlayer.isPlaying) {
            true -> audioPlayer.pause()
            false -> audioPlayer.play()
        }
    }

    fun moveTo(mediaItemIndex: Int) {
        audioPlayer.pause()
        audioPlayer.seekTo(mediaItemIndex, 0)
    }
}
