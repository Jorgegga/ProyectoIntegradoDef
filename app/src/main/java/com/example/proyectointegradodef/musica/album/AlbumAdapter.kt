package com.example.proyectointegradodef.musica.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadAlbum

class AlbumAdapter(private val lista: ArrayList<ReadAlbum>): RecyclerView.Adapter<AlbumViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.album_layout, parent, false)
        return AlbumViewHolder(v)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = lista[position]
        holder.render(album)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}