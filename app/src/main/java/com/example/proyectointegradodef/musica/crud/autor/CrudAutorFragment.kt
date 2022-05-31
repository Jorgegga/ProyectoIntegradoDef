package com.example.proyectointegradodef.musica.crud.autor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentCrudAutorBinding

class CrudAutorFragment : Fragment() {

    lateinit var binding: FragmentCrudAutorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCrudAutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : CrudAutorFragment{
            return CrudAutorFragment()
        }
    }
}