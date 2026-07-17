package com.mulheres

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

    class VH(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.folderName)
        val date: TextView = view.findViewById(R.id.folderDate)
        val count: TextView = view.findViewById(R.id.folderCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)

        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val file = list[position]

        holder.name.text = file.name.ifEmpty {
            "Sem nome"
        }

        val date = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(
            Date(file.lastModified())
        )

        holder.date.text = date

        val count = if (file.isDirectory) {
            file.listFiles()?.size ?: 0
        } else {
            0
        }

        holder.count.text = if (file.isDirectory) {
            "$count itens"
        } else {
            "arquivo"
        }

        holder.itemView.setOnClickListener {
            onClick(file)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList: List<File>) {
        list = newList
        notifyDataSetChanged()
    }
}