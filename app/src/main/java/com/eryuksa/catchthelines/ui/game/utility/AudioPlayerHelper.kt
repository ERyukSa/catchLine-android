package com.eryuksa.catchthelines.ui.game.utility

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class AudioPlayerHelper(private val context: Context) {

    private var _audioPlayer: ExoPlayer? = null
    val audioPlayer get() = _audioPlayer!!

    private var playbackPosition = 0L

    fun initialize() {
        _audioPlayer = ExoPlayer.Builder(context).build().apply {
            pauseAtEndOfMediaItems = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            prepare()
        }
    }

    fun release() {
        playbackPosition = audioPlayer.currentPosition
        audioPlayer.release()
        _audioPlayer = null
    }

    fun setAudioItems(uris: List<List<String>>) {
        audioPlayer.setMediaItems(
            uris.flatten().map { uri -> MediaItem.fromUri(uri) }
        )
    }

    fun moveTo(mediaItemIndex: Int) {
        audioPlayer.pause()
        audioPlayer.seekTo(mediaItemIndex, playbackPosition)
        if (audioPlayer.mediaItemCount > 0 && playbackPosition != 0L) {
            playbackPosition = 0
        }
    }
}
