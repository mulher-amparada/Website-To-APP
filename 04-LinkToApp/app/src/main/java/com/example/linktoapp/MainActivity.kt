package com.linktoapp.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {


    private val fonte by lazy {

        Typeface.createFromAsset(
            assets,
            "font.ttf"
        )

    }



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


            statusBarColor =
                Color.TRANSPARENT


            navigationBarColor =
                Color.TRANSPARENT



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                navigationBarDividerColor =
                    Color.TRANSPARENT

            }



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                isStatusBarContrastEnforced =
                    false

                isNavigationBarContrastEnforced =
                    false

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


            isAppearanceLightStatusBars =
                false


            isAppearanceLightNavigationBars =
                false

        }



        setContentView(
            R.layout.activity_main
        )



        aplicarFonte(
            findViewById(
                android.R.id.content
            )
        )



        val botao = findViewById<Button>(
            R.id.btnAtivar
        )



        botao.setOnClickListener {

            ativarAcessibilidade()

        }

    }



    private fun ativarAcessibilidade() {


        val ativa = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        .orEmpty()
        .contains(packageName)



        if (!ativa) {


            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )


        }

    }




    private fun aplicarFonte(
        view: View
    ) {


        if (view is TextView) {

            view.typeface =
                fonte

        }



        if (view is ViewGroup) {


            for (i in 0 until view.childCount) {


                aplicarFonte(
                    view.getChildAt(i)
                )


            }

        }


    }


}