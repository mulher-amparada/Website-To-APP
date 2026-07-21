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



        // Evita recriar a tela enquanto está bloqueando
        if (LockState.bloqueando)
            return



        // Ignora SystemUI
        if (pacote == "com.android.systemui")
            return



        // Ignora teclado
        if (pacote == "com.google.android.inputmethod.latin")
            return



        // Ignora o próprio app
        if (pacote == packageName)
            return



        // Se este app já foi desbloqueado,
        // deixa ele continuar aberto
        if (pacote == LockState.pacoteDesbloqueado) {

            return

        }



        // Saiu do app desbloqueado
        if (
            LockState.pacoteDesbloqueado.isNotEmpty() &&
            pacote != LockState.pacoteDesbloqueado
        ) {

            LockState.pacoteDesbloqueado = ""

        }



        // Ignora troca de Activity do mesmo app
        if (pacote == ultimoPacote)
            return


        ultimoPacote = pacote



        // Verifica se o app está protegido
        if (!repository.protegido(pacote))
            return



        // Salva o pacote bloqueado
        LockState.pacoteBloqueado = pacote



        abrirBloqueio(
            pacote
        )

    }



    private fun abrirBloqueio(
        pacote: String
    ) {


        if (LockState.bloqueando)
            return



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
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
            Intent.FLAG_ACTIVITY_NO_HISTORY
        )



        startActivity(intent)

    }



    override fun onInterrupt() {
    }

}