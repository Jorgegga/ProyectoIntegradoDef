package com.example.proyectointegradodef.room

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.MusicaroomLayoutBinding
import com.example.proyectointegradodef.preferences.AppUse

/**
 * Musica room adapter
 *
 * @property allMusic
 * @property clickListener
 * @property longClickListener
 * @constructor Create empty Musica room adapter
 */
class MusicaRoomAdapter(
    private val allMusic: ArrayList<Musica>,
    private val clickListener: (Musica) -> Unit,
    private val longClickListener: (Musica) -> Unit
) : RecyclerView.Adapter<MusicaRoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicaRoomViewHolder {
        val inflater = MusicaRoomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.musicaroom_layout, parent, false),
            {
                clickListener(allMusic[it])
            }, {
                longClickListener(allMusic[it])
            })
        return inflater
    }

    override fun onBindViewHolder(holder: MusicaRoomViewHolder, position: Int) {
        val musica = allMusic[position]
        holder.render(musica)
        holder.itemView.setOnClickListener {
            AppUse.recyclerPosition = position
            clickListener(allMusic[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener(allMusic[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return allMusic.size
    }


}