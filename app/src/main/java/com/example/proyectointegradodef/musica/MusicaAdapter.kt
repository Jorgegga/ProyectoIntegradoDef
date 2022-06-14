package com.example.proyectointegradodef.musica

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectointegradodef.PortadaFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment
import com.example.proyectointegradodef.musica.album.AlbumFragment
import com.example.proyectointegradodef.musica.autor.AutorFragment

/**
 * Musica adapter
 *
 * @constructor
 *
 * @param fragmentManager
 * @param lifecycle
 */
class MusicaAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position){
            0 -> return AlbumFragment()
            1 -> return AutorFragment()
            2 -> return MusicaFragment()
            else -> return PortadaFragment()
        }
    }

}