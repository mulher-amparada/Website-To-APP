package com.mulheres

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView

class TiltBrightnessController(
    private val activity: Activity,
    private val sensorManager: SensorManager,
    private val webView: WebView
) : SensorEventListener {

    private var isDark = false

    private val gravitySensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private var enabled = false

    private var darkBrightness = 0.15f

    fun start() {
        if (enabled) return

        enabled = true

        gravitySensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun setDarkBrightness(value: Float) {
        darkBrightness = value.coerceIn(0f, 1f)
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (!enabled) return

        val z = event.values[2]

        activity.runOnUiThread {

            val brightness =
                ((z + 10f) / 20f)
                    .coerceIn(darkBrightness, 1f)

            setBrightness(brightness)

            if (z < -8f) {

                if (!isDark) {

                    isDark = true

                    enterFullscreen()

                    webView.evaluateJavascript(
                        """
                        document.body.style.visibility='hidden';
                        document.body.style.opacity='0';
                        """.trimIndent(),
                        null
                    )

                    webView.evaluateJavascript(
                        "window.dispatchEvent(new CustomEvent('tiltbrightness',{detail:'dark'}));",
                        null
                    )
                }
            }
        }
    }
    
    fun stop() {

    enabled = false

    sensorManager.unregisterListener(this)

    isDark = false

    activity.runOnUiThread {
        setBrightness(1f)
    }
}

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }

    private fun setBrightness(value: Float) {

        val params = activity.window.attributes

        if (params.screenBrightness != value) {
            params.screenBrightness = value
            activity.window.attributes = params
        }
    }

    private fun enterFullscreen() {

        activity.window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    class WebAppInterface(
        private val controller: TiltBrightnessController
    ) {

        @JavascriptInterface
        fun startTiltBrightness() {
            controller.start()
        }

        @JavascriptInterface
        fun setDarkBrightness(value: Float) {
            controller.setDarkBrightness(value)
        }
        
        @JavascriptInterface
fun stopTiltBrightness() {
    controller.stop()
}
    }
}