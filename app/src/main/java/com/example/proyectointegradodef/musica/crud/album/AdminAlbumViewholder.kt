package com.example.proyectointegradodef.musica.crud.album

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AdminAlbumLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.google.firebase.storage.FirebaseStorage

class AdminAlbumViewholder(v: View, clickAtPosition: (Int) -> Unit, longClickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = AdminAlbumLayoutBinding.bind(v)
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

    fun render(album: ReadAlbumAutor){
        binding.tvNombreAlbum.text = album.titulo
        binding.tvAutorAlbum.text = album.autor
        binding.tvDescripcionAlbum.text = album.descripcion
        if(album.portada == "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"){
            binding.ivAlbumCrud.setImageDrawable(
                AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.default_album
                ))
        }else {
            val gsReference2 = storageFire.getReferenceFromUrl(album.portada + ".png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(itemView.context).load(gsReference2)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option)
                .into(binding.ivAlbumCrud)
        }
    }
}