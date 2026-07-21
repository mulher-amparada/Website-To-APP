package com.linktoapp.app

object LockOverlayManager {

    @Volatile
    var overlay: LockOverlay? = null


    fun mostrar(service: AppLockService) {

        if (overlay != null)
            return


        overlay = LockOverlay(service)

        overlay?.mostrar()

    }


    fun remover() {

        overlay?.remover()

        overlay = null

    }

}