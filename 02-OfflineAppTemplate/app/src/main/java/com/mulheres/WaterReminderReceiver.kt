package com.mulheres

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class WaterReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "water_reminders"
        private const val NOTIFICATION_ID = 5001
    }

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        createChannel(context)

        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Hora de beber água")
            .setContentText("Que tal beber um pouco de água agora?")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Seu corpo agradece. 💙\n\n" +
                        "Pare um pouquinho e beba água."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            NOTIFICATION_ID,
            notification
        )
    }

    private fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lembretes de água",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Lembretes para beber água"

                enableVibration(true)

                setShowBadge(true)
            }

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}