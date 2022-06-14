package com.example.proyectointegradodef.musica.crud.autor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityCrearAutorBinding
import com.example.proyectointegradodef.models.ReadAutor
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

/**
 * Crear autor activity
 *
 * @constructor Create empty Crear autor activity
 */
class CrearAutorActivity : AppCompatActivity() {

    lateinit var binding: ActivityCrearAutorBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var storage: FirebaseStorage

    var imagen: Uri = "".toUri()
    var nombre = ""
    var descripcion = ""
    val PERMISO_CODE_FICHERO = 200
    val PICK_IMAGE_REQUEST = 100
    var crearId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAutorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        storage = Firebase.storage
        initDb()
        listeners()
    }

    private fun listeners() {
        binding.btnResetAutor.setOnClickListener {
            limpiar()
        }
        binding.ibAutor.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero()
            }
        }
        binding.btnCrearAutor.setOnClickListener {
            if(comprobarCampos()){
                binding.loadingPanel.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                buscarId()
            }
        }
    }

    private fun annadirAutor() {
        val storageRef = storage.reference
        var randomString = UUID.randomUUID().toString()
        if(imagen.toString().equals("")){
            var ruta = "gs://proyectointegradodam-eef79.appspot.com/proyecto/autor/default"
            reference.child(randomString).setValue(ReadAutor(crearId, nombre, ruta, descripcion))
            Toast.makeText(this, "Se ha subido el autor correctamente", Toast.LENGTH_LONG).show()
            limpiar()
        }else {
            val imageRef = storageRef.child("proyecto/autor/${randomString}.png")
            val uploadTask = imageRef.putFile(imagen)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.loadingPanel.visibility = View.GONE
            }.addOnCompleteListener {
                var ruta =
                    "gs://proyectointegradodam-eef79.appspot.com/proyecto/autor/$randomString"
                reference.child(randomString)
                    .setValue(ReadAutor(crearId, nombre, ruta, descripcion))
                Toast.makeText(this, "Se ha subido el autor correctamente", Toast.LENGTH_LONG)
                    .show()
                limpiar()
            }
        }

    }

    private fun limpiar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.loadingPanel.visibility = View.GONE
        binding.etNombreAutor.text.clear()
        binding.etDescripcionAutor.text.clear()
        binding.ibAutor.setImageDrawable(
            AppCompatResources.getDrawable(
            this,
            R.drawable.default_autor
        ))
        imagen = "".toUri()
        nombre = ""
        descripcion = ""
        crearId = 0
    }

    private fun comprobarCampos(): Boolean{
        if(binding.etNombreAutor.text.isEmpty()){
            Toast.makeText(this, "Tienes que poner un nombre", Toast.LENGTH_LONG).show()
            return false
        }else{
            nombre = binding.etNombreAutor.text.toString()
        }
        if(binding.etDescripcionAutor.text.isEmpty()){
            descripcion = "No hay descripción disponible"
        }else{
            descripcion = binding.etDescripcionAutor.text.toString()
        }
        return true
    }

    private fun isPermisosConcedidosFichero(): Boolean{
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun permisosFichero(){
        var checkPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkPermisos != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISO_CODE_FICHERO)
        }else{
            Toast.makeText(this, resources.getString(R.string.noHasProporcionadoPermisos), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISO_CODE_FICHERO->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    cogerFichero()
                }else{
                    Toast.makeText(this, resources.getString(R.string.rechazarPermisos), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            var filePath = data!!.data
            imagen = filePath!!
            binding.ibAutor.setImageURI(filePath)
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

    private fun buscarId() {
        reference.get()
        var query = reference.orderByChild("id").limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        crearId = messageSnapshot.getValue<ReadAutor>(ReadAutor::class.java)!!.id + 1
                        annadirAutor()
                        return
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        return true
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("autors")
    }

}