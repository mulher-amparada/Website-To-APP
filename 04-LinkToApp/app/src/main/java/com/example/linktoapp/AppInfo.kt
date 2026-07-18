package com.linktoapp.app

import android.graphics.drawable.Drawable

data class AppInfo(
    val nome: String,
    val pacote: String,
    val icone: Drawable,
    var protegido: Boolean
)