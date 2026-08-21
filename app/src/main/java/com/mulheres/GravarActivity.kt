package com.mulheres

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

import java.io.File

class GravarActivity : AppCompatActivity() {

    private lateinit var btnRecord: ImageView
    private lateinit var list: LinearLayout

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var currentFile: String = ""
    private var recording = false


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // =====================================================
        // BARRAS TRANSPARENTES
        // =====================================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
            R.layout.activity_gravar
        )

        val raiz = findViewById<View>(
            android.R.id.content
        )

        aplicarFonte(raiz)

        // =====================================================
        // COMPONENTES
        // =====================================================

        btnRecord = findViewById(
            R.id.btnRecord
        )

        list = findViewById(
            R.id.list
        )

        checkPermission()

        // =====================================================
        // BOTÃO DE GRAVAÇÃO
        // =====================================================

        btnRecord.setOnClickListener {

            if (recording) {

                stopRecord()

            } else {

                startRecord()
            }
        }
    }


    // =========================================================
    // BOTÃO VOLTAR
    // =========================================================

    override fun onBackPressed() {

        finish()
    }


    // =========================================================
    // PERMISSÃO
    // =========================================================

    private fun checkPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),
                1
            )
        }
    }


    // =========================================================
    // INICIAR GRAVAÇÃO
    // =========================================================

    private fun startRecord() {

        try {

            val dir =
                getExternalFilesDir(null)
                    ?: return

            currentFile =
                "${dir.absolutePath}/rec_${System.currentTimeMillis()}.3gp"

            recorder =
                MediaRecorder().apply {

                    setAudioSource(
                        MediaRecorder.AudioSource.MIC
                    )

                    setOutputFormat(
                        MediaRecorder.OutputFormat.THREE_GPP
                    )

                    setAudioEncoder(
                        MediaRecorder.AudioEncoder.AMR_NB
                    )

                    setOutputFile(
                        currentFile
                    )

                    prepare()

                    start()
                }

            recording = true

            btnRecord.setImageResource(
                R.drawable.mic1
            )

        } catch (e: Exception) {

            e.printStackTrace()

            recorder?.release()
            recorder = null

            recording = false

            btnRecord.setImageResource(
                R.drawable.mic
            )
        }
    }


    // =========================================================
    // PARAR GRAVAÇÃO
    // =========================================================

    private fun stopRecord() {

        try {

            recorder?.apply {

                stop()

                release()
            }

            recorder = null

            recording = false

            btnRecord.setImageResource(
                R.drawable.mic
            )

            addToList(
                currentFile
            )

        } catch (e: Exception) {

            e.printStackTrace()

            recorder?.release()

            recorder = null

            recording = false

            btnRecord.setImageResource(
                R.drawable.mic
            )
        }
    }


    // =========================================================
    // REPRODUZIR ÁUDIO
    // =========================================================

    private fun playAudio(
        path: String
    ) {

        try {

            player?.release()

            player =
                MediaPlayer().apply {

                    setDataSource(path)

                    prepare()

                    start()
                }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }


    // =========================================================
    // ADICIONAR GRAVAÇÃO À LISTA
    // =========================================================

    private fun addToList(
        path: String
    ) {

        val file = File(path)

        if (!file.exists()) {
            return
        }


        // =====================================================
        // CONTAINER
        // =====================================================

        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.HORIZONTAL

        container.setPadding(
            28,
            28,
            28,
            28
        )

        container.setBackgroundResource(
            R.drawable.bg_record_item
        )


        val params =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        params.setMargins(
            0,
            0,
            0,
            20
        )

        container.layoutParams =
            params


        // =====================================================
        // ÍCONE
        // =====================================================

        val icon =
            ImageView(this)

        icon.setImageResource(
            R.drawable.ic_audio
        )

        icon.layoutParams =
            LinearLayout.LayoutParams(
                60,
                60
            )


        // =====================================================
        // NOME
        // =====================================================

        val text =
            TextView(this)

        text.text =
            file.name

        text.textSize =
            16f

        text.setTextColor(
            Color.WHITE
        )

        text.setPadding(
            24,
            0,
            24,
            0
        )

        text.typeface =
            Typeface.createFromAsset(
                assets,
                "font.ttf"
            )

        text.layoutParams =
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )


        // =====================================================
        // EXCLUIR
        // =====================================================

        val delete =
            ImageView(this)

        delete.setImageResource(
            R.drawable.ic_delete
        )

        delete.layoutParams =
            LinearLayout.LayoutParams(
                60,
                60
            )

        delete.setBackgroundColor(
            Color.TRANSPARENT
        )


        // =====================================================
        // ADICIONAR COMPONENTES
        // =====================================================

        container.addView(
            icon
        )

        container.addView(
            text
        )

        container.addView(
            delete
        )


        // =====================================================
        // REPRODUZIR
        // =====================================================

        text.setOnClickListener {

            playAudio(path)
        }

        icon.setOnClickListener {

            playAudio(path)
        }


        // =====================================================
        // EXCLUIR
        // =====================================================

        delete.setOnClickListener {

            list.removeView(
                container
            )

            file.delete()
        }


        // =====================================================
        // ADICIONAR À LISTA
        // =====================================================

        list.addView(
            container
        )
    }


    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        recorder?.release()
        recorder = null

        player?.release()
        player = null

        super.onDestroy()
    }


    // =========================================================
    // FONTE
    // =========================================================

    private fun aplicarFonte(
        view: View
    ) {

        val fonte =
            try {

                Typeface.createFromAsset(
                    assets,
                    "font.ttf"
                )

            } catch (e: Exception) {

                Typeface.DEFAULT
            }


        if (view is TextView) {

            view.typeface =
                fonte
        }


        if (view is ViewGroup) {

            for (
                i in 0 until view.childCount
            ) {

                aplicarFonte(
                    view.getChildAt(i)
                )
            }
        }
    }


    // =========================================================
    // RESULTADO DA PERMISSÃO
    // =========================================================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 1) {

            if (
                grantResults.isEmpty() ||
                grantResults[0] !=
                PackageManager.PERMISSION_GRANTED
            ) {

                finish()
            }
        }
    }
}