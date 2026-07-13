package com.webviewtemplate.webviewtemplate1

import android.Manifest
import android.content.Context
import android.app.admin.DevicePolicyManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.content.Context
import android.hardware.camera2.CameraManager
import com.google.android.gms.location.LocationServices
import android.hardware.camera2.CameraCharacteristics


object Comandos {


    fun executar(
        contexto: Context,
        comando: String
    ) {

        val texto = comando.lowercase()


        when {
        
        texto.startsWith("me mande uma notificação") -> {

    val mensagem =
        texto.removePrefix(
            "me mande uma notificação"
        )
        .trim()


    enviarNotificacao(
        contexto,
        mensagem
    )

}

texto.contains("enviar minha localização para o 180") ||
texto.contains("mandar minha localização para o 180") -> {

    enviarLocalWhatsApp180(contexto)

}

texto.contains("quais são os meus apoios") ||
texto.contains("quais sao os meus apoios") ||
texto.contains("meus apoios") -> {

    enviarApoios(contexto)

}

texto.contains("bloquear celular") ||
texto.contains("bloquear aparelho") -> {

    bloquearCelular(contexto)

}

texto.contains("vibrar") -> {

    vibrar(contexto)

}

texto.contains("ligar lanterna") -> {

    ligarLanterna(contexto)

}


texto.contains("desligar lanterna") -> {

    desligarLanterna(contexto)

}

            texto.contains("ligue para 180") ||
            texto.contains("ligue para o 180") ||
            texto.contains("ligar para o 180") ||
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
    
    private fun ligarServico(
    contexto: Context,
    numero: String
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
            "tel:$numero"
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

private fun bloquearCelular(contexto: Context) {

    val dpm = contexto.getSystemService(
        Context.DEVICE_POLICY_SERVICE
    ) as DevicePolicyManager


    val admin = ComponentName(
        contexto,
        MeuAdministrador::class.java
    )


    if (dpm.isAdminActive(admin)) {

        dpm.lockNow()

    }

}

private fun bloquearCelular(contexto: Context) {

    val dpm = contexto.getSystemService(
        Context.DEVICE_POLICY_SERVICE
    ) as DevicePolicyManager


    val admin = ComponentName(
        contexto,
        MeuAdministrador::class.java
    )


    if (dpm.isAdminActive(admin)) {

        Toast.makeText(
            contexto,
            "Bloqueando celular",
            Toast.LENGTH_SHORT
        ).show()


        dpm.lockNow()

    } else {

        Toast.makeText(
            contexto,
            "Administrador do dispositivo não ativado",
            Toast.LENGTH_SHORT
        ).show()

    }

}

private var cameraIdLanterna: String? = null


private fun encontrarCameraComFlash(
    contexto: Context
): String? {


    val cameraManager =
        contexto.getSystemService(
            Context.CAMERA_SERVICE
        ) as CameraManager


    for (cameraId in cameraManager.cameraIdList) {


        val caracteristicas =
            cameraManager.getCameraCharacteristics(
                cameraId
            )


        val temFlash =
            caracteristicas.get(
                CameraCharacteristics.FLASH_INFO_AVAILABLE
            ) ?: false


        if (temFlash) {

            return cameraId

        }

    }


    return null
}



fun ligarLanterna(
    contexto: Context
) {


    val cameraManager =
        contexto.getSystemService(
            Context.CAMERA_SERVICE
        ) as CameraManager


    cameraIdLanterna =
        encontrarCameraComFlash(contexto)


    if (cameraIdLanterna != null) {


        cameraManager.setTorchMode(
            cameraIdLanterna!!,
            true
        )

    }

}



fun desligarLanterna(
    contexto: Context
) {


    val cameraManager =
        contexto.getSystemService(
            Context.CAMERA_SERVICE
        ) as CameraManager


    cameraIdLanterna?.let {


        cameraManager.setTorchMode(
            it,
            false
        )

    }

}

fun vibrar(
    contexto: Context
) {

    val vibrator =
        contexto.getSystemService(
            Context.VIBRATOR_SERVICE
        ) as Vibrator


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        vibrator.vibrate(
            VibrationEffect.createOneShot(
                500,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )

    } else {

        vibrator.vibrate(500)

    }

}

private fun enviarNotificacao(
    contexto: Context,
    mensagem: String
) {

    val canalId = "assistente"


    val notificationManager =
        contexto.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as android.app.NotificationManager



    if (android.os.Build.VERSION.SDK_INT >= 26) {

        val canal = android.app.NotificationChannel(
            canalId,
            "Assistente",
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(
            canal
        )

    }



    val notificacao =
        androidx.core.app.NotificationCompat.Builder(
            contexto,
            canalId
        )
            .setSmallIcon(
                R.mipmap.ic_launcher
            )
            .setContentTitle(
                "Assistente"
            )
            .setContentText(
                mensagem
            )
            .setPriority(
                androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
            )
            .build()



    notificationManager.notify(
        1,
        notificacao
    )

}

private fun enviarLocalWhatsApp180(
    contexto: Context
) {

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(
            contexto
        )


    if (
        ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        Toast.makeText(
            contexto,
            "Permissão de localização negada",
            Toast.LENGTH_SHORT
        ).show()

        return
    }


    fusedLocationClient.lastLocation
        .addOnSuccessListener { local ->


            if (local != null) {


                val mensagem =
                    "Preciso de ajuda. Minha localização:\n" +
                    "https://maps.google.com/?q=${local.latitude},${local.longitude}"


                val uri = Uri.parse(
                    "https://wa.me/556196100180?text=" +
                            Uri.encode(mensagem)
                )


                val intent = Intent(
                    Intent.ACTION_VIEW,
                    uri
                )


                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )


                contexto.startActivity(intent)


            } else {


                Toast.makeText(
                    contexto,
                    "Não foi possível obter localização",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

}

private fun enviarApoios(
    contexto: Context
) {

    val mensagem = """
190 - Polícia Militar (emergências policiais)
191 - Polícia Rodoviária Federal (emergências em rodovias federais)
192 - SAMU (atendimento médico de urgência)
193 - Corpo de Bombeiros (incêndios e resgates)
180 - Central de Atendimento à Mulher
181 - Disque Denúncia (denúncias)
156 - Serviços públicos municipais (varia conforme a cidade)
188 - CVV (apoio emocional)
190, 191, 192, 193 - Serviços de emergência
""".trimIndent()


    val canalId = "apoios_assistente"


    val notificationManager =
        contexto.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as android.app.NotificationManager



    if (android.os.Build.VERSION.SDK_INT >= 26) {

        val canal = android.app.NotificationChannel(
            canalId,
            "Apoios",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(
            canal
        )

    }



    val notificacao =
        androidx.core.app.NotificationCompat.Builder(
            contexto,
            canalId
        )
            .setSmallIcon(
                R.mipmap.ic_launcher
            )
            .setContentTitle(
                "Seus apoios"
            )
            .setStyle(
                androidx.core.app.NotificationCompat.BigTextStyle()
                    .bigText(mensagem)
            )
            .setContentText(
                "Serviços de ajuda disponíveis"
            )
            .build()



    notificationManager.notify(
        10,
        notificacao
    )

}
}