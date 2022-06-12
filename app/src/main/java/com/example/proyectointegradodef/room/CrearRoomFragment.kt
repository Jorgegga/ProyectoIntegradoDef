package com.example.proyectointegradodef.room

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentCrearRoomBinding
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDatabase

class CrearRoomFragment : Fragment() {
    lateinit var binding : FragmentCrearRoomBinding
    val PERMISO_CODE = 150
    lateinit var database : MusicaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(requireContext(), MusicaDatabase::class.java, "musica_database").allowMainThreadQueries().build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCrearRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun subirMusica(){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, 0)
    }

    private fun isPermisosConcedidos(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            database.MusicaDao().insertMusic(Musica(nombre = binding.insertarNombreLocal.text.toString(), autor = binding.insertarAutorLocal.text.toString(), album = binding.insertarAlbumLocal.text.toString(),  musica = data!!.data!!.toString()))
            Toast.makeText(context, resources.getString(R.string.audioSubido), Toast.LENGTH_LONG).show()
            reset()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun listeners() {
        binding.btnCrear.setOnClickListener {
            if(isPermisosConcedidos()){
                if(comprobarVacios()) {
                    subirMusica()
                }else{
                    Toast.makeText(context, resources.getString(R.string.campoVacio), Toast.LENGTH_LONG).show()
                }
            }else{
                pedirPermisos()
            }

        }
        binding.btnReset.setOnClickListener {
            reset()
        }
    }

    fun comprobarVacios(): Boolean{
        if(binding.insertarNombreLocal.text.toString() == ("")){
            Toast.makeText(requireContext(), "Tienes que poner un nombre", Toast.LENGTH_LONG).show()
            return false
        }
        if(binding.insertarAutorLocal.text.toString() == ("")){
            Toast.makeText(requireContext(), "Tienes que poner un autor", Toast.LENGTH_LONG).show()
            return false
        }
        if(binding.insertarAlbumLocal.text.toString() == ""){
            Toast.makeText(requireContext(), "Tienes que poner un album", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun reset(){
        binding.insertarNombreLocal.setText("")
        binding.insertarAutorLocal.setText("")
        binding.insertarAlbumLocal.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pedirPermisos(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
            Toast.makeText(context, resources.getString(R.string.darPermisos), Toast.LENGTH_SHORT).show()
        }else{
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISO_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_CODE->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    if(comprobarVacios()){
                        subirMusica()
                    }else{
                        Toast.makeText(context, resources.getString(R.string.campoVacio), Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(context, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : CrearRoomFragment {
            return CrearRoomFragment()
        }

    }
}