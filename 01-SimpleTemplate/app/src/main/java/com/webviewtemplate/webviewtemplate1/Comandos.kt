package com.webviewtemplate.webviewtemplate1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat

object Comandos {


    fun executar(
        contexto: Context,
        comando: String
    ) {

        val texto = comando.lowercase()


        when {


            texto.contains("ligue para 180") ||
            texto.contains("ligar para 180") -> {

                ligar180(contexto)

            }


            texto.startsWith("abrir ") -> {

                val nomeApp = texto
                    .removePrefix("abrir ")
                    .trim()


                abrirAplicativo(
                    contexto,
                    nomeApp
                )

            }


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



    private fun ligar180(
        contexto: Context
    ) {


        if (
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {


            val intent = Intent(
                Intent.ACTION_CALL
            )


            intent.data = Uri.parse(
                "tel:180"
            )


            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )


            contexto.startActivity(intent)


        } else {


            Toast.makeText(
                contexto,
                "Permissão de telefone não concedida",
                Toast.LENGTH_SHORT
            ).show()

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