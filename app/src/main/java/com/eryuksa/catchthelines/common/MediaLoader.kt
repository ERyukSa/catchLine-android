package com.eryuksa.catchthelines.common

import android.content.Context
import android.media.MediaPlayer

class MediaLoader(private val context: Context) {

    fun getMediaPlayer(fileName: String): MediaPlayer {
        val audioResId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        return MediaPlayer.create(context, audioResId)
    }
}
