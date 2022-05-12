package com.example.proyectointegradodef.musica.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor

class MusicaAdapter(private val lista: ArrayList<ReadMusicaAlbumAutor>): RecyclerView.Adapter<MusicaViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.musica_layout, parent, false)
        return MusicaViewHolder(v)
    }

    override fun onBindViewHolder(holder: MusicaViewHolder, position: Int) {
        val musica = lista[position]
        holder.render(musica)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}