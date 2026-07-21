package com.linktoapp.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppLockService : AccessibilityService() {

    private lateinit var repository: AppRepository

    private var ultimoPacote = ""

    private var overlay: LockOverlay? = null


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



        // Voltar pressionado na tela de bloqueio
        if (LockState.voltarPressionado) {

            LockState.voltarPressionado = false

            if (LockState.pacoteBloqueado.isEmpty())
                return


            mostrarBloqueio()

            return
        }



        // Ignora SystemUI
        if (pacote == "com.android.systemui")
            return


        // Ignora teclado
        if (pacote == "com.google.android.inputmethod.latin")
            return



        // Ignora o próprio app
        if (pacote == packageName)
            return



        // Ignora mudança interna de Activity
        if (pacote == ultimoPacote)
            return



        ultimoPacote = pacote



        // Verifica se está protegido
        if (!ehAplicativoReal(pacote))
    return

if (!repository.protegido(pacote))
    return



        // Salva o pacote bloqueado
        LockState.pacoteBloqueado = pacote



        mostrarBloqueio()

    }


private fun ehAplicativoReal(pacote: String): Boolean {

    return try {

        val info = packageManager.getApplicationInfo(
            pacote,
            0
        )

        info.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0 ||
        repository.protegido(pacote)

    } catch (e: Exception) {

        false

    }

}

    private fun mostrarBloqueio() {

    if (overlay != null)
        return


    overlay = LockOverlay(this)

    overlay?.mostrar()


    val intent = Intent(
        this,
        BiometricActivity::class.java
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