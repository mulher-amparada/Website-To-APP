package com.mulheres

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices

class LocalizacaoService : Service() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "localizacao",
                "Localização",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "localizacao")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Obtendo localização")
            .setContentText("Aguarde...")
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return START_NOT_STICKY
        }

        val client = LocationServices.getFusedLocationProviderClient(this)

        client.lastLocation.addOnSuccessListener { location ->

            if (location != null) {

                val link = "https://maps.google.com/?q=${location.latitude},${location.longitude}"

                val whatsapp = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage("com.whatsapp")
                    putExtra(Intent.EXTRA_TEXT, "Minha localização:\n$link")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                startActivity(whatsapp)
            }

            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}