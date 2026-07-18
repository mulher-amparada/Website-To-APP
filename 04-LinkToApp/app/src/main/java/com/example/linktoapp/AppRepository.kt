package com.linktoapp.app;

import android.content.Context

class AppRepository(context: Context) {

    private val prefs =
        context.getSharedPreferences("apps_protegidos", Context.MODE_PRIVATE)

    fun proteger(packageName: String) {
        prefs.edit()
            .putBoolean(packageName, true)
            .apply()
    }

    fun remover(packageName: String) {
        prefs.edit()
            .remove(packageName)
            .apply()
    }

    fun protegido(packageName: String): Boolean {
        return prefs.getBoolean(packageName, false)
    }

    fun todos(): Set<String> {
        return prefs.all.keys
    }
}