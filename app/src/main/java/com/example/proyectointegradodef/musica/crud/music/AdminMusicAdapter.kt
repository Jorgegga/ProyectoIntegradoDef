package com.example.proyectointegradodef.musica.crud.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor

/**
 * Admin music adapter
 *
 * @property lista
 * @property clickListener
 * @property onLongClickListener
 * @constructor Create empty Admin music adapter
 */
class AdminMusicAdapter(private val lista: ArrayList<ReadMusicaAlbumAutor>, private val clickListener: (ReadMusicaAlbumAutor) -> Unit, private val onLongClickListener: (ReadMusicaAlbumAutor) -> Unit): RecyclerView.Adapter<AdminMusicViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminMusicViewholder {
        val inflater = AdminMusicViewholder(LayoutInflater.from(parent.context).inflate(R.layout.admin_music_layout, parent, false),{
            clickListener(lista[it])
        },{
            onLongClickListener(lista[it])
        })
        return inflater
    }

    override fun onBindViewHolder(holder: AdminMusicViewholder, position: Int) {
        val music = lista[position]
        holder.render(music)
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