package com.eryuksa.catchthelines.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.data.datasource.local.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.GameRemoteDataSource
import com.eryuksa.catchthelines.data.datasource.remote.Retrofit
import com.eryuksa.catchthelines.data.datasource.remote.RetrofitGameApi
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.GameViewModel

class GameViewModelFactory(private val appContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val gameApi = Retrofit.getApi(RetrofitGameApi::class.java)
            return GameViewModel(
                GameRepository(GameRemoteDataSource(gameApi), GameLocalDataSource(appContext))
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
