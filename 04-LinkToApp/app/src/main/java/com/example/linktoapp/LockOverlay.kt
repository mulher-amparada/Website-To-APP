package com.linktoapp.app

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager

class LockOverlay(
    private val context: Context
) {

    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var view: android.view.View? = null


    fun mostrar() {

        if (view != null)
            return


        view = LayoutInflater.from(context)
            .inflate(
                R.layout.activity_lock,
                null
            )


        val params = WindowManager.LayoutParams(

            WindowManager.LayoutParams.MATCH_PARENT,

            WindowManager.LayoutParams.MATCH_PARENT,

            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,

            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,

            PixelFormat.TRANSLUCENT
        )


        params.gravity = Gravity.CENTER


        windowManager.addView(
            view,
            params
        )
    }


    fun remover() {

        if (view != null) {

            windowManager.removeView(view)

            view = null
        }
    }
}