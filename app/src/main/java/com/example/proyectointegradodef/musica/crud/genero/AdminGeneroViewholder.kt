package com.example.proyectointegradodef.musica.crud.genero

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AdminGeneroLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadGenero
import com.google.firebase.storage.FirebaseStorage

class AdminGeneroViewholder(v: View, clickAtPosition: (Int) -> Unit, longClickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = AdminGeneroLayoutBinding.bind(v)
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

    fun render(genero: ReadGenero){
        binding.tvNombreGenero.text = genero.nombre
        if(genero.portada == "gs://proyectointegradodam-eef79.appspot.com/proyecto/genero/default"){
            binding.ivGeneroCrud.setImageDrawable(
                AppCompatResources.getDrawable(
                itemView.context,
                R.drawable.default_album
            ))
        }else{
            val gsReference2 = storageFire.getReferenceFromUrl(genero.portada + ".png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(itemView.context).load(gsReference2)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option)
                .into(binding.ivGeneroCrud)
        }
    }
}