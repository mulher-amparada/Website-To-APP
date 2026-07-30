package com.mulheres

import android.content.Intent
import android.service.quicksettings.TileService

class AbrirAppTile : TileService() {

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivityAndCollapse(intent)
    }
}