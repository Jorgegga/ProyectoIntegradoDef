package com.example.proyectointegradodef.musica.crud

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectointegradodef.PortadaFragment
import com.example.proyectointegradodef.musica.album.AlbumFragment
import com.example.proyectointegradodef.musica.autor.AutorFragment
import com.example.proyectointegradodef.musica.crud.album.AdminAlbumFragment
import com.example.proyectointegradodef.musica.crud.autor.AdminAutorFragment
import com.example.proyectointegradodef.musica.crud.genero.AdminGeneroFragment
import com.example.proyectointegradodef.musica.crud.music.AdminMusicFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment

/**
 * Crud adapter
 *
 * @constructor
 *
 * @param fragmentManager
 * @param lifecycle
 */
class CrudAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when (position){
            0 -> return AdminAutorFragment()
            1 -> return AdminGeneroFragment()
            2 -> return AdminAlbumFragment()
            3 -> return AdminMusicFragment()
            else -> return PortadaFragment()
        }
    }

}