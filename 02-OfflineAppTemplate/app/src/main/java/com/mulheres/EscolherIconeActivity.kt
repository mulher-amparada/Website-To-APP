package com.mulheres

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class EscolherIconeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ==========================================
        // TELA FULLSCREEN
        // ==========================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        controller.hide(
            WindowInsetsCompat.Type.systemBars()
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        window.statusBarColor =
            android.graphics.Color.TRANSPARENT

        window.navigationBarColor =
            android.graphics.Color.TRANSPARENT

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }

        // ==========================================
        // LAYOUT
        // ==========================================

        setContentView(
            R.layout.activity_escolher_icone
        )

        val raiz = findViewById<View>(
            android.R.id.content
        )

        aplicarFonte(raiz)

        // ==========================================
        // BOTÃO ÍCONE ORIGINAL
        // ==========================================

        findViewById<Button>(
            R.id.btnIconeOriginal
        ).setOnClickListener {

            trocarParaOriginal()
        }

        // ==========================================
        // BOTÃO ÍCONE 1
        // ==========================================

        findViewById<Button>(
            R.id.btnIcone1
        ).setOnClickListener {

            trocarParaIcone1()
        }

        // ==========================================
        // BOTÃO ÍCONE 2
        // ==========================================

        findViewById<Button>(
            R.id.btnIcone2
        ).setOnClickListener {

            trocarParaIcone2()
        }

        // ==========================================
        // BOTÃO ÍCONE 3
        // ==========================================

        findViewById<Button>(
            R.id.btnIcone3
        ).setOnClickListener {

            trocarParaIcone3()
        }
    }

    // ==========================================
    // TROCAR ÍCONE
    // ==========================================

    private fun trocarPara(
        nomeAtivo: String
    ) {

        val pm = packageManager

        val icons = listOf(

            "com.mulheres.IconeOriginal",

            "com.mulheres.Icone1",

            "com.mulheres.Icone2",

            "com.mulheres.Icone3"
        )

        icons.forEach { nome ->

            val component =
                android.content.ComponentName(
                    this,
                    nome
                )

            val estado =

                if (nome == nomeAtivo) {

                    android.content.pm.PackageManager
                        .COMPONENT_ENABLED_STATE_ENABLED

                } else {

                    android.content.pm.PackageManager
                        .COMPONENT_ENABLED_STATE_DISABLED
                }

            pm.setComponentEnabledSetting(

                component,

                estado,

                android.content.pm.PackageManager
                    .DONT_KILL_APP
            )
        }
    }

    // ==========================================
    // SALVAR CONFIGURAÇÃO
    // ==========================================

    private fun salvarConfiguracao(
        abrirIndex1: Boolean
    ) {

        getSharedPreferences(
            "configuracao",
            MODE_PRIVATE
        )
            .edit()
            .putBoolean(
                "ABRIR_INDEX1",
                abrirIndex1
            )
            .apply()
    }

    // ==========================================
    // ÍCONE 1
    // ==========================================

    private fun trocarParaIcone1() {

        // Troca o ícone
        trocarPara(
            "com.mulheres.Icone1"
        )

        // A partir de agora:
        // abrirá index11.html
        salvarConfiguracao(false)

        finish()
    }

    // ==========================================
    // ÍCONE 2
    // ==========================================

    private fun trocarParaIcone2() {

        // Troca o ícone
        trocarPara(
            "com.mulheres.Icone2"
        )

        // A partir de agora:
        // abrirá index11.html
        salvarConfiguracao(false)

        finish()
    }

    // ==========================================
    // ÍCONE 3
    // ==========================================

    private fun trocarParaIcone3() {

        // Troca o ícone
        trocarPara(
            "com.mulheres.Icone3"
        )

        // Salva permanentemente:
        // quando o aplicativo for aberto
        // pelo ícone 3, abrir index1.html
        salvarConfiguracao(true)

        finish()
    }

    // ==========================================
    // ÍCONE ORIGINAL
    // ==========================================

    private fun trocarParaOriginal() {

        // Troca o ícone
        trocarPara(
            "com.mulheres.IconeOriginal"
        )

        // A partir de agora:
        // abrirá index11.html
        salvarConfiguracao(false)

        finish()
    }

    // ==========================================
    // FONTE
    // ==========================================

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

        if (view is ViewGroup) {

            for (
                i in 0 until view.childCount
            ) {

                aplicarFonte(
                    view.getChildAt(i)
                )
            }
        }
    }
}