package com.example.proyectointegradodef.musica.album

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointegradodef.databinding.MusicaLayoutBinding
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.example.proyectointegradodef.musica.music.MusicaAdapter
import com.google.firebase.storage.FirebaseStorage

/*class AlbumMusicaAdapter(private val lista: ArrayList<ReadMusicaAlbumAutor>, private val clickListener: (ReadMusicaAlbumAutor) -> Unit): RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicaAdapter.MusicaViewHolder {

    }

    override fun onBindViewHolder(holder: MusicaAdapter.MusicaViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {

    }

    class AlbumMusicaViewHolder(v: View, clickAtPosition: (Int) -> Unit): RecyclerView.ViewHolder(v){
        val binding = MusicaLayoutBinding.bind(v)
        var storageFire = FirebaseStorage.getInstance()

        init{
            itemView.setOnClickListener {
                clickAtPosition(absoluteAdapterPosition)
            }
        }

        fun render(musica: ReadMusicaAlbumAutor){

        }
    }
}*/