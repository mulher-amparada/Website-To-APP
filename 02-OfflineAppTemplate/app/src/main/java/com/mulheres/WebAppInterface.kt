package com.mulheres

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView

import org.json.JSONArray
import org.json.JSONObject

import java.io.ByteArrayOutputStream

class WebAppInterface(
    private val activity: Activity
) {

    /* =========================================================
       CHAMADA SEGURA PARA MAINACTIVITY
    ========================================================= */

    private fun chamarMainActivity(
        nomeMetodo: String,
        vararg argumentos: Any?
    ) {

        try {

            val metodo = MainActivity::class.java.methods.firstOrNull {
                it.name == nomeMetodo &&
                it.parameterTypes.size == argumentos.size
            }

            if (metodo == null) {
                android.util.Log.e(
                    "WebAppInterface",
                    "Método não encontrado: $nomeMetodo"
                )
                return
            }

            activity.runOnUiThread {

                try {

                    metodo.invoke(
                        activity,
                        *argumentos
                    )

                } catch (e: Exception) {

                    e.printStackTrace()

                    android.util.Log.e(
                        "WebAppInterface",
                        "Erro ao chamar $nomeMetodo",
                        e
                    )
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       USER 2
    ========================================================= */

    @JavascriptInterface
    fun trocarParaUser2() {

        activity.runOnUiThread {

            try {

                val webView =
                    activity.findViewById<WebView>(
                        R.id.webview
                    )

                webView?.evaluateJavascript(
                    "receberMensagemAndroid('user2')",
                    null
                )

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }


    /* =========================================================
       LISTAR APLICATIVOS
    ========================================================= */

    @JavascriptInterface
    fun obterApps(): String {

        return try {

            val pm =
                activity.packageManager

            val lista =
                JSONArray()

            pm.getInstalledApplications(
                PackageManager.GET_META_DATA
            )
                .sortedBy {

                    pm.getApplicationLabel(it)
                        .toString()
                        .lowercase()

                }
                .forEach { app ->

                    val pacote =
                        app.packageName.lowercase()

                    /*
                     * Oculta alguns pacotes internos.
                     */

                    if (
                        pacote.contains("systemui") ||
                        pacote.contains("knox")
                    ) {
                        return@forEach
                    }

                    try {

                        val nome =
                            pm.getApplicationLabel(app)
                                .toString()

                        val icone =
                            pm.getApplicationIcon(app)

                        lista.put(
                            JSONObject().apply {

                                put(
                                    "nome",
                                    nome
                                )

                                put(
                                    "pacote",
                                    app.packageName
                                )

                                put(
                                    "icone",
                                    drawableToBase64(
                                        icone
                                    )
                                )
                            }
                        )

                    } catch (_: Exception) {
                    }
                }

            lista.toString()

        } catch (e: Exception) {

            e.printStackTrace()

            "[]"
        }
    }


    /* =========================================================
       ABRIR APLICATIVO
    ========================================================= */

    @JavascriptInterface
    fun abrirApp(pacote: String) {

        try {

            if (pacote.isBlank()) {
                return
            }

            val intent =
                activity.packageManager
                    .getLaunchIntentForPackage(pacote)

            if (intent != null) {

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                activity.startActivity(intent)

            }

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       ÍCONE -> BASE64
    ========================================================= */

    private fun drawableToBase64(
        drawable: Drawable
    ): String {

        return try {

            val bitmap =
                if (drawable is BitmapDrawable) {

                    drawable.bitmap

                } else {

                    val largura =
                        drawable.intrinsicWidth
                            .coerceAtLeast(1)

                    val altura =
                        drawable.intrinsicHeight
                            .coerceAtLeast(1)

                    val bitmap =
                        Bitmap.createBitmap(
                            largura,
                            altura,
                            Bitmap.Config.ARGB_8888
                        )

                    val canvas =
                        Canvas(bitmap)

                    drawable.setBounds(
                        0,
                        0,
                        canvas.width,
                        canvas.height
                    )

                    drawable.draw(canvas)

                    bitmap
                }

            val stream =
                ByteArrayOutputStream()

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                stream
            )

            "data:image/png;base64," +
                    Base64.encodeToString(
                        stream.toByteArray(),
                        Base64.NO_WRAP
                    )

        } catch (e: Exception) {

            e.printStackTrace()

            ""
        }
    }


    /* =========================================================
       ASSIST
    ========================================================= */

    @JavascriptInterface
    fun abrirPorIntent() {

        try {

            val intent =
                Intent("com.assist.OPEN").apply {

                    setPackage(
                        "com.assist"
                    )
                }

            activity.startActivity(intent)

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       ADMINISTRADOR DO DISPOSITIVO
    ========================================================= */

    @JavascriptInterface
    fun solicitarAdministrador() {

        try {

            val component =
                ComponentName(
                    activity,
                    MyDeviceAdminReceiver::class.java
                )

            val intent =
                Intent(
                    DevicePolicyManager
                        .ACTION_ADD_DEVICE_ADMIN
                )

            intent.putExtra(
                DevicePolicyManager
                    .EXTRA_DEVICE_ADMIN,
                component
            )

            intent.putExtra(
                DevicePolicyManager
                    .EXTRA_ADD_EXPLANATION,
                "Este aplicativo precisa da permissão de Administrador do dispositivo."
            )

            activity.startActivity(intent)

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       GERENCIADOR
    ========================================================= */

    @JavascriptInterface
    fun abrirGerenciador() {

        try {

            val intent =
                Intent("com.gerenciar.OPEN").apply {

                    setPackage(
                        "com.gerenciar"
                    )
                }

            activity.startActivity(intent)

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       BLOQUEAR TELA
    ========================================================= */

    @JavascriptInterface
    fun bloquearTela() {

        try {

            val dpm =
                activity.getSystemService(
                    Context.DEVICE_POLICY_SERVICE
                ) as DevicePolicyManager

            val component =
                ComponentName(
                    activity,
                    MyDeviceAdminReceiver::class.java
                )

            if (
                dpm.isAdminActive(component)
            ) {

                dpm.lockNow()

            }

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       ARQUIVOS
    ========================================================= */

    @JavascriptInterface
    fun openFiles() {

        try {

            activity.startActivity(
                Intent(
                    activity,
                    FileActivity::class.java
                )
            )

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       ESCOLHER ÍCONE
    ========================================================= */

    @JavascriptInterface
    fun abrirEscolherIcones() {

        try {

            activity.startActivity(
                Intent(
                    activity,
                    EscolherIconeActivity::class.java
                )
            )

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       ARMAZENAMENTO
    ========================================================= */

    @JavascriptInterface
    fun salvar(
        chave: String,
        valor: String
    ) {

        val prefs =
            activity.getSharedPreferences(
                "cripto",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString(chave, valor)
            .apply()
    }


    @JavascriptInterface
    fun carregar(
        chave: String
    ): String {

        val prefs =
            activity.getSharedPreferences(
                "cripto",
                Context.MODE_PRIVATE
            )

        return prefs.getString(
            chave,
            ""
        ) ?: ""
    }


    @JavascriptInterface
    fun remover(
        chave: String
    ) {

        val prefs =
            activity.getSharedPreferences(
                "cripto",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .remove(chave)
            .apply()
    }


    @JavascriptInterface
    fun limparTudo() {

        val prefs =
            activity.getSharedPreferences(
                "cripto",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .clear()
            .apply()
    }


    /* =========================================================
       CONTATOS
    ========================================================= */

    @JavascriptInterface
    fun abrirContatos() {

        chamarMainActivity(
            "abrirContatos"
        )
    }


    @JavascriptInterface
    fun selecionarContato() {

        chamarMainActivity(
            "abrirContatos"
        )
    }


    /* =========================================================
       PROTEÇÃO POR PALMAS
    ========================================================= */

    @JavascriptInterface
    fun ativarPalmas() {

        chamarMainActivity(
            "ativarPalmas"
        )
    }


    @JavascriptInterface
    fun desativarPalmas() {

        chamarMainActivity(
            "desativarPalmas"
        )
    }


    /* =========================================================
       PROTEÇÃO POR MOVIMENTO
    ========================================================= */

    @JavascriptInterface
    fun ativarProtecao() {

        chamarMainActivity(
            "ativarProtecao"
        )
    }


    @JavascriptInterface
    fun desativarProtecao() {

        chamarMainActivity(
            "desativarProtecao"
        )
    }


    /* =========================================================
       SOS
    ========================================================= */

    @JavascriptInterface
    fun enviarSOS() {

        chamarMainActivity(
            "enviarSOS"
        )
    }


    /* =========================================================
       BIOMETRIA
    ========================================================= */

    @JavascriptInterface
    fun iniciarBiometria() {

        chamarMainActivity(
            "iniciarBiometria"
        )
    }


    @JavascriptInterface
    fun iniciarBiometriaAmor() {

        chamarMainActivity(
            "iniciarBiometriaAmor"
        )
    }


    @JavascriptInterface
    fun iniciarBiometriaMusica() {

        chamarMainActivity(
            "iniciarBiometriaMusica"
        )
    }


    @JavascriptInterface
    fun iniciarBiometriaPrincesa() {

        chamarMainActivity(
            "iniciarBiometriaPrincesa"
        )
    }


    @JavascriptInterface
    fun iniciarBiometriaPrincipe() {

        chamarMainActivity(
            "iniciarBiometriaPrincipe"
        )
    }


    /* =========================================================
       LOCALIZAÇÃO
    ========================================================= */

    @JavascriptInterface
    fun pegarLocalizacao() {

        chamarMainActivity(
            "pegarLocalizacao"
        )
    }


    /* =========================================================
       SALVAR CONTATOS
    ========================================================= */

    @JavascriptInterface
    fun salvarContatos(
        lista: String
    ) {

        val prefs =
            activity.getSharedPreferences(
                "contatos",
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString(
                "lista",
                lista
            )
            .apply()
    }


    /* =========================================================
       GRAVADOR
    ========================================================= */

    @JavascriptInterface
    fun openRecorder() {

        try {

            activity.startActivity(
                Intent(
                    activity,
                    GravarActivity::class.java
                )
            )

        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    /* =========================================================
       LIGAÇÃO DIRETA
    ========================================================= */

    @JavascriptInterface
    fun ligarDireto(
        numero: String
    ) {

        if (numero.isBlank()) {
            return
        }

        chamarMainActivity(
            "ligarDireto",
            numero
        )
    }
}