package com.linktoapp.app;

object UnlockManager {

    private val desbloqueados = HashSet<String>()

    fun desbloquear(packageName: String) {
        synchronized(desbloqueados) {
            desbloqueados.add(packageName)
        }
    }

    fun bloquear(packageName: String) {
        synchronized(desbloqueados) {
            desbloqueados.remove(packageName)
        }
    }

    fun bloqueado(packageName: String): Boolean {
        synchronized(desbloqueados) {
            return !desbloqueados.contains(packageName)
        }
    }

    fun limpar() {
        synchronized(desbloqueados) {
            desbloqueados.clear()
        }
    }
}