package com.linktoapp.app

object LockState {

    @Volatile
    var pacoteBloqueado = ""

    @Volatile
    var pacoteDesbloqueado = ""

    @Volatile
    var bloqueando = false

    @Volatile
    var voltarPressionado = false
}