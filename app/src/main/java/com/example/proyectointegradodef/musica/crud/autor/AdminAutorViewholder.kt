package com.example.proyectointegradodef.musica.crud.autor

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AdminAutorLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAutor
import com.google.firebase.storage.FirebaseStorage

class AdminAutorViewholder(v: View, clickAtPosition: (Int) -> Unit, longClickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = AdminAutorLayoutBinding.bind(v)
    var storageFire = FirebaseStorage.getInstance()

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
        itemView.setOnLongClickListener {
            longClickAtPosition(absoluteAdapterPosition)
            true
        }
    }

    fun render(autor: ReadAutor){
        binding.tvNombreAutor.text = autor.nombre
        binding.tvDescripcionAutor.text = autor.descripcion
        if(autor.foto == "gs://proyectointegradodam-eef79.appspot.com/proyecto/autor/default"){
            binding.ivAutorCrud.setImageDrawable(
                AppCompatResources.getDrawable(
                itemView.context,
                R.drawable.default_autor
            ))
        }else {
            val gsReference2 = storageFire.getReferenceFromUrl(autor.foto + ".png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(itemView.context).load(gsReference2)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option)
                .into(binding.ivAutorCrud)
        }
    }
}