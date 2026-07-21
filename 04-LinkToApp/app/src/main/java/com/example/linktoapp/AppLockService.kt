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

        val pacote = event.packageName?.toString() ?: return


        /*
         * Se apertou voltar na LockActivity,
         * reabre a tela de bloqueio usando
         * o pacote salvo anteriormente.
         */
        if (LockState.voltarPressionado) {

            LockState.voltarPressionado = false

            if (LockState.pacoteBloqueado.isEmpty())
                return

            val intent = Intent(
                this,
                LockActivity::class.java
            )

            intent.putExtra(
                "package",
                LockState.pacoteBloqueado
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )

            startActivity(intent)

            return
        }


        /*
         * Ignora eventos do sistema:
         * - barra de navegação
         * - notificações
         * - painel rápido
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
         * Ignora troca de Activity
         * dentro do mesmo aplicativo.
         */
        if (pacote == ultimoPacote)
            return


        ultimoPacote = pacote


        /*
         * Verifica se o aplicativo está protegido.
         */
        if (!repository.protegido(pacote))
            return


        /*
         * Não abre duas telas de bloqueio.
         */
        if (LockActivity.aberta)
            return


        /*
         * Guarda o app que está sendo bloqueado.
         */
        LockState.pacoteBloqueado = pacote


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
            Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        )

        startActivity(intent)

    }

    override fun onInterrupt() {
    }
}