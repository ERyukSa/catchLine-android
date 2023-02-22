package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.GameRemoteDataSource
import com.eryuksa.catchthelines.data.dto.MediaContent
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: GameLocalDataSource
) {

    suspend fun getMediaContents(): List<MediaContent> =
        remoteDataSource.getMediaContents()

    fun getAvailableHintCount(): Flow<Int> =
        localDataSource.getAvailableHintCount()

    suspend fun decreaseHintCount() =
        localDataSource.decreaseHintCount()

    suspend fun increaseHintCount() =
        localDataSource.increaseHintCount()
}
