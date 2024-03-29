package com.eryuksa.catchthelines.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.eryuksa.catchthelines.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HintCountRepository @Inject constructor(
    private val hintCountStore: DataStore<Preferences>,
    @ApplicationScope externalScope: CoroutineScope
) {
    private val hintCountKey = intPreferencesKey("hint_count")
    private val lastUpdatedTimeKey = longPreferencesKey("last_updated_time_key")

    private val mutex = Mutex()
    private var isIncreasingHintCount = false

    val availableHintCount: Flow<Int> by lazy {
        hintCountStore.data.map { preference ->
            preference[hintCountKey] ?: MAX_HINT_COUNT
        }
    }

    init {
        externalScope.launch {
            setUpHintCount()
            increaseHintCountPeriodically()
        }
    }

    private suspend fun setUpHintCount() {
        hintCountStore.edit { preference ->
            val lastHintCount = preference[hintCountKey] ?: MAX_HINT_COUNT
            val lastUpdatedTime = preference[lastUpdatedTimeKey] ?: System.currentTimeMillis()
            val increasedCount =
                ((System.currentTimeMillis() - lastUpdatedTime) / PERIOD_IN_MILLIS).toInt()
            preference.updateHintData((lastHintCount + increasedCount).coerceAtMost(MAX_HINT_COUNT))
        }
    }

    private suspend fun increaseHintCountPeriodically() {
        mutex.withLock {
            if (isIncreasingHintCount) return
            isIncreasingHintCount = true
        }

        while (true) {
            delay(PERIOD_IN_MILLIS)
            mutex.withLock {
                val increasedCount = increaseHintCount()
                if (increasedCount == MAX_HINT_COUNT) {
                    isIncreasingHintCount = false
                    return
                }
            }
        }
    }

    suspend fun decreaseHintCount() {
        hintCountStore.edit { preference ->
            val hintCount = preference[hintCountKey] ?: MAX_HINT_COUNT
            preference.updateHintData(hintCount - 1)
        }
        increaseHintCountPeriodically()
    }

    private suspend fun increaseHintCount(): Int {
        return hintCountStore.edit { preference ->
            val hintCount = preference[hintCountKey] ?: 0
            if (hintCount == MAX_HINT_COUNT) {
                return@edit
            }
            preference.updateHintData(hintCount + 1)
        }[hintCountKey] ?: MAX_HINT_COUNT
    }

    private fun MutablePreferences.updateHintData(hintCount: Int) {
        this[hintCountKey] = hintCount
        this[lastUpdatedTimeKey] = System.currentTimeMillis()
    }

    companion object {
        private const val MAX_HINT_COUNT = 10
        private const val PERIOD_IN_MILLIS: Long = 1000 * 60 * 3
    }
}
