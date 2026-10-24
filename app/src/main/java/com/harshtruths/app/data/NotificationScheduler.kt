package com.harshtruths.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.ListenableWorker.Result
import com.harshtruths.app.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

typealias NotificationScheduler = NotificationWorker

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val quoteRepository = QuoteRepository(applicationContext)
        val quote = quoteRepository.getTodayQuote() ?: return@withContext Result.success()

        showNotification(quote)
        Result.success()
    }

    private fun showNotification(quote: Quote) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, "daily_quote_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Today's Harsh Truth #${quote.id}")
            .setContentText(quote.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote.text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(quote.id, notification)
    }

    companion object {
        fun scheduleDailyNotification(context: Context, hour: Int, minute: Int) {
            val workRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 24, TimeUnit.HOURS)
                .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_quote_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelDailyNotification(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("daily_quote_notification")
        }

        private fun calculateInitialDelay(hour: Int, minute: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val now = Calendar.getInstance()
            if (calendar.timeInMillis <= now.timeInMillis) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            return calendar.timeInMillis - now.timeInMillis
        }
    }
}