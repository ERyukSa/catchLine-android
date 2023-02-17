package com.eryuksa.catchthelines.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameLocalDataSource(private val context: Context) {

    private val HINT_COUNT_KEY = intPreferencesKey("hint_count")
    private val Context.hintDataStore: DataStore<Preferences> by preferencesDataStore(name = "temp")

    suspend fun getAvailableHintCount(): Flow<Int> =
        context.hintDataStore.data.map { preference ->
            preference[HINT_COUNT_KEY] ?: 10
        }

    suspend fun decreaseHintCount() {
        context.hintDataStore.edit { preference ->
            val hintCount = preference[HINT_COUNT_KEY] ?: return@edit
            preference[HINT_COUNT_KEY] = hintCount - 1
        }
    }
}
