package com.mulheres

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FolderAdapter(
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<FolderAdapter.VH>() {

    private var list: List<File> = emptyList()


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    class VH(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView =
            view.findViewById(R.id.folderName)

        val date: TextView =
            view.findViewById(R.id.folderDate)

        val count: TextView =
            view.findViewById(R.id.folderCount)
    }


    // =========================================================
    // CRIAR ITEM
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_folder,
                parent,
                false
            )

        return VH(view)
    }


    // =========================================================
    // PREENCHER ITEM
    // =========================================================

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {

        val file = list[position]


        // -----------------------------------------------------
        // FONTE
        // -----------------------------------------------------

        val fonte = try {

            Typeface.createFromAsset(
                holder.itemView.context.assets,
                "font.ttf"
            )

        } catch (e: Exception) {

            Typeface.DEFAULT
        }

        holder.name.typeface = fonte
        holder.date.typeface = fonte
        holder.count.typeface = fonte


        // -----------------------------------------------------
        // NOME
        // -----------------------------------------------------

        holder.name.text =
            file.name.ifEmpty {
                "Sem nome"
            }


        // -----------------------------------------------------
        // DATA
        // -----------------------------------------------------

        holder.date.text =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(
                Date(file.lastModified())
            )


        // -----------------------------------------------------
        // PASTA OU ARQUIVO
        // -----------------------------------------------------

        if (file.isDirectory) {

            val quantidade =
                file.listFiles()?.size ?: 0

            holder.count.text =
                if (quantidade == 1) {
                    "1 item"
                } else {
                    "$quantidade itens"
                }

        } else {

            holder.count.text = "Arquivo"
        }


        // -----------------------------------------------------
        // CLIQUE
        // -----------------------------------------------------

        holder.itemView.setOnClickListener {

            onClick(file)
        }
    }


    // =========================================================
    // QUANTIDADE
    // =========================================================

    override fun getItemCount(): Int {
        return list.size
    }


    // =========================================================
    // ATUALIZAR
    // =========================================================

    fun update(newList: List<File>) {

        list = newList

        notifyDataSetChanged()
    }
}