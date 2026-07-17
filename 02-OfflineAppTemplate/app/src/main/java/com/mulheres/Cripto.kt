package com.mulheres

import android.content.Context
import android.content.SharedPreferences
import android.webkit.JavascriptInterface
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class Cripto(context: Context) {

    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            "dados_seguro",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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