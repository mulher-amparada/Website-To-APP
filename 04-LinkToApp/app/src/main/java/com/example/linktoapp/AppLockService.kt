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

        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return

        val pacote = event.packageName?.toString() ?: return

        // Ignora o próprio AppLock
        if (pacote == packageName) {
            return
        }

        // Mesmo aplicativo: ignora troca de Activity
        if (pacote == ultimoPacote) {
            return
        }

        // Novo aplicativo aberto
        ultimoPacote = pacote

        // Verifica se está protegido
        if (!repository.protegido(pacote))
            return

        // Evita abrir várias telas de bloqueio
        if (LockActivity.aberta)
            return

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