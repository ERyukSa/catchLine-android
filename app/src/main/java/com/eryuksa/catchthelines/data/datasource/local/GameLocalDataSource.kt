package com.eryuksa.catchthelines.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val HINT_COUNT_KEY = intPreferencesKey("hint_count")
private val Context.hintDataStore: DataStore<Preferences> by preferencesDataStore(name = "temp")

class GameLocalDataSource(private val appContext: Context) {

    fun getAvailableHintCount(): Flow<Int> {
        return appContext.hintDataStore.data.map { preference ->
            increaseHintCount()
            preference[HINT_COUNT_KEY] ?: MAX_HINT_COUNT
        }
    }

    suspend fun decreaseHintCount() {
        appContext.hintDataStore.edit { preference ->
            val hintCount = preference[HINT_COUNT_KEY] ?: MAX_HINT_COUNT
            preference[HINT_COUNT_KEY] = (hintCount - 1).coerceAtLeast(0)
        }
    }

    suspend fun increaseHintCount() {
        appContext.hintDataStore.edit { preference ->
            val hintCount = preference[HINT_COUNT_KEY] ?: MAX_HINT_COUNT
            preference[HINT_COUNT_KEY] = (hintCount + 1).coerceAtMost(MAX_HINT_COUNT)
        }
    }

    companion object {
        private const val MAX_HINT_COUNT = 10
    }
}
