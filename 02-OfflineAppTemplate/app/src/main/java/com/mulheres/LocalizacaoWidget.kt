package com.mulheres

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class LocalizacaoWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            val views = RemoteViews(
                context.packageName,
                R.layout.widget_localizacao
            )

            val intent = Intent(context, LocalizacaoWidget::class.java).apply {
                action = ACTION_ENVIAR_LOCALIZACAO
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.localizacao, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_ENVIAR_LOCALIZACAO) {
            val serviceIntent = Intent(context, LocalizacaoService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }

    companion object {
        private const val ACTION_ENVIAR_LOCALIZACAO =
            "com.mulheres.ENVIAR_LOCALIZACAO"
    }
}