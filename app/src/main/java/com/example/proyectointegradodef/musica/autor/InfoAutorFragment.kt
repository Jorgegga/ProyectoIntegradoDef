package com.example.proyectointegradodef.musica.autor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentInfoAutorBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage

/**
 * Info autor fragment
 *
 * @constructor Create empty Info autor fragment
 */
class InfoAutorFragment : Fragment() {

    lateinit var binding: FragmentInfoAutorBinding

    var storageFire = FirebaseStorage.getInstance()

    var nombre = ""
    var ruta = ""
    var descripcion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInfoAutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recogerBundle()
        ponerDatos()
    }

    /**
     * Recoger bundle
     *
     */
    private fun recogerBundle() {
        if (arguments != null) {
            if (arguments?.getInt("id", 0) != 0) {
                nombre = arguments?.getString("nombre", "Default")!!
                ruta = arguments?.getString("foto", "Default")!!
                descripcion = arguments?.getString("descripcion", "Default")!!
            }
        }
    }

    /**
     * Poner datos
     *
     */
    private fun ponerDatos(){
        binding.tvNombreInfoAutor.text = nombre
        binding.tvDescripcionInfoAutor.text = descripcion
        val gsReference2 = storageFire.getReferenceFromUrl("$ruta.png")
        val option = RequestOptions().error(R.drawable.default_autor)
        GlideApp.with(requireContext()).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivInfoAutor)
    }

    companion object {
        @JvmStatic
        fun newInstance() : InfoAutorFragment{
            return InfoAutorFragment()
        }

    }
}