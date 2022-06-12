package com.example.proyectointegradodef.room

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.MusicaroomLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica

class MusicaRoomViewHolder(v: View, clickAtPosition: (Int) -> Unit, longClickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = MusicaroomLayoutBinding.bind(v)

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
        itemView.setOnLongClickListener {
            longClickAtPosition(absoluteAdapterPosition)
            true
        }
    }

    fun render(musica : Musica){
        binding.tvTitulo.text = musica.nombre
        binding.tvAutor.text = musica.autor
        binding.tvAlbum.text = musica.album
        GlideApp.with(itemView.context).load(R.drawable.default_album).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.ivMusica)
    }
}