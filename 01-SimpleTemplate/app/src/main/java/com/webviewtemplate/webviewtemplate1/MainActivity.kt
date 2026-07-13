package com.webviewtemplate.webviewtemplate1

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var edgeLight: View

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
                navigationBarDividerColor =
                    Color.TRANSPARENT
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


        edgeLight = View(this)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        params.setMargins(12, 12, 12, 12)

        root.addView(edgeLight, params)

        setContentView(root)

        iniciarEdgeLight()
    }


    private fun iniciarEdgeLight() {

        val borda = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)

            setStroke(
                10,
                Color.rgb(255, 0, 150)
            )

            cornerRadius = 50f
        }


        edgeLight.background = borda


        val animator = ValueAnimator.ofInt(80, 255)

        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = LinearInterpolator()


        animator.addUpdateListener {

            val alpha = it.animatedValue as Int

            borda.setStroke(
                10,
                Color.argb(
                    alpha,
                    255,
                    0,
                    150
                )
            )
        }

        animator.start()
    }
}