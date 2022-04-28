package com.example.proyectointegradodef.camara

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentCamaraBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.CrearFoto
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File


class CamaraFragment : Fragment() {
    lateinit var binding : FragmentCamaraBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var mUri: Uri
    var storageFire = FirebaseStorage.getInstance()
    val PERMISO_CODE = 150

    val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDb()
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        perfil()

    }

    fun listeners(){
        binding.btnCamara.setOnClickListener {
            if(isPermisosConcedidos()){
                tomarFoto()
            }else{
                permisosCamara()
            }
        }
    }

    private fun perfil(){
        var referencia2 = ""
        reference.child(user!!.uid).child("ruta").get().addOnSuccessListener {
            if (it.value == null) {
                binding.ivCamara.setImageDrawable(getDrawable(requireContext(), R.drawable.keystoneback))
            } else {
                referencia2 = it.value as String
                val gsReference2 = storageFire.getReferenceFromUrl(referencia2 + ".png")
                val option = RequestOptions().error(R.drawable.keystoneback)
                GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivCamara)
            }
        }
    }

    private fun perfilInicio(){
        var navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        var header = navView.getHeaderView(0)

        var referencia2 = ""
        reference.child(user!!.uid).child("ruta").get().addOnSuccessListener {
            if (it.value == null) {
                header.findViewById<ImageView>(R.id.ivPerfil).setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.keystoneback
                    )
                )
            } else {
                referencia2 = it.value as String
                val gsReference2 = storageFire.getReferenceFromUrl(referencia2 + ".png")
                val option = RequestOptions().error(R.drawable.keystoneback)
                GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(header.findViewById<ImageView>(R.id.ivPerfil))
            }
        }
    }

    private fun isPermisosConcedidos(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_CODE->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    tomarFoto()
                }else{
                    Toast.makeText(context, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun permisosCamara(){
        var checkPermisos = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        var checkPermisos2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkPermisos != PackageManager.PERMISSION_GRANTED || checkPermisos2 !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISO_CODE)
        }else{
            Toast.makeText(requireContext(), resources.getString(R.string.noHasProporcionadoPermisos), Toast.LENGTH_SHORT).show()
        }
    }

    fun tomarFoto(){
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "IMG_FOLDER"
        )

        try {
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        mUri = Uri.fromFile(
            File(
                mediaStorageDir.path + File.separator +
                        "profile_img.jpg"
            )
        )
        i.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(i, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            val storageRef = storage.reference
            val imageRef = storageRef.child("proyecto/perfil/${user?.uid}.png")
            val uploadTask = imageRef.putFile(mUri)
            uploadTask.addOnFailureListener{
                Toast.makeText(requireContext(), resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { taskSnapshot ->
                reference.child(user!!.uid).setValue(CrearFoto("gs://proyectointegradodam-eef79.appspot.com/proyecto/perfil/${user!!.uid}"))
                Toast.makeText(requireContext(), resources.getString(R.string.fotoActualizada), Toast.LENGTH_LONG).show()
                perfil()
                perfilInicio()
            }
        }
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("perfil")
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(): CamaraFragment{
            return CamaraFragment()
        }

    }
}