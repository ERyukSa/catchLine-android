package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.GameRemoteDataSource
import com.eryuksa.catchthelines.data.dto.MediaContent
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: GameLocalDataSource
) {

    // private var lastHintCount = 10

    suspend fun getMediaContents(): List<MediaContent> =
        remoteDataSource.getMediaContents()

    suspend fun getAvailableHintCount(): Flow<Int> =
        localDataSource.getAvailableHintCount()

    suspend fun decreaseHintCount() {
        localDataSource.decreaseHintCount()
    }
}
