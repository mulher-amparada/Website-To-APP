package com.mulheres

import android.content.Intent
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

        // Tela fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }

        setContentView(R.layout.activity_escolher_icone)

        val raiz = findViewById<View>(android.R.id.content)

        aplicarFonte(raiz)

        findViewById<Button>(R.id.btnIconeOriginal)
            .setOnClickListener {
                trocarParaOriginal()
            }

        findViewById<Button>(R.id.btnIcone1)
            .setOnClickListener {
                trocarParaIcone1()
            }

        findViewById<Button>(R.id.btnIcone2)
            .setOnClickListener {
                trocarParaIcone2()
            }

        findViewById<Button>(R.id.btnIcone3)
            .setOnClickListener {
                trocarParaIcone3()
            }
    }

    private fun trocarPara(nomeAtivo: String) {

        val pm = packageManager

        val icons = listOf(
            "com.mulheres.IconeOriginal",
            "com.mulheres.Icone1",
            "com.mulheres.Icone2",
            "com.mulheres.Icone3"
        )

        icons.forEach { nome ->

            val component = android.content.ComponentName(
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
                android.content.pm.PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun trocarParaIcone1() {

        trocarPara("com.mulheres.Icone1")

        finish()
    }

    private fun trocarParaIcone2() {

        trocarPara("com.mulheres.Icone2")

        finish()
    }

    private fun trocarParaIcone3() {

        // 1. Troca para o ícone 3
        trocarPara("com.mulheres.Icone3")

        // 2. Abre a MainActivity
        val intent = Intent(
            this,
            MainActivity::class.java
        )

        // 3. Manda a mensagem
        intent.putExtra(
            "INICIAR_INDEX1",
            true
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        startActivity(intent)

        // 4. Fecha o seletor
        finish()
    }

    private fun trocarParaOriginal() {

        trocarPara("com.mulheres.IconeOriginal")

        finish()
    }

    private fun aplicarFonte(view: View) {

        val fonte = android.graphics.Typeface.createFromAsset(
            assets,
            "font.ttf"
        )

        if (view is android.widget.TextView) {
            view.typeface = fonte
        }

        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {
                aplicarFonte(view.getChildAt(i))
            }
        }
    }
}