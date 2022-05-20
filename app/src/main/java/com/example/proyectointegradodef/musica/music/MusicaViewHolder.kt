package com.example.proyectointegradodef.musica.music

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.MusicaLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.google.firebase.storage.FirebaseStorage

class MusicaViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v){
    val binding = MusicaLayoutBinding.bind(v)
    var storageFire = FirebaseStorage.getInstance()

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
    }

    fun render(musica : ReadMusicaAlbumAutor){
        binding.tvTitulo.text = musica.nombre
        binding.tvAlbum.text = musica.album
        binding.tvAutor.text = musica.autor
        val gsReference2 = storageFire.getReferenceFromUrl(musica.portada + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(itemView.context).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivMusica)

    }

}