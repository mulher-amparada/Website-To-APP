package com.example.linktoapp

import android.content.Context

object LockOverlayManager {

    private var overlay: LockOverlay? = null


    fun mostrar(context: Context) {

        if (overlay != null)
            return

        overlay = LockOverlay(context)

        overlay?.mostrar()

    }


    fun remover() {

        overlay?.remover()

        overlay = null

    }

}