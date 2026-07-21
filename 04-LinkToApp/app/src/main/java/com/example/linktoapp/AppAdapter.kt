package com.linktoapp.app

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val apps: MutableList<AppInfo>,
    private val repository: AppRepository
) : RecyclerView.Adapter<AppAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val icone: ImageView = view.findViewById(R.id.icone)
        val nome: TextView = view.findViewById(R.id.nome)
        val pacote: TextView = view.findViewById(R.id.pacote)
        val check: CheckBox = view.findViewById(R.id.check)
    }

    private var fonte: Typeface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        if (fonte == null) {
            fonte = Typeface.createFromAsset(
                parent.context.assets,
                "font.ttf"
            )
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val app = apps[position]

        holder.icone.setImageDrawable(app.icone)

        holder.nome.text = app.nome
        holder.nome.typeface = fonte

        holder.pacote.text = app.pacote
        holder.pacote.typeface = fonte

        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = app.protegido

        holder.check.setOnCheckedChangeListener { _, checked ->

            app.protegido = checked

            if (checked) {
                repository.proteger(app.pacote)
            } else {
                repository.remover(app.pacote)
            }
        }
    }
}