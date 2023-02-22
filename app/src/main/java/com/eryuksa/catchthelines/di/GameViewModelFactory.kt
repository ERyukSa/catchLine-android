package com.eryuksa.catchthelines.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.data.datasource.local.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofit
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.GameRemoteDataSource
import com.eryuksa.catchthelines.data.datasource.remote.GameRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.Retrofit
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.GameViewModel

class GameViewModelFactory(private val appContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val gameApi = Retrofit.getApi(GameRetrofitApi::class.java)
            val contentDetailApi = ContentDetailRetrofit.getApi(ContentDetailRetrofitApi::class.java)

            return GameViewModel(
                GameRepository(
                    GameRemoteDataSource(gameApi, contentDetailApi),
                    GameLocalDataSource(appContext)
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
