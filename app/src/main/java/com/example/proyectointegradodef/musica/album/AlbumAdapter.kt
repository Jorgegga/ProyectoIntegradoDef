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

class AlbumAdapter(private val lista: ArrayList<ReadAlbumAutor>, private val clickListener: (ReadAlbumAutor) -> Unit): RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
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

    class AlbumViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v)  {
        private val binding = AlbumLayoutBinding.bind(v)
        var storageFire = FirebaseStorage.getInstance()

        init{
            itemView.setOnClickListener {
                clickAtPosition(absoluteAdapterPosition)
            }
        }

        fun render(album: ReadAlbumAutor){
            binding.tvRecyclerAlbum.text = album.titulo
            binding.tvAutorRecyclerAlbum.text = album.autor
            val gsReference2 = storageFire.getReferenceFromUrl(album.portada + ".png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(itemView.context).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivRecyclerAlbum)

        }

    }
}