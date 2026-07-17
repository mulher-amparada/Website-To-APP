package com.mulheres

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_CODE = 100
        const val PICK_CONTACT = 1
    }


    private var acelerometro: Sensor? = null
    private var protecaoAtiva = false

    var destinoBiometria = 0

    private lateinit var locationClient: FusedLocationProviderClient

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeListener: SensorEventListener

    private var ultimoShake = 0L

    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }


        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0,0,0,0)
            insets
        }


        webView = findViewById(R.id.webview)

        webView.setBackgroundColor(Color.BLACK)
        webView.visibility = View.VISIBLE


        locationClient =
            LocationServices.getFusedLocationProviderClient(this)


        configurarWebView()


        val pagina = intent.getStringExtra("pagina")


        if (!pagina.isNullOrEmpty()) {
            webView.loadUrl(pagina)
        } else {
            webView.loadUrl(
                "file:///android_asset/user1/index1.html"
            )
        }


        if (!temPermissoes()) {
            pedirPermissoes()
        }
    }
    
    private fun configurarWebView() {

    webView.addJavascriptInterface(
        WebAppInterface(this),
        "Android"
    )

    webView.addJavascriptInterface(
        Cripto(this),
        "Cripto"
    )


    val settings = webView.settings

    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.mediaPlaybackRequiresUserGesture = false
    settings.setGeolocationEnabled(true)

    settings.allowFileAccess = true
    settings.allowContentAccess = true
    settings.allowFileAccessFromFileURLs = true
    settings.allowUniversalAccessFromFileURLs = true


    webView.webChromeClient =
        object : WebChromeClient() {


            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {

                callback?.invoke(
                    origin,
                    true,
                    false
                )
            }


            override fun onPermissionRequest(
                request: PermissionRequest
            ) {

                runOnUiThread {

                    if (
                        request.resources.contains(
                            PermissionRequest.RESOURCE_AUDIO_CAPTURE
                        )
                    ) {

                        request.grant(
                            arrayOf(
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE
                            )
                        )

                    } else {

                        request.deny()

                    }
                }
            }
        }



    webView.webViewClient =
        object : WebViewClient() {


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {


                val url =
                    request?.url?.toString()
                        ?: return false



                try {

                    if (url.startsWith("tel:")) {

                        startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse(url)
                            )
                        )

                        return true
                    }



                    if (url.startsWith("https://wa.me")) {

                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                            )
                        )

                        return true
                    }


                } catch (e: Exception) {

                    e.printStackTrace()

                }


                return false
            }



            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {

                super.onPageFinished(view,url)


                val js = """
                    (function(){

                    var style=document.createElement('style');

                    style.innerHTML=
                    `
                    @font-face{
                        font-family:'Quicksand';
                        src:url('file:///android_asset/font.ttf');
                    }

                    *{
                        font-family:'Quicksand' !important;
                    }
                    `;

                    document.head.appendChild(style);

                    })();
                """.trimIndent()


                view?.evaluateJavascript(
                    js,
                    null
                )


                // Só executa se existir
                view?.evaluateJavascript(
                    """
                    typeof mostrarConteudo === 'function'
                    ? mostrarConteudo()
                    : null
                    """.trimIndent(),
                    null
                )
            }
        }
}



private fun carregarWebView1() {

    webView.loadUrl(
        "file:///android_asset/user1/index1.html"
    )

}


private fun carregarWebView2() {

    webView.loadUrl(
        "file:///android_asset/user1/index1.html"
    )

}


private fun carregarWebView3() {

    webView.loadUrl(
        "file:///android_asset/user1/index1.html"
    )

}


private fun carregarWebView4() {

    webView.loadUrl(
        "file:///android_asset/user1/botao.html"
    )

}




private fun iniciarSensor() {


    if (::sensorManager.isInitialized) {
        return
    }


    sensorManager =
        getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager



    acelerometro =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )


    shakeListener =
        object : SensorEventListener {


            override fun onSensorChanged(
                event: SensorEvent
            ) {


                if (!protecaoAtiva) {
                    return
                }


                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]


                val aceleracao =
                    sqrt(
                        (
                        x*x +
                        y*y +
                        z*z
                        ).toDouble()
                    )



                if (aceleracao > 18) {


                    val agora =
                        System.currentTimeMillis()


                    if (
                        agora - ultimoShake > 4000
                    ) {

                        ultimoShake = agora

                        ligarDireto("180")
                    }

                }

            }



            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {

            }
        }



    acelerometro?.let {

        sensorManager.registerListener(
            shakeListener,
            it,
            SensorManager.SENSOR_DELAY_GAME
        )

    }

}



private fun pararSensor() {


    if (
        ::sensorManager.isInitialized &&
        ::shakeListener.isInitialized
    ) {

        sensorManager.unregisterListener(
            shakeListener
        )

    }


    protecaoAtiva = false
}



private fun pedirPermissoes() {


    ActivityCompat.requestPermissions(
        this,

        arrayOf(

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO

        ),

        PERMISSION_CODE
    )

}

private fun semInternet(): Boolean {

    val cm =
        getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager


    val network =
        cm.activeNetwork ?: return true


    val capabilities =
        cm.getNetworkCapabilities(network)
            ?: return true


    return !capabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_INTERNET
    )
}



private fun temPermissoes(): Boolean {

    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&

    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED &&

    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED &&

    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CALL_PHONE
    ) == PackageManager.PERMISSION_GRANTED &&

    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}



private fun temPermissaoMicrofone(): Boolean {

    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}



fun abrirContatos() {

    try {

        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )

        startActivityForResult(
            intent,
            PICK_CONTACT
        )

    } catch (e: Exception) {

        e.printStackTrace()

    }
}



fun ativarPalmas() {

    if (!temPermissaoMicrofone()) {

        Toast.makeText(
            this,
            "Permissão de microfone não concedida",
            Toast.LENGTH_SHORT
        ).show()

        return
    }


    try {

        startForegroundService(
            Intent(
                this,
                PalmaService::class.java
            )
        )

    } catch (e: Exception) {

        e.printStackTrace()

    }
}



fun ativarProtecao() {

    protecaoAtiva = true
    iniciarSensor()

}



fun desativarPalmas() {

    stopService(
        Intent(
            this,
            PalmaService::class.java
        )
    )


    Toast.makeText(
        this,
        "Proteção por palmas desativada",
        Toast.LENGTH_SHORT
    ).show()
}



fun desativarProtecao() {

    protecaoAtiva = false
    pararSensor()

}



fun enviarSOS() {


    if (
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        Toast.makeText(
            this,
            "Localização sem permissão",
            Toast.LENGTH_SHORT
        ).show()

        return
    }



    locationClient.lastLocation
        .addOnSuccessListener {

            location ->


            if (location != null) {


                val link =
                    "https://maps.google.com/?q=${location.latitude},${location.longitude}"


                abrirIntentSMS(
                    "🚨 SOCORRO! Estou aqui: $link"
                )

            }

        }

}




private fun abrirIntentSMS(
    mensagem: String
) {


    val lista =
        getSharedPreferences(
            "contatos",
            MODE_PRIVATE
        )
        .getString(
            "lista",
            ""
        )


    if (lista.isNullOrBlank()) {

        Toast.makeText(
            this,
            "Nenhum contato cadastrado",
            Toast.LENGTH_SHORT
        ).show()

        return
    }



    try {

        val intent =
            Intent(Intent.ACTION_SENDTO)


        intent.data =
            Uri.parse("smsto:$lista")


        intent.putExtra(
            "sms_body",
            mensagem
        )


        startActivity(intent)


    } catch (e: Exception) {

        e.printStackTrace()

    }

}




fun pegarLocalizacao() {


    if (
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }


    locationClient.lastLocation
        .addOnSuccessListener {


            location ->


            location?.let {


                webView.evaluateJavascript(
                    "receberLocalizacao(${it.latitude},${it.longitude})",
                    null
                )

            }

        }

}




fun ligarDireto(numero: String) {


    if (numero.isBlank()) {
        return
    }


    try {


        val intent =
            Intent(
                Intent.ACTION_DIAL
            )


        intent.data =
            Uri.parse(
                "tel:$numero"
            )


        startActivity(intent)



    } catch (e: Exception) {

        e.printStackTrace()

    }

}