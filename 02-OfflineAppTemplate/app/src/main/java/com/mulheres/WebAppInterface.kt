package com.mulheres

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.webkit.JavascriptInterface

class WebAppInterface(
    private val activity: Activity
) {

    private fun safeMainActivityCall(action: (MainActivity) -> Unit) {
        try {
            val act = activity
            if (act is MainActivity) {
                action(act)
            }
        } catch (_: Exception) {
        }
    }

    @JavascriptInterface
    fun solicitarAdministrador() {
        val component = ComponentName(
            activity,
            MyDeviceAdminReceiver::class.java
        )

        val intent = Intent("android.app.action.ADD_DEVICE_ADMIN")

        intent.putExtra(
            "android.app.extra.DEVICE_ADMIN",
            component as Parcelable
        )

        intent.putExtra(
            "android.app.extra.ADD_EXPLANATION",
            "Este aplicativo precisa da permissão de Administrador do dispositivo."
        )

        activity.startActivity(intent)
    }

    @JavascriptInterface
    fun bloquearTela() {
        val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE)
                as DevicePolicyManager

        val component = ComponentName(
            activity,
            MyDeviceAdminReceiver::class.java
        )

        if (dpm.isAdminActive(component)) {
            dpm.lockNow()
        }
    }

    @JavascriptInterface
    fun openFiles() {
        activity.startActivity(
            Intent(activity, FileActivity::class.java)
        )
    }

    @JavascriptInterface
    fun abrirEscolherIcone() {
        activity.startActivity(
            Intent(activity, EscolherIconeActivity::class.java)
        )
    }

    @JavascriptInterface
    fun salvar(chave: String, valor: String) {
        val prefs = activity.getSharedPreferences(
            "cripto",
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putString(chave, valor)
            .apply()
    }

    @JavascriptInterface
    fun carregar(chave: String): String {
        val prefs = activity.getSharedPreferences(
            "cripto",
            Context.MODE_PRIVATE
        )

        return prefs.getString(chave, "") ?: ""
    }

    @JavascriptInterface
    fun remover(chave: String) {
        val prefs = activity.getSharedPreferences(
            "cripto",
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .remove(chave)
            .apply()
    }

    @JavascriptInterface
    fun limparTudo() {
        val prefs = activity.getSharedPreferences(
            "cripto",
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .clear()
            .apply()
    }

    @JavascriptInterface
    fun openRecorder() {
        activity.startActivity(
            Intent(activity, GravarActivity::class.java)
        )
    }

    @JavascriptInterface
    fun abrirContatos() {
        safeMainActivityCall {
            it.abrirContatos()
        }
    }

    @JavascriptInterface
    fun selecionarContato() {
        safeMainActivityCall {
            it.abrirContatos()
        }
    }

    @JavascriptInterface
    fun ativarPalmas() {
        safeMainActivityCall {
            it.ativarPalmas()
        }
    }

    @JavascriptInterface
    fun desativarPalmas() {
        safeMainActivityCall {
            it.desativarPalmas()
        }
    }

    @JavascriptInterface
    fun ativarProtecao() {
        safeMainActivityCall {
            it.ativarProtecao()
        }
    }

    @JavascriptInterface
    fun desativarProtecao() {
        safeMainActivityCall {
            it.desativarProtecao()
        }
    }

    @JavascriptInterface
    fun enviarSOS() {
        safeMainActivityCall {
            it.enviarSOS()
        }
    }

    @JavascriptInterface
    fun iniciarBiometria() {
        safeMainActivityCall {
            it.iniciarBiometria()
        }
    }

    @JavascriptInterface
    fun iniciarBiometriaAmor() {
        safeMainActivityCall {
            it.iniciarBiometriaAmor()
        }
    }

    @JavascriptInterface
    fun iniciarBiometriaMusica() {
        safeMainActivityCall {
            it.iniciarBiometriaMusica()
        }
    }

    @JavascriptInterface
    fun iniciarBiometriaPrincesa() {
        safeMainActivityCall {
            it.iniciarBiometriaPrincesa()
        }
    }

    @JavascriptInterface
    fun iniciarBiometriaPrincipe() {
        safeMainActivityCall {
            it.iniciarBiometriaPrincipe()
        }
    }

    @JavascriptInterface
    fun pegarLocalizacao() {
        safeMainActivityCall {
            it.pegarLocalizacao()
        }
    }

    @JavascriptInterface
    fun salvarContatos(lista: String) {
        val prefs = activity.getSharedPreferences(
            "contatos",
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putString("lista", lista)
            .apply()
    }

    @JavascriptInterface
    fun ligarDireto(numero: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$numero")
        }

        activity.startActivity(intent)
    }
}