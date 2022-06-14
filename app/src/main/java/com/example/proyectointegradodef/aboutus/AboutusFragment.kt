package com.example.proyectointegradodef.aboutus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentAboutusBinding

/**
 * Aboutus fragment
 *
 * @constructor Create empty Aboutus fragment
 */
class AboutusFragment : Fragment() {

    lateinit var binding: FragmentAboutusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {

        @JvmStatic
        fun newInstance() : AboutusFragment{
            return AboutusFragment()
        }

    }
}