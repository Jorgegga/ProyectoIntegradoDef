package com.example.proyectointegradodef.musica.autor

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AutorLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAutor
import com.google.firebase.storage.FirebaseStorage

/**
 * Autor view holder
 *
 * @constructor
 *
 * @param v
 * @param clickAtPosition
 */
class AutorViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = AutorLayoutBinding.bind(v)
    var storageFire = FirebaseStorage.getInstance()

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
    }

    /**
     * Render
     *
     * @param autor
     */
    fun render(autor: ReadAutor){
        binding.tvNombreRecycler.text = autor.nombre
        binding.tvDescripcionRecycler.text = autor.descripcion
        val gsReference2 = storageFire.getReferenceFromUrl(autor.foto + ".png")
        val option = RequestOptions().error(R.drawable.default_autor)
        GlideApp.with(itemView.context).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivFotoRecycler)

    }
}