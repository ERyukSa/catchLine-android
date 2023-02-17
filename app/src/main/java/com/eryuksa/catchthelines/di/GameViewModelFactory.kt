package com.eryuksa.catchthelines.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.data.datasource.GameLocalDataSource
import com.eryuksa.catchthelines.data.datasource.GameRemoteDataSource
import com.eryuksa.catchthelines.data.datasource.Retrofit
import com.eryuksa.catchthelines.data.datasource.RetrofitGameApi
import com.eryuksa.catchthelines.data.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.GameViewModel

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val gameApi = Retrofit.getApi(RetrofitGameApi::class.java)
            return GameViewModel(
                GameRepository(GameRemoteDataSource(gameApi), GameLocalDataSource(context))
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
