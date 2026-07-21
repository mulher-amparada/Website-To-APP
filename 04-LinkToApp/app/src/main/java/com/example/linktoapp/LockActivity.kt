package com.linktoapp.app

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class LockActivity : AppCompatActivity() {

    companion object {

        @Volatile
        var aberta = false

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        aberta = true


        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    // Apenas bloqueia o voltar
                    // sem fechar a Activity

                    
                }

            }
        )



        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )



        window.apply {

            addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )


            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                navigationBarDividerColor =
                    Color.TRANSPARENT

            }



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                isStatusBarContrastEnforced = false
                isNavigationBarContrastEnforced = false

            }



            // Mantém layout ocupando a área das barras
            // sem esconder fullscreen

            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        }



        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false

        }



        setContentView(
            R.layout.activity_lock
        )



        aplicarFonte(
            findViewById(android.R.id.content)
        )



        val pacote = intent.getStringExtra(
            "package"
        )



        if (pacote.isNullOrEmpty()) {

            aberta = false

            finish()

            return

        }



        BiometricHelper(this).autenticar(

            sucesso = {


                LockState.pacoteDesbloqueado =
                    pacote


                LockState.bloqueando = false


                aberta = false


                finish()

            },



            erro = {

                // Não fecha.
                // Continua na tela de bloqueio.

                LockState.bloqueando = true

            }

        )

    }




    private fun aplicarFonte(
        view: View
    ) {


        val fonte =
            android.graphics.Typeface.createFromAsset(
                assets,
                "font.ttf"
            )



        if (view is android.widget.TextView) {

            view.typeface = fonte

        }



        if (view is android.view.ViewGroup) {

            for (i in 0 until view.childCount) {

                aplicarFonte(
                    view.getChildAt(i)
                )

            }

        }

    }




    override fun onDestroy() {

        aberta = false

        super.onDestroy()

    }

}