package com.example.proyectointegradodef.musica.album

import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AlbumLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * Album view holder
 *
 * @constructor
 *
 * @param v
 * @param clickAtPosition
 */
class AlbumViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v)  {
    private val binding = AlbumLayoutBinding.bind(v)
    var storageFire = FirebaseStorage.getInstance()

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
    }

    /**
     * Render
     *
     * @param album
     */
    fun render(album: ReadAlbumAutor){
        binding.tvRecyclerAlbum.text = album.titulo
        binding.tvAutorRecyclerAlbum.text = album.autor
        val gsReference2 = storageFire.getReferenceFromUrl(album.portada + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(itemView.context).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivRecyclerAlbum)

    }

}