package com.example.proyectointegradodef.musica.crud.autor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadAutor

class AdminAutorAdapter(private val lista: ArrayList<ReadAutor>, private val clickListener: (ReadAutor) -> Unit): RecyclerView.Adapter<AdminAutorViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAutorViewholder {
        val inflater = AdminAutorViewholder(LayoutInflater.from(parent.context).inflate(R.layout.admin_autor_layout, parent, false)){
            clickListener(lista[it])
        }
        return inflater
    }

    override fun onBindViewHolder(holder: AdminAutorViewholder, position: Int) {
        val autor = lista[position]
        holder.render(autor)
        holder.itemView.setOnClickListener {
            clickListener(lista[position])
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}