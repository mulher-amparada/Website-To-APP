package com.webviewtemplate.webviewtemplate1

import android.content.Context
import android.content.Intent
import android.widget.Toast

object Comandos {


    fun executar(
        contexto: Context,
        comando: String
    ) {


        val texto = comando.lowercase()


        if (texto.startsWith("abrir ")) {

            val nomeApp = texto
                .removePrefix("abrir ")
                .trim()


            abrirAplicativo(
                contexto,
                nomeApp
            )

            return
        }



        when {


            texto.contains("voltar") -> {

                Toast.makeText(
                    contexto,
                    "Voltando",
                    Toast.LENGTH_SHORT
                ).show()

            }


            else -> {

                Toast.makeText(
                    contexto,
                    "Comando não encontrado",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }



    private fun abrirAplicativo(
        contexto: Context,
        nome: String
    ) {


        val pm = contexto.packageManager


        val apps = pm.getInstalledApplications(0)


        for (app in apps) {


            val nomeApp = pm
                .getApplicationLabel(app)
                .toString()
                .lowercase()



            if (nomeApp.contains(nome)) {


                val intent =
                    pm.getLaunchIntentForPackage(
                        app.packageName
                    )


                if (intent != null) {

                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )


                    contexto.startActivity(intent)

                    return

                }

            }

        }


        Toast.makeText(
            contexto,
            "Aplicativo não encontrado",
            Toast.LENGTH_SHORT
        ).show()

    }

}