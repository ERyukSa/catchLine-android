package com.eryuksa.catchthelines.ui.game.utility

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class AudioPlayerHandler(private val audioPlayer: ExoPlayer) {

    var playbackPosition = 0L
        private set

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
        playbackPosition: Long,
        onPlay: () -> Unit,
        onPause: () -> Unit
    ) {
        audioPlayer.also { exoPlayer ->
            exoPlayer.pauseAtEndOfMediaItems = true
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            exoPlayer.addListener(audioPlayerListener)
            exoPlayer.prepare()
        }

        this.playbackPosition = playbackPosition
        this.callBackOnPlay = onPlay
        this.callBackOnPause = onPause
    }

    fun releasePlayer() {
        playbackPosition = audioPlayer.currentPosition
        audioPlayer.pause()
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
        audioPlayer.seekTo(mediaItemIndex, playbackPosition)
        if (audioPlayer.mediaItemCount > 0 && playbackPosition != 0L) {
            playbackPosition = 0
        }
    }
}
