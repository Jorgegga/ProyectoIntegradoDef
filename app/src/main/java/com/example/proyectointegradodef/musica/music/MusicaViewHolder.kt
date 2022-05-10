package com.example.proyectointegradodef.musica.music

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.databinding.MusicaLayoutBinding
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.preferences.AppUse

class MusicaViewHolder(v: View ): RecyclerView.ViewHolder(v){
    private val binding = MusicaLayoutBinding.bind(v)

    fun render(musica : ReadMusica){
        binding.tvTitulo.text = musica.nombre
        binding.tvAlbum.text = musica.album_id.toString()
        binding.btnPlay.setOnClickListener {
            AppUse.nombre = musica.nombre
            AppUse.autor = musica.autor_id.toString()
            AppUse.cancion = musica.ruta
            AppUse.reproduciendo.value = true
            AppUse.reproduciendo.value = false
        }
    }

}