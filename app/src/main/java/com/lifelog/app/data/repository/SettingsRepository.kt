package com.lifelog.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val darkTheme: Flow<Boolean> = dataStore.data.map { it[DARK_THEME] ?: false }
    val notificationEnabled: Flow<Boolean> = dataStore.data.map { it[NOTIFICATION_ENABLED] ?: true }
    val notificationHour: Flow<Int> = dataStore.data.map { it[NOTIFICATION_HOUR] ?: 21 }
    val notificationMinute: Flow<Int> = dataStore.data.map { it[NOTIFICATION_MINUTE] ?: 0 }
    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { it[FIRST_LAUNCH] ?: true }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[DARK_THEME] = enabled }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[NOTIFICATION_HOUR] = hour
            it[NOTIFICATION_MINUTE] = minute
        }
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        dataStore.edit { it[FIRST_LAUNCH] = isFirst }
    }
}
