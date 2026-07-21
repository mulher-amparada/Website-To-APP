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

                    // Bloqueia o botão voltar
                    LockState.voltarPressionado = true

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
                navigationBarDividerColor = Color.TRANSPARENT
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                isStatusBarContrastEnforced = false
                isNavigationBarContrastEnforced = false

            }


            decorView.systemUiVisibility =
    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
    View.SYSTEM_UI_FLAG_FULLSCREEN or
    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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



        val raiz = findViewById<View>(
            android.R.id.content
        )

        aplicarFonte(raiz)



        val pacote = intent.getStringExtra(
            "package"
        )


        if (pacote.isNullOrEmpty()) {

            finish()
            return

        }



        BiometricHelper(this).autenticar(

            sucesso = {

                aberta = false

                finish()

            },


            erro = {

                // Não fecha a tela se cancelar/errar
                // mantém o bloqueio ativo

            }

        )

    }



    private fun aplicarFonte(
        view: View
    ) {

        val fonte = android.graphics.Typeface.createFromAsset(
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

        super.onDestroy()

    }

}