package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.GameRemoteDataSource
import com.eryuksa.catchthelines.data.dto.MediaContent
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val remoteDataSource: GameRemoteDataSource,
    private val localDataSource: GameLocalDataSource
) {

    suspend fun getMediaContents(): List<MediaContent> =
        remoteDataSource.getMediaContents()

    suspend fun getAvailableHintCount(): Flow<Int> =
        localDataSource.getAvailableHintCount()

    suspend fun decreaseHintCount() =
        localDataSource.decreaseHintCount()
}
