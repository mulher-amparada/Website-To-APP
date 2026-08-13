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

        setContentView(
            R.layout.activity_escolher_icone
        )

        val raiz = findViewById<View>(
            android.R.id.content
        )

        aplicarFonte(raiz)

        findViewById<Button>(
            R.id.btnIconeOriginal
        ).setOnClickListener {
            trocarParaOriginal()
        }

        findViewById<Button>(
            R.id.btnIcone1
        ).setOnClickListener {
            trocarParaIcone1()
        }

        findViewById<Button>(
            R.id.btnIcone2
        ).setOnClickListener {
            trocarParaIcone2()
        }

        findViewById<Button>(
            R.id.btnIcone3
        ).setOnClickListener {
            trocarParaIcone3()
        }
    }

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

    private fun abrirMainActivity() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        startActivity(intent)

        finish()
    }

    private fun trocarParaOriginal() {

        trocarPara(
            "com.mulheres.IconeOriginal"
        )

        salvarConfiguracao(false)

        // Volta para MainActivity
        // e mostra index11
        abrirMainActivity()
    }

    private fun trocarParaIcone1() {

        trocarPara(
            "com.mulheres.Icone1"
        )

        salvarConfiguracao(false)

        // Volta para MainActivity
        // e mostra index11
        abrirMainActivity()
    }

    private fun trocarParaIcone2() {

        trocarPara(
            "com.mulheres.Icone2"
        )

        salvarConfiguracao(false)

        // Volta para MainActivity
        // e mostra index11
        abrirMainActivity()
    }

    private fun trocarParaIcone3() {

        trocarPara(
            "com.mulheres.Icone3"
        )

        salvarConfiguracao(true)

        // Volta para MainActivity
        // e mostra index1
        abrirMainActivity()
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