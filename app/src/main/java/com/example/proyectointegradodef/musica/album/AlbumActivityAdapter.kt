package com.example.proyectointegradodef.musica.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.example.proyectointegradodef.preferences.AppUse

/**
 * Album activity adapter
 *
 * @property lista
 * @property clickListener
 * @property longClickListener
 * @constructor Create empty Album activity adapter
 */
class AlbumActivityAdapter(
    private val lista: ArrayList<ReadMusicaAlbumAutor>,
    private val clickListener: (ReadMusicaAlbumAutor) -> Unit,
    private val longClickListener: (ReadMusicaAlbumAutor) -> Unit
) : RecyclerView.Adapter<AlbumActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumActivityViewHolder {
        return AlbumActivityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.musica_layout, parent, false),
            { clickListener(lista[it]) },
            { longClickListener(lista[it]) })
    }

    override fun onBindViewHolder(holder: AlbumActivityViewHolder, position: Int) {
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