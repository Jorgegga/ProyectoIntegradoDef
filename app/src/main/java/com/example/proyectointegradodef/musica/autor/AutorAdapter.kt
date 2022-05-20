package com.example.proyectointegradodef.musica.autor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AutorLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAutor
import com.google.firebase.storage.FirebaseStorage

class AutorAdapter(private val lista: ArrayList<ReadAutor>, private val clickListener: (ReadAutor) -> Unit): RecyclerView.Adapter<AutorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutorViewHolder {
        val inflater = AutorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.autor_layout, parent, false)){
            clickListener(lista[it])
        }
        return inflater
    }

    override fun onBindViewHolder(holder: AutorViewHolder, position: Int) {
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