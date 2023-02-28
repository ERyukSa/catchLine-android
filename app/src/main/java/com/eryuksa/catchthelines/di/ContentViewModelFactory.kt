package com.eryuksa.catchthelines.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.data.datasource.local.ContentDatabase
import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofit
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.Retrofit
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.data.repository.HintCountRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.detail.DetailViewModel
import com.eryuksa.catchthelines.ui.game.GameViewModel

class ContentViewModelFactory private constructor() :
    ViewModelProvider.Factory {

    private val gameApi = Retrofit.getApi(ContentRetrofitApi::class.java)
    private val contentDetailApi =
        ContentDetailRetrofit.getApi(ContentDetailRetrofitApi::class.java)
    private val contentLocalDatabase: ContentDatabase by lazy {
        ContentDatabase.getInstance(application.applicationContext)
    }
    private val contentRepository: ContentRepository by lazy {
        ContentRepository(
            ContentRemoteDataSource(gameApi, contentDetailApi),
            ContentLocalDataSource(contentLocalDatabase.contentDao())
        )
    }

    private val Context.hintDataStore: DataStore<Preferences> by preferencesDataStore(name = "hint")
    private val HINT_COUNT_KEY = intPreferencesKey("hint_count")
    private val LAST_UPDATEDD_TIME_KEY = longPreferencesKey("last_updated_time_key")
    private val hintRepository: HintCountRepository by lazy {
        HintCountRepository(
            application.applicationContext.hintDataStore,
            HINT_COUNT_KEY,
            LAST_UPDATEDD_TIME_KEY
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(contentRepository, hintRepository, StringProvider.getInstance()) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(contentRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }

    companion object {

        private lateinit var application: Application
        private var instance: ContentViewModelFactory? = null

        fun initialize(_application: Application) {
            instance ?: synchronized(this) {
                instance ?: ContentViewModelFactory().also {
                    instance = it
                    application = _application
                }
            }
        }

        fun getInstance(): ContentViewModelFactory =
            instance ?: throw UninitializedPropertyAccessException()
    }
}
