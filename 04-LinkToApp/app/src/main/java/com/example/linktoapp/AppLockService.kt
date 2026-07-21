package com.linktoapp.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    private lateinit var repository: AppRepository

    private var ultimoPacote = ""


    override fun onServiceConnected() {

        super.onServiceConnected()

        repository = AppRepository(this)

    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {


        if (event == null)
            return


        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return



        val pacote =
            event.packageName?.toString()
                ?: return



        // ignora sistema

        if (pacote == "com.android.systemui")
            return



        // ignora teclado

        if (pacote == "com.google.android.inputmethod.latin")
            return



        // ignora o próprio app

        if (pacote == packageName)
            return



        // mesmo app

        if (pacote == ultimoPacote)
            return



        ultimoPacote = pacote



        // verifica proteção

        if (!repository.protegido(pacote))
            return



        // bloqueia tela pelo administrador

        DeviceAdminHelper.bloquearTela(this)

    }



    override fun onInterrupt() {

    }

}