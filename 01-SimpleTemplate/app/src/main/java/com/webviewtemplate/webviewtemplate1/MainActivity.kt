package com.webviewtemplate.webviewtemplate1

import android.Manifest
import android.app.Dialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

import android.graphics.Typeface

import android.view.ViewGroup




class MainActivity : AppCompatActivity() {


    private val REQUEST_PERMISSIONS = 100

    private lateinit var adminComponent: ComponentName

    private lateinit var speechRecognizer: SpeechRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )


        window.apply {

            addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT


            if (android.os.Build.VERSION.SDK_INT >= 28) {
                navigationBarDividerColor = Color.TRANSPARENT
            }


            if (android.os.Build.VERSION.SDK_INT >= 29) {
                isStatusBarContrastEnforced = false
                isNavigationBarContrastEnforced = false
            }


            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }


        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }


        val root = FrameLayout(this)
        root.setBackgroundColor(Color.BLACK)

        setContentView(root)
        
        

val raiz = findViewById<View>(
    android.R.id.content
)

aplicarFonte(raiz)


        pedirPermissoes()

pedirPermissaoNotificacao()

        abrirPopupVoz()

    }


    private fun pedirPermissoes() {

    val permissoes = arrayOf(

        Manifest.permission.CAMERA,
        
        Manifest.permission.READ_CALENDAR,
        
        
        
        Manifest.permission.WRITE_CALENDAR,

        Manifest.permission.POST_NOTIFICATIONS,

        Manifest.permission.RECORD_AUDIO,

        Manifest.permission.READ_CONTACTS,

        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        
        Manifest.permission.READ_MEDIA_IMAGES,
Manifest.permission.READ_MEDIA_VIDEO,
Manifest.permission.READ_MEDIA_AUDIO,

Manifest.permission.BLUETOOTH_SCAN,
Manifest.permission.BLUETOOTH_CONNECT,
Manifest.permission.BLUETOOTH_ADVERTISE,

        Manifest.permission.READ_PHONE_STATE,

Manifest.permission.READ_CALL_LOG,

        Manifest.permission.CALL_PHONE

    )


    val faltando = permissoes.filter {

        ContextCompat.checkSelfPermission(
            this,
            it
        ) != PackageManager.PERMISSION_GRANTED

    }


    if (faltando.isNotEmpty()) {

        ActivityCompat.requestPermissions(
            this,
            faltando.toTypedArray(),
            REQUEST_PERMISSIONS
        )

    } else {

        ativarAdministrador()

    }

}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

if (requestCode == REQUEST_NOTIFICACAO) {

    if (grantResults.isNotEmpty() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED
    ) {

        // notificações liberadas

    }

}

        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.all {
                    it == PackageManager.PERMISSION_GRANTED
                }) {

                ativarAdministrador()

            }

        }
    }


private fun aplicarFonte(view: View) {

    val fonte = Typeface.createFromAsset(
        assets,
        "font.ttf"
    )


    if (view is TextView) {

        view.typeface = fonte

    }


    if (view is ViewGroup) {

        for (i in 0 until view.childCount) {

            aplicarFonte(
                view.getChildAt(i)
            )

        }

    }

}


    private fun ativarAdministrador() {

        adminComponent = ComponentName(
            this,
            MeuAdministrador::class.java
        )


        val intent = Intent(
            DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN
        )


        intent.putExtra(
            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
            adminComponent
        )


        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Ative o administrador para liberar funções de segurança"
        )


        startActivity(intent)

    }



    private fun abrirPopupVoz() {


        val dialog = Dialog(this)


        val view = layoutInflater.inflate(
            R.layout.popup_voz,
            null
        )
        
        aplicarFonte(view)


        val texto = view.findViewById<TextView>(
            R.id.textoVoz
        )


        val botao = view.findViewById<Button>(
            R.id.botaoOuvir
        )


        dialog.setContentView(view)


        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )


        dialog.show()



        botao.setOnClickListener {

            iniciarReconhecimento(texto)

        }

    }



    private fun iniciarReconhecimento(
        texto: TextView
    ) {


        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this)



        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "pt-BR"
        )



        speechRecognizer.setRecognitionListener(

            object : RecognitionListener {


                override fun onResults(results: Bundle?) {


                    val resultado =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )


                    if (!resultado.isNullOrEmpty()) {

                        texto.text = resultado[0]

                        Comandos.executar(
    this@MainActivity,
    resultado[0]
)

                    }

                }



                override fun onError(error: Int) {

                    texto.text =
                        "Não foi possível reconhecer"

                }



                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}

            }

        )


        speechRecognizer.startListening(intent)

    }


private val REQUEST_NOTIFICACAO = 200


private fun pedirPermissaoNotificacao() {

    if (android.os.Build.VERSION.SDK_INT >= 33) {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                REQUEST_NOTIFICACAO
            )

        }

    }

}
    


}