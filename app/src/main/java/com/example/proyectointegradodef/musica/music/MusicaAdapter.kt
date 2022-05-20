package com.example.proyectointegradodef.musica.music

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.MusicaLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.google.firebase.storage.FirebaseStorage

class MusicaAdapter(private val lista: ArrayList<ReadMusicaAlbumAutor>, private val clickListener: (ReadMusicaAlbumAutor) -> Unit): RecyclerView.Adapter<MusicaViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicaViewHolder {
        val inflater = MusicaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.musica_layout, parent, false)){
            clickListener(lista[it])
        }
        return inflater
    }

    override fun onBindViewHolder(holder: MusicaViewHolder, position: Int) {
        val musica = lista[position]
        holder.render(musica)
        holder.itemView.setOnClickListener {
            clickListener(lista[position])
        }

    }

    override fun getItemCount(): Int {
        return lista.size
    }


}