package com.example.proyectointegradodef.musica.crud

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectointegradodef.PortadaFragment
import com.example.proyectointegradodef.musica.album.AlbumFragment
import com.example.proyectointegradodef.musica.autor.AutorFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment

class CrudAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when (position){
            0 -> return AlbumFragment()
            1 -> return AutorFragment()
            2 -> return MusicaFragment()
            3 -> return AlbumFragment()
            else -> return PortadaFragment()
        }
    }

}