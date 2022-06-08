package com.example.proyectointegradodef.musica.crud.music

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AdminMusicLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.google.firebase.storage.FirebaseStorage

class AdminMusicViewholder(v: View, clickAtPosition: (Int) -> Unit, longClickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = AdminMusicLayoutBinding.bind(v)
    val storageFire = FirebaseStorage.getInstance()

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
        itemView.setOnLongClickListener {
            longClickAtPosition(absoluteAdapterPosition)
            true
        }
    }

    fun render(music: ReadMusicaAlbumAutor){
        binding.tvNombreMusic.text = music.nombre
        binding.tvAlbumMusic.text = music.album
        binding.tvAutorMusic.text = music.autor
        binding.tvDescripcionMusic.text = music.descripcion
        if(music.portada == "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"){
            binding.ivMusicCrud.setImageDrawable(
                AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.default_album
                ))
        }else {
            val gsReference2 = storageFire.getReferenceFromUrl(music.portada + ".png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(itemView.context).load(gsReference2)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option)
                .into(binding.ivMusicCrud)
        }
    }
}