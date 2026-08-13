package com.mulheres

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
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
    private lateinit var recycler: RecyclerView
    private lateinit var pathText: TextView
    private lateinit var itemCount: TextView


    private val history =
        ArrayList<File>()


    private var index = -1


    // =========================================================
    // CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        // =====================================================
        // TELA CHEIA
        // =====================================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )


        val controller =
            WindowInsetsControllerCompat(
                window,
                window.decorView
            )


        controller.hide(
            WindowInsetsCompat.Type.systemBars()
        )


        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        controller.isAppearanceLightStatusBars = false

        controller.isAppearanceLightNavigationBars = false


        window.statusBarColor =
            Color.TRANSPARENT


        window.navigationBarColor =
            Color.TRANSPARENT


        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q
        ) {

            window.isNavigationBarContrastEnforced =
                false
        }


        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { view, insets ->

            view.setPadding(
                0,
                0,
                0,
                0
            )

            insets
        }


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
            R.layout.activity_file
        )


        val raiz =
            findViewById<View>(
                android.R.id.content
            )


        aplicarFonte(raiz)


        // =====================================================
        // VIEWS
        // =====================================================

        recycler =
            findViewById(
                R.id.recycler
            )


        pathText =
            findViewById(
                R.id.pathText
            )


        itemCount =
            findViewById(
                R.id.itemCount
            )


        configurarRecycler()

        configurarBack()


        // =====================================================
        // PERMISSÃO
        // =====================================================

        if (!temPermissao()) {

            pedirPermissao()

        } else {

            iniciar()
        }
    }


    // =========================================================
    // RECYCLER
    // =========================================================

    private fun configurarRecycler() {

        recycler.layoutManager =
            LinearLayoutManager(this)


        adapter =
            FolderAdapter { file ->

                abrirArquivoOuPasta(file)
            }


        recycler.adapter =
            adapter
    }


    // =========================================================
    // BACK
    // =========================================================

    private fun configurarBack() {

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    /*
                     * Se estiver dentro de uma pasta,
                     * volta para a anterior.
                     */

                    if (index > 0) {

                        voltarDiretorio()

                    } else {

                        /*
                         * Se estiver na raiz,
                         * fecha a FileActivity.
                         */

                        finish()
                    }
                }
            }
        )
    }


    // =========================================================
    // INÍCIO
    // =========================================================

    private fun iniciar() {

        val root =
            Environment
                .getExternalStorageDirectory()


        abrirDiretorioInicial(root)
    }


    private fun abrirDiretorioInicial(
        file: File
    ) {

        history.clear()

        index = -1

        abrirDiretorio(file)
    }


    // =========================================================
    // ABRIR ARQUIVO OU PASTA
    // =========================================================

    private fun abrirArquivoOuPasta(
        file: File
    ) {

        if (file.isDirectory) {

            abrirDiretorio(file)

        } else {

            abrirExterno(file)
        }
    }


    // =========================================================
    // ABRIR DIRETÓRIO
    // =========================================================

    private fun abrirDiretorio(
        file: File
    ) {

        if (!file.isDirectory) {
            return
        }


        /*
         * Remove histórico futuro.
         */

        if (
            index <
            history.size - 1
        ) {

            history.subList(
                index + 1,
                history.size
            ).clear()
        }


        /*
         * Evita duplicar a pasta atual.
         */

        if (
            index >= 0 &&
            history[index].absolutePath ==
            file.absolutePath
        ) {

            atualizarLista(file)

            return
        }


        history.add(file)

        index =
            history.lastIndex


        atualizarLista(file)
    }


    // =========================================================
    // ATUALIZAR LISTA
    // =========================================================

    private fun atualizarLista(
        directory: File
    ) {

        val files =
            directory
                .listFiles()
                ?.toList()
                ?: emptyList()


        val collator =
            Collator.getInstance(
                Locale("pt", "BR")
            )


        /*
         * Pastas primeiro.
         * Arquivos depois.
         * Ambos em ordem alfabética.
         */

        val sorted =
            files.sortedWith(
                Comparator { a, b ->

                    if (
                        a.isDirectory &&
                        !b.isDirectory
                    ) {

                        return@Comparator -1
                    }


                    if (
                        !a.isDirectory &&
                        b.isDirectory
                    ) {

                        return@Comparator 1
                    }


                    collator.compare(
                        a.name,
                        b.name
                    )
                }
            )


        adapter.update(sorted)


        atualizarCaminho(
            directory
        )


        atualizarContador(
            sorted.size
        )
    }


    // =========================================================
    // CONTADOR
    // =========================================================

    private fun atualizarContador(
        quantidade: Int
    ) {

        itemCount.text =
            when (quantidade) {

                0 -> ""

                1 -> "1 item"

                else ->
                    "$quantidade itens"
            }
    }


    // =========================================================
    // CAMINHO
    // =========================================================

    private fun atualizarCaminho(
        directory: File
    ) {

        pathText.text =
            obterCaminhoBonito(
                directory
            )
    }


    private fun obterCaminhoBonito(
        directory: File
    ): String {

        val root =
            Environment
                .getExternalStorageDirectory()


        val rootPath =
            root.absolutePath


        val currentPath =
            directory.absolutePath


        if (
            currentPath ==
            rootPath
        ) {

            return "Armazenamento interno"
        }


        return if (
            currentPath.startsWith(
                rootPath
            )
        ) {

            val relativo =
                currentPath
                    .removePrefix(
                        rootPath
                    )
                    .trim('/')


            if (
                relativo.isEmpty()
            ) {

                "Armazenamento interno"

            } else {

                "Armazenamento interno / $relativo"
            }

        } else {

            directory.name
        }
    }


    // =========================================================
    // AVANÇAR
    // =========================================================

    private fun avancarDiretorio() {

        if (
            index >=
            history.size - 1
        ) {
            return
        }


        index++


        val directory =
            history[index]


        if (
            directory.isDirectory
        ) {

            atualizarLista(
                directory
            )
        }
    }


    // =========================================================
    // VOLTAR DIRETÓRIO
    // =========================================================

    private fun voltarDiretorio() {

        if (index <= 0) {

            finish()

            return
        }


        index--


        val directory =
            history[index]


        if (
            directory.isDirectory
        ) {

            atualizarLista(
                directory
            )
        }
    }


    // =========================================================
    // ABRIR ARQUIVO
    // =========================================================

    private fun abrirExterno(
        file: File
    ) {

        try {

            val uri =
                androidx.core.content.FileProvider
                    .getUriForFile(
                        this,
                        "$packageName.provider",
                        file
                    )


            val extensao =
                file.extension
                    .lowercase(
                        Locale.ROOT
                    )


            val mime =
                MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(
                        extensao
                    )
                    ?: "*/*"


            val intent =
                Intent(
                    Intent.ACTION_VIEW
                ).apply {

                    setDataAndType(
                        uri,
                        mime
                    )


                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
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


    // =========================================================
    // PERMISSÃO
    // =========================================================

    private fun pedirPermissao() {

        if (
            Build.VERSION.SDK_INT >= 30
        ) {

            if (
                !Environment
                    .isExternalStorageManager()
            ) {

                val intent =
                    Intent(
                        Settings
                            .ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
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
                    android.Manifest.permission
                        .READ_EXTERNAL_STORAGE
                ),
                PERMISSION_CODE
            )
        }
    }


    private fun temPermissao(): Boolean {

        return if (
            Build.VERSION.SDK_INT >= 30
        ) {

            Environment
                .isExternalStorageManager()

        } else {

            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission
                    .READ_EXTERNAL_STORAGE
            ) ==
                android.content.pm.PackageManager
                    .PERMISSION_GRANTED
        }
    }


    // =========================================================
    // RESULTADO DA PERMISSÃO
    // =========================================================

    @Deprecated(
        "Compatibilidade com versões antigas"
    )
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


        if (
            requestCode ==
            MANAGE_STORAGE_CODE
        ) {

            if (temPermissao()) {

                iniciar()

            } else {

                finish()
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


        if (
            requestCode ==
            PERMISSION_CODE
        ) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                android.content.pm.PackageManager
                    .PERMISSION_GRANTED
            ) {

                iniciar()

            } else {

                finish()
            }
        }
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
}