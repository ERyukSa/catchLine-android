package com.eryuksa.catchthelines.repository

import android.media.MediaPlayer

interface GameMediaDataSource {

    fun getMediaPlayer(fileName: String): MediaPlayer
}
