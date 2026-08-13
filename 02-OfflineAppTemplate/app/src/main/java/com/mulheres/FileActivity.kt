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
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
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
    private lateinit var recycler: RecyclerView
    private lateinit var pathText: TextView
    private lateinit var searchBox: View

    private val history = ArrayList<File>()

    private var index = -1
    private var diretorioAtual: File? = null
    private var textoPesquisa = ""

    private enum class Filtro {
        TODOS,
        PASTAS,
        IMAGENS,
        DOCUMENTOS
    }

    private var filtroAtual = Filtro.TODOS


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configurarSistema()

        setContentView(R.layout.activity_file)

        aplicarFonte(
            findViewById(android.R.id.content)
        )

        recycler = findViewById(R.id.recycler)
        pathText = findViewById(R.id.pathText)
        searchBox = findViewById(R.id.searchBox)

        configurarRecycler()
        configurarPesquisa()
        configurarCategorias()
        configurarBack()

        if (!temPermissao()) {
            pedirPermissao()
        } else {
            iniciar()
        }
    }


    // =========================================================
    // SISTEMA / TELA CHEIA
    // =========================================================

    private fun configurarSistema() {

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

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }
    }


    // =========================================================
    // RECYCLER
    // =========================================================

    private fun configurarRecycler() {

        recycler.layoutManager =
            LinearLayoutManager(this)

        adapter = FolderAdapter { file ->

            abrirArquivoOuPasta(file)
        }

        recycler.adapter = adapter
    }


    // =========================================================
    // PESQUISA
    // =========================================================

    private fun configurarPesquisa() {

        searchBox.setOnClickListener {
            abrirPesquisa()
        }
    }


    private fun abrirPesquisa() {

        val campo = EditText(this)

        campo.setSingleLine(true)

        campo.hint = "Pesquisar arquivos"

        campo.setText(textoPesquisa)

        campo.setSelectAllOnFocus(true)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Pesquisar arquivos")
                .setView(campo)
                .setNegativeButton(
                    "Cancelar",
                    null
                )
                .setPositiveButton(
                    "Pesquisar"
                ) { _, _ ->

                    textoPesquisa =
                        campo.text
                            .toString()
                            .trim()

                    atualizarListaAtual()
                }
                .create()

        dialog.show()
    }


    // =========================================================
    // CATEGORIAS
    // =========================================================

    private fun configurarCategorias() {

        findViewById<View>(
            R.id.categoryFolders
        ).setOnClickListener {

            filtroAtual = Filtro.PASTAS

            textoPesquisa = ""

            atualizarListaAtual()
        }


        findViewById<View>(
            R.id.categoryImages
        ).setOnClickListener {

            filtroAtual = Filtro.IMAGENS

            textoPesquisa = ""

            atualizarListaAtual()
        }


        findViewById<View>(
            R.id.categoryDocuments
        ).setOnClickListener {

            filtroAtual = Filtro.DOCUMENTOS

            textoPesquisa = ""

            atualizarListaAtual()
        }
    }


    // =========================================================
    // BACK MODERNO
    // =========================================================

    private fun configurarBack() {

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (index > 0) {

                        voltarDiretorio()

                    } else {

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

        diretorioAtual = null

        filtroAtual = Filtro.TODOS

        textoPesquisa = ""

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


    private fun abrirDiretorio(
        file: File
    ) {

        if (!file.isDirectory) {
            return
        }


        // Se voltou e entrou em outro diretório,
        // remove o histórico futuro.

        if (index < history.size - 1) {

            history.subList(
                index + 1,
                history.size
            ).clear()
        }


        // Não duplica o diretório atual.

        if (
            index >= 0 &&
            history[index] == file
        ) {

            diretorioAtual = file

            atualizarLista(file)

            return
        }


        history.add(file)

        index = history.lastIndex

        diretorioAtual = file

        filtroAtual = Filtro.TODOS

        textoPesquisa = ""

        atualizarLista(file)
    }


    // =========================================================
    // ATUALIZAR LISTA
    // =========================================================

    private fun atualizarLista(
        directory: File
    ) {

        diretorioAtual = directory

        val files =
            directory.listFiles()?.toList()
                ?: emptyList()


        val collator =
            Collator.getInstance(
                Locale("pt", "BR")
            )


        var filtrados = files


        // =====================================================
        // FILTRO
        // =====================================================

        filtrados =
            when (filtroAtual) {

                Filtro.TODOS -> {

                    filtrados
                }


                Filtro.PASTAS -> {

                    filtrados.filter {
                        it.isDirectory
                    }
                }


                Filtro.IMAGENS -> {

                    filtrados.filter {
                        it.isFile &&
                        ehImagem(it)
                    }
                }


                Filtro.DOCUMENTOS -> {

                    filtrados.filter {
                        it.isFile &&
                        ehDocumento(it)
                    }
                }
            }


        // =====================================================
        // PESQUISA
        // =====================================================

        if (textoPesquisa.isNotEmpty()) {

            val pesquisa =
                textoPesquisa.lowercase(
                    Locale.ROOT
                )


            filtrados =
                filtrados.filter {

                    it.name
                        .lowercase(Locale.ROOT)
                        .contains(pesquisa)
                }
        }


        // =====================================================
        // ORDENAÇÃO
        // =====================================================

        val sorted =
            filtrados.sortedWith(
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

        atualizarCaminho(directory)
    }


    private fun atualizarListaAtual() {

        val directory =
            diretorioAtual
                ?: return

        atualizarLista(directory)
    }


    // =========================================================
    // IMAGENS
    // =========================================================

    private fun ehImagem(
        file: File
    ): Boolean {

        val extensao =
            file.extension
                .lowercase(Locale.ROOT)


        return extensao in setOf(
            "jpg",
            "jpeg",
            "png",
            "webp",
            "gif",
            "bmp",
            "heic",
            "heif",
            "avif"
        )
    }


    // =========================================================
    // DOCUMENTOS
    // =========================================================

    private fun ehDocumento(
        file: File
    ): Boolean {

        val extensao =
            file.extension
                .lowercase(Locale.ROOT)


        return extensao in setOf(
            "pdf",
            "doc",
            "docx",
            "xls",
            "xlsx",
            "ppt",
            "pptx",
            "txt",
            "csv",
            "rtf",
            "odt"
        )
    }


    // =========================================================
    // CAMINHO
    // =========================================================

    private fun atualizarCaminho(
        directory: File
    ) {

        pathText.text =
            obterCaminhoBonito(directory)
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


        if (currentPath == rootPath) {

            return "Armazenamento interno"
        }


        return if (
            currentPath.startsWith(rootPath)
        ) {

            val relativo =
                currentPath
                    .removePrefix(rootPath)
                    .trim('/')


            if (relativo.isEmpty()) {

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
            index >= history.size - 1
        ) {
            return
        }


        index++


        val directory =
            history[index]


        if (directory.isDirectory) {

            filtroAtual = Filtro.TODOS

            textoPesquisa = ""

            atualizarLista(directory)
        }
    }


    // =========================================================
    // VOLTAR DIRETÓRIO
    // =========================================================

    private fun voltarDiretorio() {

        if (index <= 0) {
            return
        }


        index--


        val directory =
            history[index]


        if (directory.isDirectory) {

            filtroAtual = Filtro.TODOS

            textoPesquisa = ""

            atualizarLista(directory)
        }
    }


    // =========================================================
    // ABRIR ARQUIVO EXTERNO
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
                    .lowercase(Locale.ROOT)


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
    // PERMISSÕES
    // =========================================================

    private fun pedirPermissao() {

        if (Build.VERSION.SDK_INT >= 30) {

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
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
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
                android.Manifest.permission.READ_EXTERNAL_STORAGE
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
                MANAGE_STORAGE_CODE &&
            temPermissao()
        ) {

            iniciar()
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
            requestCode == PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] ==
                android.content.pm.PackageManager
                    .PERMISSION_GRANTED
        ) {

            iniciar()
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

                assets.open("font.ttf").use {

                    Typeface.createFromAsset(
                        assets,
                        "font.ttf"
                    )
                }

            } catch (e: Exception) {

                Typeface.DEFAULT
            }


        if (view is TextView) {

            view.typeface = fonte
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