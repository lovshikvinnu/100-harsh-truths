package com.harshtruths.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.harshtruths.app.data.NotificationScheduler
import com.harshtruths.app.data.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val settingsDataStore = SettingsDataStore(context)
                val enabled = settingsDataStore.notificationsEnabled.first()
                if (enabled) {
                    val time = settingsDataStore.notificationTime.first()
                    NotificationScheduler.scheduleDailyNotification(context, time.first, time.second)
                }
            }
        }
    }
}