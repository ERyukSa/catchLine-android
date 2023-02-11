package com.eryuksa.catchthelines.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.repository.GameDataSourceImpl
import com.eryuksa.catchthelines.repository.GameRepository
import com.eryuksa.catchthelines.ui.game.GameViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            val gameRepository =
                GameRepository(GameDataSourceImpl(AssetLoader(context), MediaLoader(context)))
            GameViewModel(gameRepository) as T
        } else {
            throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
        }
    }
}
