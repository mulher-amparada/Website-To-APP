package com.mulheres

import android.content.Context
import android.content.SharedPreferences
import android.webkit.JavascriptInterface

class Cripto(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dados_seguro", Context.MODE_PRIVATE)

    @JavascriptInterface
    fun salvar(chave: String, valor: String) {
        prefs.edit()
            .putString(chave, valor)
            .apply()
    }

    @JavascriptInterface
    fun carregar(chave: String): String {
        return prefs.getString(chave, "") ?: ""
    }

    @JavascriptInterface
    fun remover(chave: String) {
        prefs.edit()
            .remove(chave)
            .apply()
    }

    @JavascriptInterface
    fun limparTudo() {
        prefs.edit()
            .clear()
            .apply()
    }
}