package com.mulheres

import android.app.Activity
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.Collator
import java.util.Locale

class FileActivity : AppCompatActivity() {

    companion object {
        const val MANAGE_STORAGE_CODE = 101
        const val PERMISSION_CODE = 100
    }

    private lateinit var adapter: FolderAdapter
    private val history = ArrayList<File>()
    private var index = -1
    private lateinit var recycler: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(
            window,
            window.decorView
        )

        controller.show(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        window.statusBarColor = 0
        window.navigationBarColor = 0

        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }


        setContentView(R.layout.activity_file)

val raiz = findViewById<View>(
    android.R.id.content
)

aplicarFonte(raiz)


        recycler = findViewById(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(this)


        adapter = FolderAdapter { file ->
            openFile(file)
        }


        recycler.adapter = adapter


        findViewById<View>(R.id.ic_back).setOnClickListener {
            voltar()
        }


        findViewById<View>(R.id.ic_forward).setOnClickListener {
            avancar()
        }


        if (!temPermissao()) {
            pedirPermissao()
        } else {
            iniciar()
        }
    }


    private fun iniciar() {
        val root = Environment.getExternalStorageDirectory()
        println(root.absolutePath)

        openFile(root)
    }


    private fun openFile(file: File) {

        if (index < history.size - 1) {
            history.subList(index + 1, history.size).clear()
        }

        history.add(file)
        index++


        if (file.isDirectory) {

            val list = file.listFiles()?.toList()
                ?: emptyList()


            val collator = Collator.getInstance(
                Locale("pt", "BR")
            )


            val sorted = list.sortedWith(
                Comparator { a, b ->

                    if (a.isDirectory && !b.isDirectory)
                        return@Comparator -1

                    if (!a.isDirectory && b.isDirectory)
                        return@Comparator 1


                    collator.compare(
                        a.name,
                        b.name
                    )
                }
            )


            adapter.update(sorted)

        } else {
            openExternal(file)
        }
    }


    private fun openExternal(file: File) {

        try {

            val uri = androidx.core.content.FileProvider
                .getUriForFile(
                    this,
                    "$packageName.provider",
                    file
                )


            val ext = file.extension.lowercase(Locale.ROOT)

            val mime = MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(ext)
                ?: "*/*"


            val intent = Intent(
                Intent.ACTION_VIEW
            ).apply {

                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            }


            startActivity(
                Intent.createChooser(
                    intent,
                    "Abrir com"
                )
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun avancar() {

        if (index < history.size - 1) {

            index++

            val file = history[index]

            if (file.isDirectory) {

                adapter.update(
                    file.listFiles()?.toList()
                        ?: emptyList()
                )
            }
        }
    }



    private fun voltar() {

        if (index > 0) {

            index--

            val file = history[index]

            if (file.isDirectory) {

                adapter.update(
                    file.listFiles()?.toList()
                        ?: emptyList()
                )
            }
        }
    }



    private fun pedirPermissao() {

        if (Build.VERSION.SDK_INT >= 30) {

            if (!Environment.isExternalStorageManager()) {

                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse(
                        "package:$packageName"
                    )
                )

                startActivityForResult(
                    intent,
                    MANAGE_STORAGE_CODE
                )

            } else {

                iniciar()
            }


        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_CODE
            )
        }
    }



    private fun temPermissao(): Boolean {

        return if (Build.VERSION.SDK_INT >= 30) {

            Environment.isExternalStorageManager()

        } else {

            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }



    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )


        if (requestCode == MANAGE_STORAGE_CODE) {

            if (temPermissao()) {
                iniciar()
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


        if (requestCode == PERMISSION_CODE) {

            if (grantResults.isNotEmpty() &&
                grantResults[0] ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {

                iniciar()
            }
        }
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

    override fun onBackPressed() {

        if (index > 0) {
            voltar()
        } else {
            super.onBackPressed()
        }
    }
}