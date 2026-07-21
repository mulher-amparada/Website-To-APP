package com.linktoapp.app

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class BiometricHelper(
    private val activity: AppCompatActivity
) {

    fun autenticar(
        sucesso: () -> Unit,
        erro: () -> Unit
    ) {

        val biometricManager = BiometricManager.from(activity)

        val autenticadores =
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

        if (biometricManager.canAuthenticate(autenticadores)
            != BiometricManager.BIOMETRIC_SUCCESS
        ) {
            erro()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    sucesso()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    erro()
                }

                override fun onAuthenticationFailed() {
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Desbloquear aplicativo")
            .setSubtitle("Confirme sua identidade")
            .setAllowedAuthenticators(autenticadores)
            .build()

        prompt.authenticate(info)
    }
}