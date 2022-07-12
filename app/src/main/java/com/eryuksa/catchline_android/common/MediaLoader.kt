package com.eryuksa.catchline_android.common

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class MediaLoader(private val context: Context) {

    fun getMediaPlayer(fileName: String): MediaPlayer {
        val audioResId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        return MediaPlayer.create(context, audioResId)
    }
}