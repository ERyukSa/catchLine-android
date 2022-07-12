package com.eryuksa.catchline_android.repository

import android.media.MediaPlayer

interface GameMediaDataSource {

    fun getMediaPlayer(fileName: String): MediaPlayer
}