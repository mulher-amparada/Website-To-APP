package com.linktoapp.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BiometricActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BiometricHelper(this).autenticar(

            sucesso = {
                LockOverlayManager.remover()
                finish()
            },

            erro = {
                finish()
            }

        )
    }
}