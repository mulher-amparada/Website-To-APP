package com.mulheres

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class EscolherIconeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =====================================================
        // BARRAS TRANSPARENTES — SEM FULLSCREEN
        // =====================================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        // Mantém as barras visíveis
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
            R.layout.activity_escolher_icone
        )

        val raiz = findViewById<View>(
            android.R.id.content
        )

        aplicarFonte(raiz)

        // =====================================================
        // BOTÕES
        // =====================================================

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

        findViewById<Button>(R.id.btnIcone4)
            .setOnClickListener {
                trocarParaIcone4()
            }
    }

    // =========================================================
    // TROCAR ÍCONE
    // =========================================================

    private fun trocarPara(nomeAtivo: String) {

        val pm = packageManager

        val icons = listOf(
            "com.mulheres.IconeOriginal",
            "com.mulheres.Icone1",
            "com.mulheres.Icone2",
            "com.mulheres.Icone3",
            "com.mulheres.Icone4"
        )

        icons.forEach { nome ->

            val component = ComponentName(
                this,
                nome
            )

            val estado =
                if (nome == nomeAtivo) {

                    PackageManager
                        .COMPONENT_ENABLED_STATE_ENABLED

                } else {

                    PackageManager
                        .COMPONENT_ENABLED_STATE_DISABLED
                }

            pm.setComponentEnabledSetting(
                component,
                estado,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    // =========================================================
    // MANDAR MENSAGEM PARA A MAINACTIVITY
    // =========================================================

    private fun avisarJavaScript(pagina: String) {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.putExtra(
            "PAGINA_JS",
            pagina
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        startActivity(intent)

        finish()
    }

    // =========================================================
    // ÍCONE ORIGINAL
    // =========================================================

    private fun trocarParaOriginal() {

        trocarPara(
            "com.mulheres.IconeOriginal"
        )

        avisarJavaScript("index1")
    }

    // =========================================================
    // ÍCONE 1
    // =========================================================

    private fun trocarParaIcone1() {

        trocarPara(
            "com.mulheres.Icone1"
        )

        avisarJavaScript("index1")
    }

    // =========================================================
    // ÍCONE 2
    // =========================================================

    private fun trocarParaIcone2() {

        trocarPara(
            "com.mulheres.Icone2"
        )

        avisarJavaScript("index1")
    }

    // =========================================================
    // ÍCONE 3
    // =========================================================

    private fun trocarParaIcone3() {

        trocarPara(
            "com.mulheres.Icone3"
        )

        avisarJavaScript("index11")
    }

    // =========================================================
    // ÍCONE 4
    // =========================================================

    private fun trocarParaIcone4() {

        trocarPara(
            "com.mulheres.Icone4"
        )

        avisarJavaScript("index1")
    }

    // =========================================================
    // FONTE
    // =========================================================

    private fun aplicarFonte(view: View) {

        val fonte = Typeface.createFromAsset(
            assets,
            "font.ttf"
        )

        if (view is android.widget.TextView) {
            view.typeface = fonte
        }

        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                aplicarFonte(
                    view.getChildAt(i)
                )
            }
        }
    }
}