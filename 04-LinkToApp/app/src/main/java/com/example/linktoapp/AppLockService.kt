package com.linktoapp.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
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


        val pacote = event.packageName?.toString()
            ?: return



        // Ignora sistema
        if (pacote == "com.android.systemui")
            return


        // Ignora teclado
        if (pacote == "com.google.android.inputmethod.latin")
            return


        // Ignora o próprio app
        if (pacote == packageName)
            return



        // Mesmo pacote, não faz novamente
        if (pacote == ultimoPacote)
            return



        ultimoPacote = pacote



        // Verifica proteção
        if (!repository.protegido(pacote))
            return



        if (LockState.bloqueando)
            return



        LockState.pacoteBloqueado = pacote
        LockState.bloqueando = true



        val intent = Intent(
            this,
            LockActivity::class.java
        )


        intent.putExtra(
            "package",
            pacote
        )


        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        )


        startActivity(intent)

    }


    override fun onInterrupt() {
    }

}