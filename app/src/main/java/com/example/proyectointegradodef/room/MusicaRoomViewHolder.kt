package com.example.proyectointegradodef.room

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.databinding.MusicaroomLayoutBinding
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica

class MusicaRoomViewHolder(v: View): RecyclerView.ViewHolder(v) {
    private val binding = MusicaroomLayoutBinding.bind(v)

    fun render(musica : Musica){
        binding.tvTitulo.text = musica.nombre
        binding.tvAutor.text = musica.autor
        binding.musicCardView.setOnClickListener {
            AppUse.nombre = musica.nombre
            AppUse.autor = musica.autor
            AppUse.cancion = musica.musica
            AppUse.reproduciendoLocal.value = true
            AppUse.reproduciendoLocal.value = false
        }
    }
}