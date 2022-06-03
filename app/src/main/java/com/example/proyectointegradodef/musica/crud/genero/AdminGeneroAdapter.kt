package com.example.proyectointegradodef.musica.crud.genero

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadGenero

class AdminGeneroAdapter(private val lista: ArrayList<ReadGenero>, private val clickListener: (ReadGenero) -> Unit, private val onLongClickListener: (ReadGenero) -> Unit): RecyclerView.Adapter<AdminGeneroViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminGeneroViewholder {
        val inflater = AdminGeneroViewholder(LayoutInflater.from(parent.context).inflate(R.layout.admin_genero_layout, parent, false),{
            clickListener(lista[it])
        },{
            onLongClickListener(lista[it])
        })
        return inflater
    }

    override fun onBindViewHolder(holder: AdminGeneroViewholder, position: Int) {
        val genero = lista[position]
        holder.render(genero)
        holder.itemView.setOnClickListener {
            clickListener(lista[position])
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener(lista[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}