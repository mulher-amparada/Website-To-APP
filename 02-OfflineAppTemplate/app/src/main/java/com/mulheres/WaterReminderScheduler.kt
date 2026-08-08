package com.mulheres

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object WaterReminderScheduler {

    private const val BASE_REQUEST_CODE = 2000

    private val HORARIOS = intArrayOf(
        8, 10, 12, 14, 16, 18, 20
    )

    fun schedule(context: Context) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Primeiro cancela qualquer programação anterior
        cancel(context)

        val agora = Calendar.getInstance()

        for (i in HORARIOS.indices) {

            val hora = HORARIOS[i]

            val horario = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Se esse horário de hoje já passou,
            // não agenda. Amanhã será agendado novamente.
            if (!horario.after(agora)) {
                continue
            }

            agendarNotificacao(
                context,
                alarmManager,
                horario,
                BASE_REQUEST_CODE + i
            )
        }

        // Agenda a programação do próximo dia às 00:01
        // para recriar os horários das 08:00 às 20:00.
        val proximoDia = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val intent = Intent(
            context,
            WaterReminderReceiver::class.java
        ).apply {
            action = "com.mulheres.RESCHEDULE_WATER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            proximoDia.timeInMillis,
            pendingIntent
        )
    }

    private fun agendarNotificacao(
        context: Context,
        alarmManager: AlarmManager,
        horario: Calendar,
        requestCode: Int
    ) {

        val intent = Intent(
            context,
            WaterReminderReceiver::class.java
        ).apply {
            action = "com.mulheres.WATER_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            horario.timeInMillis,
            pendingIntent
        )
    }

    fun cancel(context: Context) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancela as 7 notificações
        for (i in HORARIOS.indices) {

            val intent = Intent(
                context,
                WaterReminderReceiver::class.java
            ).apply {
                action = "com.mulheres.WATER_REMINDER"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BASE_REQUEST_CODE + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }

        // Cancela o reagendamento da meia-noite
        val rescheduleIntent = Intent(
            context,
            WaterReminderReceiver::class.java
        ).apply {
            action = "com.mulheres.RESCHEDULE_WATER"
        }

        val reschedulePendingIntent = PendingIntent.getBroadcast(
            context,
            2999,
            rescheduleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(reschedulePendingIntent)
    }
}