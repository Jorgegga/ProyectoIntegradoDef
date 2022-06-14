package com.example.proyectointegradodef.musica.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.example.proyectointegradodef.preferences.AppUse

/**
 * Musica adapter
 *
 * @property lista
 * @property clickListener
 * @property longClickListener
 * @constructor Create empty Musica adapter
 */
class MusicaAdapter(
    private val lista: ArrayList<ReadMusicaAlbumAutor>,
    private val clickListener: (ReadMusicaAlbumAutor) -> Unit,
    private val longClickListener: (ReadMusicaAlbumAutor) -> Unit
) : RecyclerView.Adapter<MusicaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicaViewHolder {
        return MusicaViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.musica_layout, parent, false),
            { clickListener(lista[it]) },
            { longClickListener(lista[it]) })
    }

    override fun onBindViewHolder(holder: MusicaViewHolder, position: Int) {
        val musica = lista[position]
        holder.render(musica)
        holder.itemView.setOnClickListener {
            AppUse.recyclerPosition = position
            clickListener(lista[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener(lista[position])
            true
        }

    }

    override fun getItemCount(): Int {
        return lista.size
    }


}