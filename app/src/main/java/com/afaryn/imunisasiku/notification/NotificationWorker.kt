package com.afaryn.imunisasiku.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.afaryn.imunisasiku.R
import com.afaryn.imunisasiku.notification.database.NotificationDatabase
import com.afaryn.imunisasiku.notification.database.NotificationEntity
import com.afaryn.imunisasiku.presentation.auth.AuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val imunisasiTitle = inputData.getString(NOTIFICATION_TITLE)
    private val imunisasiTime = inputData.getString(WAKTU_IMUNISASI)

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(applicationContext, AuthActivity::class.java)
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            imunisasiTitle?.let { title ->
                val notifikasi = NotificationEntity(title = title, message = imunisasiTime ?: "-")
                NotificationDatabase.getInstance(applicationContext).notificationDao.insert(notifikasi)

                val pendingIntent = getPendingIntent()
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Reminder Imunisasi $title")
                    .setContentText(imunisasiTime ?: "-")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    builder.setChannelId(NOTIFICATION_CHANNEL_ID)
                    notificationManager.createNotificationChannel(channel)
                }

                val notification = builder.build()
                notificationManager.notify(1, notification)
            }
        }

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_TITLE = "notification_title"
        const val WAKTU_IMUNISASI = "waktu_imunisasi"
        const val NOTIFICATION_CHANNEL_ID = "notification-channel"
        const val CHANNEL_NAME = "channel-imunisasi"
    }
}