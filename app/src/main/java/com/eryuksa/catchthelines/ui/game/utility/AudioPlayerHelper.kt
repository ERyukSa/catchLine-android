package com.eryuksa.catchthelines.ui.game.utility

import android.content.Context
import com.eryuksa.catchthelines.ui.game.GameViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class AudioPlayerHelper(private val context: Context, private val viewModel: GameViewModel) {

    private var _audioPlayer: ExoPlayer? = null
    val audioPlayer get() = _audioPlayer!!

    fun initialize() {
        _audioPlayer = ExoPlayer.Builder(context).build().apply {
            pauseAtEndOfMediaItems = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            setMediaItems(
                viewModel.uiState.value.contentItems.flatMap {
                    it.audioUrls.map { uri -> MediaItem.fromUri(uri) }
                }
            )
            seekTo(viewModel.uiState.value.audioIndex, viewModel.audioPlaybackPosition)
            prepare()
        }
    }

    fun release() {
        viewModel.audioPlaybackPosition = audioPlayer.currentPosition
        audioPlayer.release()
        _audioPlayer = null
    }

    fun setAudioItems(urls: List<List<String>>) {
        if (audioPlayer.mediaItemCount == urls.size * 2) {
            return
        }
        audioPlayer.pause()
        audioPlayer.setMediaItems(
            urls.flatten().map { url -> MediaItem.fromUri(url) }
        )
        audioPlayer.seekTo(viewModel.uiState.value.audioIndex, 0)
    }

    fun moveTo(mediaItemIndex: Int) {
        if (audioPlayer.currentMediaItemIndex == mediaItemIndex) {
            return
        }
        audioPlayer.pause()
        audioPlayer.seekTo(mediaItemIndex, 0)
    }
}
