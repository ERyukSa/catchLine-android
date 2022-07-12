package com.eryuksa.catchline_android.repository

import android.media.MediaPlayer
import com.eryuksa.catchline_android.model.Content

class GameRepository(private val dataSource: GameDataSourceImpl) {

    fun getCaughtNumber(): Int {
        return dataSource.getCaughtNumber()
    }

    fun getChallengeNumber(): Int {
        return dataSource.getChallengeNumber()
    }

    fun getUncaughtContents(): List<Content> {
        return dataSource.getUncaughtContents()
    }

    fun getMediaPlayer(fileName: String): MediaPlayer {
        return dataSource.getMediaPlayer(fileName)
    }
}