package com.harshtruths.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_datastore")
private val NOTIFICATION_HOUR_KEY = intPreferencesKey("notification_hour")
private val NOTIFICATION_MINUTE_KEY = intPreferencesKey("notification_minute")
private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")

class SettingsDataStore(private val context: Context) {

    val notificationTime: Flow<Pair<Int, Int>> = context.settingsDataStore.data
        .map { prefs ->
            val hour = prefs[NOTIFICATION_HOUR_KEY] ?: 9
            val minute = prefs[NOTIFICATION_MINUTE_KEY] ?: 0
            Pair(hour, minute)
        }

    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs ->
            prefs[NOTIFICATION_ENABLED_KEY] ?: true
        }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIFICATION_HOUR_KEY] = hour
            prefs[NOTIFICATION_MINUTE_KEY] = minute
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }
}
