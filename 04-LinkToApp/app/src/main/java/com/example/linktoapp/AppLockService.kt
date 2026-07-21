package com.linktoapp.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    private lateinit var repository: AppRepository

    private var ultimoPacote = ""

    private var pacoteBloqueadoAtual = ""


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



        /*
         * Se o app já foi desbloqueado,
         * não abre a tela novamente.
         */
        if (pacote == LockState.pacoteDesbloqueado) {
            return
        }



        /*
         * Voltou da LockActivity
         */
        if (LockState.voltarPressionado) {

            LockState.voltarPressionado = false


            if (LockState.pacoteBloqueado.isEmpty())
                return


            abrirBloqueio(
                LockState.pacoteBloqueado
            )

            return
        }



        /*
         * Saiu do aplicativo desbloqueado
         */
        if (
            LockState.pacoteDesbloqueado.isNotEmpty() &&
            pacote != LockState.pacoteDesbloqueado
        ) {

            LockState.pacoteDesbloqueado = ""

        }



        /*
         * Ignora SystemUI
         */
        if (pacote == "com.android.systemui")
            return



        /*
         * Ignora teclado
         */
        if (pacote == "com.google.android.inputmethod.latin")
            return



        /*
         * Ignora o próprio AppLock
         */
        if (pacote == packageName)
            return



        /*
         * Ignora mudança interna do mesmo app
         */
        if (pacote == ultimoPacote)
            return



        ultimoPacote = pacote



        /*
         * Verifica se o app está protegido
         */
        if (!repository.protegido(pacote))
            return



        /*
         * Evita abrir duas telas
         */
        if (LockActivity.aberta)
            return



        pacoteBloqueadoAtual = pacote

        LockState.pacoteBloqueado = pacote


        abrirBloqueio(
            pacote
        )

    }



    private fun abrirBloqueio(
        pacote: String
    ) {

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