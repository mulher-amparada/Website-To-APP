package com.mulheres

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
    private var normalBrightness = -1f

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

    fun stop() {
        enabled = false
        sensorManager.unregisterListener(this)

        restoreBrightness()

        if (isDark) {
            isDark = false

            activity.runOnUiThread {
                webView.evaluateJavascript(
                    """
                    document.body.style.visibility='visible';
                    document.body.style.opacity='1';
                    """.trimIndent(),
                    null
                )
            }
        }
    }

    fun setDarkBrightness(value: Float) {
        darkBrightness = value.coerceIn(0f, 1f)
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (!enabled) return

        val z = event.values[2]

        activity.runOnUiThread {

            if (z < 5f) {

                setBrightness(darkBrightness)

                if (!isDark) {

                    isDark = true

                    webView.evaluateJavascript(
                        """
                        document.body.style.transition='opacity .5s';
                        document.body.style.opacity='0';

                        setTimeout(()=>{
                            document.body.style.visibility='hidden';
                        },500);
                        """.trimIndent(),
                        null
                    )
                }

            } else {

                restoreBrightness()

                if (isDark) {

                    isDark = false

                    webView.evaluateJavascript(
                        """
                        document.body.style.visibility='visible';
                        document.body.style.transition='opacity .5s';
                        document.body.style.opacity='1';
                        """.trimIndent(),
                        null
                    )
                }
            }
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

    private fun restoreBrightness() {

        val params = activity.window.attributes

        if (params.screenBrightness != normalBrightness) {
            params.screenBrightness = normalBrightness
            activity.window.attributes = params
        }
    }

    class WebAppInterface(
        private val controller: TiltBrightnessController
    ) {

        @JavascriptInterface
        fun startTiltBrightness() {
            controller.start()
        }

        @JavascriptInterface
        fun stopTiltBrightness() {
            controller.stop()
        }

        @JavascriptInterface
        fun setDarkBrightness(value: Float) {
            controller.setDarkBrightness(value)
        }
    }
}