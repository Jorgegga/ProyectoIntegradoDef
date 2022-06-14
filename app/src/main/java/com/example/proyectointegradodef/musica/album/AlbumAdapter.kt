package com.example.proyectointegradodef.musica.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AlbumLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.google.firebase.storage.FirebaseStorage

/**
 * Album adapter
 *
 * @property lista
 * @property clickListener
 * @constructor Create empty Album adapter
 */
class AlbumAdapter(private val lista: ArrayList<ReadAlbumAutor>, private val clickListener: (ReadAlbumAutor) -> Unit): RecyclerView.Adapter<AlbumViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = AlbumViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.album_layout, parent, false)){
            clickListener(lista[it])
        }
        return inflater
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = lista[position]
        holder.render(album)
        holder.itemView.setOnClickListener {
            clickListener(lista[position])
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}