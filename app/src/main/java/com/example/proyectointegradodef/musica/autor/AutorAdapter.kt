package com.example.proyectointegradodef.musica.autor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadAutor

class AutorAdapter(private val lista: ArrayList<ReadAutor>): RecyclerView.Adapter<AutorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.autor_layout, parent, false)
        return AutorViewHolder(v)
    }

    override fun onBindViewHolder(holder: AutorViewHolder, position: Int) {
       val autor = lista[position]
        holder.render(autor)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}