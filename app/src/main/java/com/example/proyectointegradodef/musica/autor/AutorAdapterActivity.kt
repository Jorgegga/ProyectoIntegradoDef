package com.example.proyectointegradodef.musica.autor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectointegradodef.PortadaFragment
import com.example.proyectointegradodef.musica.album.AlbumFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment

class AutorAdapterActivity(fragmentManager: FragmentManager, lifecycle: Lifecycle, var bundle: Bundle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position){
            0 -> {
                var fragment : Fragment = AlbumFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 ->{
                var fragment : Fragment = MusicaFragment()
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                var fragment : Fragment = PortadaFragment()
                fragment.arguments = bundle
                return fragment
            }
        }
    }
}