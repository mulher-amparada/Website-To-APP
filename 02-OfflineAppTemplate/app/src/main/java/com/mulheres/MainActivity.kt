package com.mulheres

import com.mulheres.WebAppInterface
import com.mulheres.Cripto
import com.mulheres.PalmaService
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.telephony.SubscriptionManager
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
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
    var destinoBiometria: Int = 0

    private lateinit var locationClient: FusedLocationProviderClient
    private var protecaoAtiva = false

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeListener: SensorEventListener

    private var ultimoShake: Long = 0

    private lateinit var webView: WebView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false

    window.statusBarColor = Color.BLACK
    window.navigationBarColor = Color.BLACK

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isNavigationBarContrastEnforced = false
    }

    setContentView(R.layout.activity_main)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
        insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(0, 0, 0, 0)
        insets
    }

    webView = findViewById(R.id.webview)
    webView.setBackgroundColor(Color.BLACK)
    webView.visibility = View.VISIBLE

    locationClient = LocationServices.getFusedLocationProviderClient(this)

    configurarWebView()

    val pagina = intent.getStringExtra("pagina")

    if (!pagina.isNullOrEmpty()) {
        webView.loadUrl(pagina)
    } else {
        webView.loadUrl("file:///android_asset/user1/index1.html")
    }

    if (!temPermissoes()) {
        pedirPermissoes()
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == 100) {
        carregarWebView3()

        if (!temPermissoes()) {
            Toast.makeText(
                this,
                "Algumas permissões não foram concedidas.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

override fun onBackPressed() {
    if (webView.canGoBack()) {
        webView.goBack()
    } else {
        finish()
    }
}

private fun abrirIntentSMS(mensagem: String) {
    val prefs = getSharedPreferences("contatos", MODE_PRIVATE)
    val lista = prefs.getString("lista", "") ?: ""

    if (lista.trim().isEmpty()) {
        Toast.makeText(
            this,
            "Nenhum contato cadastrado",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:")
        putExtra("address", lista)
        putExtra("sms_body", mensagem)
    }

    startActivity(intent)
}

private fun abrirConfiguracoes() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
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
    settings.mediaPlaybackRequiresUserGesture = false
    settings.domStorageEnabled = true
    settings.setGeolocationEnabled(true)

    settings.allowFileAccess = true
    settings.allowContentAccess = true
    settings.javaScriptCanOpenWindowsAutomatically = true
    settings.allowFileAccessFromFileURLs = true
    settings.allowUniversalAccessFromFileURLs = true

    webView.webChromeClient = object : WebChromeClient() {

        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            callback?.invoke(origin, true, false)
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            runOnUiThread {
                val resources = request.resources

                if (resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    request.grant(
                        arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                    )
                } else {
                    request.deny()
                }
            }
        }
    }

    webView.webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            val url = request?.url.toString()

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

            return false
        }

        override fun onPageFinished(
            view: WebView?,
            url: String?
        ) {
            super.onPageFinished(view, url)

            if (semInternet()) {
                val js = """
                    (function() {

                        var style = document.createElement('style');

                        style.innerHTML = `
                            @font-face {
                                font-family: 'Quicksand';
                                src: url('file:///android_asset/font.ttf');
                            }

                            * {
                                font-family: 'Quicksand' !important;
                            }
                        `;

                        document.head.appendChild(style);

                    })();
                """.trimIndent()

                view?.evaluateJavascript(js, null)
            }

            view?.evaluateJavascript(
                "mostrarConteudo()",
                null
            )
        }
    }
}

private fun carregarWebView() {
    webView.loadUrl("file:///android_asset/user1/index1.html")
    webView.visibility = View.VISIBLE
}

private fun carregarWebView1() {
    webView.loadUrl("file:///android_asset/user1/index1.html")
    webView.visibility = View.VISIBLE
}

private fun carregarWebView2() {
    webView.loadUrl("file:///android_asset/user1/index1.html")
    webView.visibility = View.VISIBLE
}

private fun carregarWebView3() {
    webView.loadUrl("file:///android_asset/user1/index1.html")
    webView.visibility = View.VISIBLE
}

private fun carregarWebView4() {
    webView.loadUrl("file:///android_asset/user1/botao.html")
    webView.visibility = View.VISIBLE
}

private fun iniciarSensor() {

    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    shakeListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {

            if (!protecaoAtiva) return

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val aceleracao = sqrt(
                (x * x + y * y + z * z).toDouble()
            )

            if (aceleracao > 18.0) {

                val agora = System.currentTimeMillis()

                if (agora - ultimoShake > 4000) {
                    ultimoShake = agora
                    ligarDireto("180")
                }
            }
        }

        override fun onAccuracyChanged(
            sensor: Sensor?,
            accuracy: Int
        ) {
            // Nada
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
    
        sensorManager.unregisterListener(shakeListener)
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
        100
    )
}


private fun semInternet(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return true

    val capabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return true

    return !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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

private fun temPermissoesProtecao(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

fun abrirContatos() {
    val intent = Intent(
        Intent.ACTION_PICK,
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    )
    startActivityForResult(intent, 1)
}

@JavascriptInterface
fun ativarPalmas() {
    if (!temPermissoesProtecao()) {
        Toast.makeText(
            this,
            "Permissão de microfone não concedida",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val intent = Intent(this, PalmaService::class.java)
    startForegroundService(intent)
}

@JavascriptInterface
fun ativarProtecao() {
    protecaoAtiva = true
    iniciarSensor()
}

@JavascriptInterface
fun desativarPalmas() {
    stopService(Intent(this, PalmaService::class.java))

    Toast.makeText(
        this,
        "Proteção por palmas desativada",
        Toast.LENGTH_SHORT
    ).show()
}

@JavascriptInterface
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
        return
    }

    locationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude

            val link = "https://maps.google.com/?q=$lat,$lng"
            val mensagem = "🚨 SOCORRO! Estou aqui: $link"

            abrirIntentSMS(mensagem)
        }
    }
}

@JavascriptInterface
fun iniciarBiometria() {
    runOnUiThread {

        val biometricManager = BiometricManager.from(this)
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val canAuth = biometricManager.canAuthenticate(authenticators)

        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(
                this,
                "Biometria indisponível, mas abriremos o serviço pra você!",
                Toast.LENGTH_SHORT
            ).show()

            carregarWebView4()
            return@runOnUiThread
        }

        val biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    try {
                        val afd = assets.openFd("unlock.mp3")

                        val mediaPlayer = MediaPlayer().apply {
                            setDataSource(
                                afd.fileDescriptor,
                                afd.startOffset,
                                afd.length
                            )
                            prepare()
                            start()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    when (destinoBiometria) {
                        1 -> carregarWebView1()
                        2 -> carregarWebView2()
                        3 -> carregarWebView3()
                        else -> carregarWebView4()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        this@MainActivity,
                        "Biometria não disponível, recomendo ativar a biometria no seu aparelho, porém mesmo assim abriremos os seus acessos!",
                        Toast.LENGTH_SHORT
                    ).show()

                    carregarWebView4()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Desbloquear")
            .setDescription("Use biometria, PIN ou senha")
            .setAllowedAuthenticators(authenticators)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

fun getDestinoBiometria(): Int = destinoBiometria

@JavascriptInterface
fun iniciarBiometriaPrincesa() {
    destinoBiometria = 1
    iniciarBiometria()
}

@JavascriptInterface
fun iniciarBiometriaPrincipe() {
    destinoBiometria = 2
    iniciarBiometria()
}

@JavascriptInterface
fun iniciarBiometriaAmor() {
    destinoBiometria = 3
    iniciarBiometria()
}

@JavascriptInterface
fun iniciarBiometriaMusica() {
    destinoBiometria = 4
    iniciarBiometria()
}

@JavascriptInterface
fun ligarDireto(numero: String) {
    require(numero.isNotBlank())

    try {
        val subscriptionManager =
            getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        val temChip = !subscriptionManager.activeSubscriptionInfoList.isNullOrEmpty()

        val intent = if (temChip) {
            Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$numero")
            }
        } else {
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$numero")
            }
        }

        startActivity(intent)

    } catch (e: Exception) {
        e.printStackTrace()

        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$numero")
            }

            startActivity(intent)
        } catch (_: Exception) {
        }
    }
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 1 && resultCode == RESULT_OK) {

        val uri = data?.data ?: return

        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {

            val numeroIndex = cursor.getColumnIndex("data1")
            val nomeIndex = cursor.getColumnIndex("display_name")

            val numero = cursor.getString(numeroIndex)
                ?.replace(Regex("\\s"), "")
                ?.replace("-", "")
                ?: ""

            val nome = cursor.getString(nomeIndex) ?: "Contato"

            val prefs = getSharedPreferences("contatos", MODE_PRIVATE)

            val listaAtual = prefs.getString("lista", "") ?: ""
            val nomesAtual = prefs.getString("nomes", "") ?: ""

            val novaLista =
                if (listaAtual.isEmpty()) {
                    numero
                } else {
                    "$listaAtual,$numero"
                }

            val novosNomes =
                if (nomesAtual.isEmpty()) {
                    "$nome - $numero"
                } else {
                    "$nomesAtual\n$nome - $numero"
                }

            prefs.edit()
                .putString("lista", novaLista)
                .putString("nomes", novosNomes)
                .apply()

            cursor.close()

            Toast.makeText(
                this,
                "Contato salvo",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    locationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {

            val lat = location.latitude
            val lng = location.longitude

            val js = "javascript:receberLocalizacao($lat,$lng)"

            webView.evaluateJavascript(js, null)
        }
    }
}

fun setDestinoBiometria(value: Int) {
    destinoBiometria = value
}

}