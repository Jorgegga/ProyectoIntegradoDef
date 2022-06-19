package com.example.proyectointegradodef.musica.music

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentCrearBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

/**
 * Crear fragment
 *
 * @constructor Create empty Crear fragment
 *
 * Antiguo fragment para crear musica, ya no lo utilizo
 */
class CrearFragment : Fragment() {

    lateinit var binding: FragmentCrearBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var storage: FirebaseStorage
    val PERMISO_CODE = 150


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCrearBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Firebase.storage
        initDb()
        listeners()
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

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("musica")
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
            var numeroRandom = UUID.randomUUID().toString()
            val storageRef = storage.reference
            val path = data?.data
            val musicRef = storageRef.child("proyecto/musica/$numeroRandom.mp3")
            val uploadTask = musicRef.putFile(path!!)
            uploadTask.addOnFailureListener{
                Toast.makeText(context, resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                //reference.child(numeroRandom).setValue(CrearMusica("gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/$numeroRandom", binding.insertarNombre.text.toString(), binding.insertarAutor.text.toString(), numeroRandom))
                Toast.makeText(context, resources.getString(R.string.audioSubido), Toast.LENGTH_LONG).show()
                reset()
            }
        }
    }

    /**
     * Comprobar vacios
     *
     * @return
     */
    fun comprobarVacios(): Boolean{
        if(binding.insertarNombre.text.toString().equals("")){
            return false
        }
        if(binding.insertarAutor.text.toString().equals("")){
            return false
        }
        return true
    }

    /**
     * Reset
     *
     */
    fun reset(){
        binding.insertarNombre.setText("")
        binding.insertarAutor.setText("")

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pedirPermisos(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
            Toast.makeText(context, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
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

        @JvmStatic
        fun newInstance(): CrearFragment {
            return CrearFragment()
        }

    }
}