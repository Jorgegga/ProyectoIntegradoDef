package com.example.proyectointegradodef.musica.crud.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadAlbumAutor

class AdminAlbumAdapter(private val lista: ArrayList<ReadAlbumAutor>, private val clickListener: (ReadAlbumAutor) -> Unit, private val onLongClickListener: (ReadAlbumAutor) -> Unit): RecyclerView.Adapter<AdminAlbumViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAlbumViewholder {
        val inflater = AdminAlbumViewholder(LayoutInflater.from(parent.context).inflate(R.layout.admin_album_layout, parent, false),{
            clickListener(lista[it])
        },{
            onLongClickListener(lista[it])
        })
        return inflater
    }

    override fun onBindViewHolder(holder: AdminAlbumViewholder, position: Int) {
        val album = lista[position]
        holder.render(album)
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