package com.example.proyectointegradodef.musica.crud.album

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityCrearAlbumBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadGenero
import com.example.proyectointegradodef.musica.filtros.ListAutorAdapter
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


class CrearAlbumActivity : AppCompatActivity() {

    lateinit var binding: ActivityCrearAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var adapterAutor: ListAutorAdapter
    var introAutor: MutableList<ReadAutor> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introGenero: MutableList<ReadGenero> = ArrayList()
    var introId : List<String> = ArrayList()

    var imagen: Uri = "".toUri()
    var nombre = ""
    var descripcion = ""
    var autor = ""
    val PERMISO_CODE_FICHERO = 200
    val PICK_IMAGE_REQUEST = 100
    var crearId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        storage = Firebase.storage
        initDb()
        recogerDatosAutor()
        recogerDatosGenero()
        listeners()
    }

    private fun listeners() {
        binding.btnResetAlbum.setOnClickListener {
            limpiar()
        }
        binding.ibAlbumPortada.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero()
            }
        }
        binding.btnCrearAlbum.setOnClickListener {
            //if(comprobarCampos()){
            //    buscarId()
            //}

            autor = binding.spAutorAlbum.editText!!.text.toString()
            Log.d("----------------------", autor)
        }

        binding.dropdown.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            Toast.makeText(
                this,
                adapterAutor.getItem(position)!!.id.toString(),
                Toast.LENGTH_SHORT
            ).show()
        })


    }

    private fun setSpinners(){
        introAutor = ArrayList(introAutor.sortedWith(compareBy { it.nombre }))
        introId = introAutor.map { it.nombre }
        adapterAutor = ListAutorAdapter(this, R.layout.list_item, introAutor as ArrayList<ReadAutor>)
        (binding.spAutorAlbum.editText as? AutoCompleteTextView)?.setAdapter(adapterAutor)
    }

    private fun limpiar() {
        binding.etNombreAlbum.text.clear()
        binding.etDescripcionAlbum.text.clear()
        binding.ibAlbumPortada.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.default_album
            ))
        imagen = "".toUri()
        nombre = ""
        descripcion = ""
        crearId = 0
    }

    private fun comprobarCampos(): Boolean{
        if(binding.etNombreAlbum.text.isEmpty()){
            Toast.makeText(this, "Tienes que poner un nombre", Toast.LENGTH_LONG).show()
            return false
        }else{
            nombre = binding.etNombreAlbum.text.toString()
        }
        if(binding.etDescripcionAlbum.text.isEmpty()){
            descripcion = "No hay descripción disponible"
        }else{
            descripcion = binding.etDescripcionAlbum.text.toString()
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
            binding.ibAlbumPortada.setImageURI(filePath)
        }
    }

    fun cogerFichero(){
        val i = Intent(Intent.ACTION_PICK)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(i, PICK_IMAGE_REQUEST)
    }

    private fun recogerDatosGenero(){
        introGenero.clear()
        referenceGenero.get()
        referenceGenero.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introGenero.clear()
                for(messageSnapshot in snapshot.children){
                    val genero = messageSnapshot.getValue<ReadGenero>(ReadGenero::class.java)
                    if(genero != null){
                        introGenero.add(genero)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun recogerDatosAutor(){
        introAutor.clear()
        referenceAutor.get()
        referenceAutor.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAutor.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAutor>(ReadAutor::class.java)
                    if(tema != null){
                        introAutor.add(tema)
                    }
                }
                setSpinners()
                //binding.loadingPanel.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun buscarId() {
        referenceAlbum.get()
        var query = referenceAlbum.orderByChild("id").limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        crearId = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)!!.id + 1
                        annadirAlbum()
                        return
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun annadirAlbum() {
        val storageRef = storage.reference
        var randomString = UUID.randomUUID().toString()
        val imageRef = storageRef.child("proyecto/album/${randomString}.png")
        val uploadTask = imageRef.putFile(imagen)
        if(imagen.toString().equals("")){
            var ruta = "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"
            referenceAlbum.child(randomString).setValue(ReadAutor(crearId, nombre, ruta, descripcion))
            Toast.makeText(this, "Se ha subido el album correctamente", Toast.LENGTH_LONG).show()
            limpiar()
        }else {
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
            }.addOnCompleteListener {
                var ruta =
                    "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/$randomString"
                referenceAlbum.child(randomString)
                    .setValue(ReadAutor(crearId, nombre, ruta, descripcion))
                Toast.makeText(this, "Se ha subido el album correctamente", Toast.LENGTH_LONG)
                    .show()
                limpiar()
            }
        }

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
        referenceGenero = db.getReference("generos")
    }
}