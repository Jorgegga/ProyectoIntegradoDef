package com.example.proyectointegradodef.musica

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectointegradodef.camara.CamaraFragment
import com.example.proyectointegradodef.databases.CrearFragment
import com.example.proyectointegradodef.room.CrearRoomFragment

class AdapterMusic(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position){
            0 -> return CrearFragment()
            1 -> return CamaraFragment()
            2 -> return CrearRoomFragment()
            else -> return CrearFragment()
        }
    }

}