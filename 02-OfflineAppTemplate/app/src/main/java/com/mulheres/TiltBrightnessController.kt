package com.mulheres

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.webkit.JavascriptInterface

class TiltBrightnessController(
    private val activity: Activity,
    private val sensorManager: SensorManager
) : SensorEventListener {

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
            } else {
                restoreBrightness()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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