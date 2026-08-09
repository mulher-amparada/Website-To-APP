package com.mulheres

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.TileService

class AbrirAppTile : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        val tile = qsTile ?: return

        when (getAliasAtivo()) {

            ".IconeOriginal" -> {
                tile.icon = Icon.createWithResource(
                    this,
                    R.mipmap.ic_launcher
                )
                tile.label = "Mulher Amparada"
            }

            ".Icone1" -> {
                tile.icon = Icon.createWithResource(
                    this,
                    R.drawable.icon1
                )
                tile.label = "Mulher Amparada"
            }

            ".Icone2" -> {
                tile.icon = Icon.createWithResource(
                    this,
                    R.drawable.icon2
                )
                tile.label = "Mulher Amparada"
            }

            ".Icone3" -> {
                tile.icon = Icon.createWithResource(
                    this,
                    R.drawable.icon3
                )
                tile.label = "Calculadora"
            }
        }

        tile.updateTile()
    }

    private fun getAliasAtivo(): String {

        val aliases = listOf(
            ".IconeOriginal",
            ".Icone1",
            ".Icone2",
            ".Icone3"
        )

        for (alias in aliases) {

            val component = ComponentName(
                this,
                packageName + alias
            )

            if (
                packageManager.getComponentEnabledSetting(component) ==
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            ) {
                return alias
            }
        }

        return ".IconeOriginal"
    }

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            startActivityAndCollapse(pendingIntent)

        } else {

            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}