package com.linktoapp.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    private lateinit var repository: AppRepository

    private var ultimoPacote = ""
    private var pacoteDesbloqueado = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository = AppRepository(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return

        val pacote = event.packageName?.toString() ?: return

        if (pacote == packageName)
            return

        if (pacote == ultimoPacote)
            return

        if (pacoteDesbloqueado.isNotEmpty() &&
            pacote != pacoteDesbloqueado
        ) {
            UnlockManager.bloquear(pacoteDesbloqueado)
            pacoteDesbloqueado = ""
        }

        ultimoPacote = pacote

        if (!repository.protegido(pacote))
            return

        if (!UnlockManager.bloqueado(pacote))
            return

        pacoteDesbloqueado = pacote

        val intent = Intent(this, LockActivity::class.java)

        intent.putExtra("package", pacote)

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