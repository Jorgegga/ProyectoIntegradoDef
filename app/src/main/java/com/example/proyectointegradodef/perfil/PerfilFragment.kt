package com.example.proyectointegradodef.perfil

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
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
import com.example.proyectointegradodef.databinding.FragmentPerfilBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Perfil fragment
 *
 * @constructor Create empty Perfil fragment
 */
class PerfilFragment : Fragment() {
    lateinit var binding : FragmentPerfilBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var mUri: Uri
    lateinit var mediaStorageDir : File
    var storageFire = FirebaseStorage.getInstance()
    val PERMISO_CODE_CAMARA = 150
    val PERMISO_CODE_FICHERO = 200
    val PICK_IMAGE_REQUEST = 100

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
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        perfil()

    }

    /**
     * Listeners
     *
     */
    fun listeners(){
        binding.btnCamara.setOnClickListener {
            if(isPermisosConcedidosCamara()){
                tomarFoto()
            }else{
                permisosCamara()
            }
        }

        binding.btnArchivo.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero()
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
                val gsReference2 = storageFire.getReferenceFromUrl("$referencia2.png")
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

    private fun isPermisosConcedidosCamara(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isPermisosConcedidosFichero(): Boolean{
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_CODE_CAMARA->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    tomarFoto()
                }else{
                    Toast.makeText(context, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
                }
            }

            PERMISO_CODE_FICHERO->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    cogerFichero()
                }else{
                    Toast.makeText(context, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Permisos camara
     *
     */
    fun permisosCamara(){
        var checkPermisos = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        var checkPermisos2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkPermisos != PackageManager.PERMISSION_GRANTED || checkPermisos2 !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISO_CODE_CAMARA)
        }else{
            Toast.makeText(requireContext(), resources.getString(R.string.noHasProporcionadoPermisos), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Permisos fichero
     *
     */
    fun permisosFichero(){
        var checkPermisos = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkPermisos != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISO_CODE_FICHERO)
        }else{
            Toast.makeText(requireContext(), resources.getString(R.string.noHasProporcionadoPermisos), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Coger fichero
     *
     */
    fun cogerFichero(){
        val i = Intent(Intent.ACTION_PICK)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(i, PICK_IMAGE_REQUEST)
    }

    /**
     * Tomar foto
     *
     */
    fun tomarFoto(){
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        mediaStorageDir = File(
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
        if(requestCode == 0 && resultCode == RESULT_OK){
            mUri = Uri.fromFile(
                Compressor(context).compressToFile(File(
                    mediaStorageDir.path + File.separator +
                            "profile_img.jpg"
                )))
            val storageRef = storage.reference
            val imageRef = storageRef.child("proyecto/perfil/${user?.uid}.png")
            val uploadTask = imageRef.putFile(mUri)
            uploadTask.addOnFailureListener{
                Toast.makeText(requireContext(), resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                val values = HashMap<String, Any>()
                values["ruta"] = "gs://proyectointegradodam-eef79.appspot.com/proyecto/perfil/${user!!.uid}"
                reference.child(user!!.uid).updateChildren(values)
                Toast.makeText(requireContext(), resources.getString(R.string.fotoActualizada), Toast.LENGTH_LONG).show()
                perfil()
                perfilInicio()
            }
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            val storageRef = storage.reference
            var filePath = data!!.data
            val imageRef = storageRef.child("proyecto/perfil/${user?.uid}.png")
            /*mUri = Uri.fromFile(
                Compressor(context).compressToFile(File(
                    filePath.path
                )))*/
            val uploadTask = imageRef.putFile(filePath!!)
            uploadTask.addOnFailureListener {
                Toast.makeText(requireContext(), resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                val values = HashMap<String, Any>()
                values["ruta"] = "gs://proyectointegradodam-eef79.appspot.com/proyecto/perfil/${user!!.uid}"
                reference.child(user!!.uid).updateChildren(values)
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
        fun newInstance(): PerfilFragment{
            return PerfilFragment()
        }

    }
}