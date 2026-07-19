package com.mulheres

import android.os.Build
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.io.File
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GravarActivity : AppCompatActivity() {

    private lateinit var btnRecord: ImageView
    private lateinit var list: LinearLayout

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var currentFile: String = ""
    private var recording = false


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

WindowCompat.setDecorFitsSystemWindows(window, false)  

ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->  
    insets.getInsets(WindowInsetsCompat.Type.systemBars())  
    view.setPadding(0, 0, 0, 0)  
    insets  
}  

        setContentView(R.layout.activity_gravar)

val raiz = findViewById<View>(
    android.R.id.content
)

aplicarFonte(raiz)

        btnRecord = findViewById(R.id.btnRecord)
        list = findViewById(R.id.list)

        checkPermission()

        btnRecord.setOnClickListener {

            if (recording) {
                stopRecord()
            } else {
                startRecord()
            }

        }
    }


    private fun checkPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }


    private fun startRecord() {

        try {

            val dir = getExternalFilesDir(null)
                ?: return

            currentFile =
                "${dir.absolutePath}/rec_${System.currentTimeMillis()}.3gp"


            recorder = MediaRecorder().apply {

                setAudioSource(
                    MediaRecorder.AudioSource.MIC
                )

                setOutputFormat(
                    MediaRecorder.OutputFormat.THREE_GPP
                )

                setAudioEncoder(
                    MediaRecorder.AudioEncoder.AMR_NB
                )

                setOutputFile(currentFile)

                prepare()
                start()
            }


            recording = true

            btnRecord.setImageResource(
                R.drawable.mic1
            )


        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


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


            addToList(currentFile)


        } catch (e: Exception) {

            e.printStackTrace()

            recorder?.release()
            recorder = null
            recording = false
        }
    }


    private fun playAudio(path: String) {

        try {

            player?.release()

            player = MediaPlayer().apply {

                setDataSource(path)

                prepare()

                start()
            }


        } catch (e: Exception) {

            e.printStackTrace()

        }
    }


    private fun addToList(path: String) {

        val file = File(path)

        if (!file.exists())
            return


        val container = LinearLayout(this)

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

        container.layoutParams = params


        val icon = ImageView(this)

icon.setImageResource(
    R.drawable.ic_audio
)

icon.layoutParams =
    LinearLayout.LayoutParams(
        60,
        60
    )
    
    val text = TextView(this)

text.text = file.name
text.textSize = 16f
text.setTextColor(Color.WHITE)
text.setPadding(24, 0, 24, 0)

        text.setTextColor(Color.WHITE)

        text.layoutParams =
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )


        val delete = ImageView(this)

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

        


        container.addView(icon)
container.addView(text)
container.addView(delete)


        text.setOnClickListener {

            playAudio(path)

        }


        delete.setOnClickListener {

            container.animate()
                .alpha(0f)
                .translationX(80f)
                .setDuration(200)
                .withEndAction {

                    list.removeView(container)
                    file.delete()

                }
                .start()

        }


        list.addView(container)


        container.alpha = 0f
        container.scaleX = 0.95f
        container.scaleY = 0.95f
        container.translationY = 40f


        container.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(
                DecelerateInterpolator()
            )
            .start()
    }


    override fun onDestroy() {

        super.onDestroy()

        recorder?.release()

        player?.release()
    }

private fun aplicarFonte(view: View) {

    val fonte = resources.assets
        .open("font.ttf")
        .let {
            android.graphics.Typeface.createFromAsset(
                assets,
                "font.ttf"
            )
        }

    if (view is android.widget.TextView) {
        view.typeface = fonte
    }

    if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            aplicarFonte(view.getChildAt(i))
        }
    }
}

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


        if(requestCode == 1) {

            if(
                grantResults.isEmpty() ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED
            ) {

                finish()

            }
        }
    }
}