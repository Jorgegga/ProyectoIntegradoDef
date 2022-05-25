package com.example.proyectointegradodef.room

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.databinding.MusicaroomLayoutBinding
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica

class MusicaRoomViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v) {
    private val binding = MusicaroomLayoutBinding.bind(v)

    init{
        itemView.setOnClickListener {
            clickAtPosition(absoluteAdapterPosition)
        }
    }

    fun render(musica : Musica){
        binding.tvTitulo.text = musica.nombre
        binding.tvAutor.text = musica.autor
    }
}