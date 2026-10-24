package com.harshtruths.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.harshtruths.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_datastore")
private val NOTIFICATION_HOUR_KEY = intPreferencesKey("notification_hour")
private val NOTIFICATION_MINUTE_KEY = intPreferencesKey("notification_minute")
private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
private val USER_NAME_KEY = stringPreferencesKey("user_name")
private val NAME_SETUP_COMPLETE_KEY = booleanPreferencesKey("name_setup_complete")
private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")

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

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data
        .map { prefs ->
            val saved = prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(saved)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
        }

    val userName: Flow<String?> = context.settingsDataStore.data
        .map { prefs ->
            prefs[USER_NAME_KEY]
        }

    val isNameSetupComplete: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs ->
            prefs[NAME_SETUP_COMPLETE_KEY] ?: !prefs[USER_NAME_KEY].isNullOrBlank()
        }

    val isOnboardingComplete: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs ->
            prefs[ONBOARDING_COMPLETE_KEY] ?: false
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

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun setUserName(name: String) {
        val trimmed = name.trim()
        context.settingsDataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = trimmed
            prefs[NAME_SETUP_COMPLETE_KEY] = trimmed.isNotEmpty()
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean = true) {
        context.settingsDataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE_KEY] = complete
        }
    }
}
