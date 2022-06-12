package com.example.proyectointegradodef.musica.crud.album

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityUpdateAlbumBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadGenero
import com.example.proyectointegradodef.musica.filtros.ListAutorAdapter
import com.example.proyectointegradodef.musica.filtros.ListGeneroAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class UpdateAlbumActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var adapterAutor: ListAutorAdapter
    lateinit var adapterGenero: ListGeneroAdapter
    var introAutor: MutableList<ReadAutor> = ArrayList()
    var introGenero: MutableList<ReadGenero> = ArrayList()

    var imagen: Uri = "".toUri()
    var foto = ""
    var titulo = ""
    var descripcion = ""
    var autor_id_temp = 0
    var autor_id = 0
    var autor = ""
    var genero_id_temp = 0
    var genero_id = 0
    var genero = ""
    var crearId = 0
    var updateHecho = false
    var key = ""
    val PERMISO_CODE_FICHERO = 200
    val PICK_IMAGE_REQUEST = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        storage = Firebase.storage
        initDb()
        recogerDatosAutor()
        recogerDatosGenero()
        recuperarDatos()
        listeners()
    }

    private fun listeners() {
        binding.btnResetAlbum.setOnClickListener {
            recuperarDatos()
        }
        binding.ibAlbumPortada.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero()
            }
        }
        binding.btnCrearAlbum.setOnClickListener {
            if(comprobarCampos()){
                binding.loadingPanel.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                buscarId()
            }

        }

        binding.ddAutorAlbum.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                autor_id = adapterAutor.getItem(position)!!.id
            }
        binding.ddGeneroAlbum.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                genero_id = adapterGenero.getItem(position)!!.id
            }

    }

    fun recuperarDatos(){
        val bundle = intent.extras
        if(!updateHecho) {
            crearId = bundle!!.getInt("id", 0)
            autor_id = bundle.getInt("autor_id", 0)
            autor = bundle.getString("autor", "Default")
            titulo = bundle.getString("titulo", "Default")
            foto = bundle.getString("portada", "Default")
            descripcion = bundle.getString("descripcion", "No hay descripción disponible")
            genero_id = bundle.getInt("genero_id", 0)
            genero = bundle.getString("genero", "Default")
            autor_id_temp = autor_id
            genero_id_temp = genero_id
        }
        if(foto == "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"){
            binding.ibAlbumPortada.setImageDrawable(AppCompatResources.getDrawable(
                this,
                R.drawable.default_album))
        }else{
            val gsReference2 = storage.getReferenceFromUrl("$foto.png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ibAlbumPortada)
        }
        binding.etNombreAlbum.setText(titulo)
        binding.etDescripcionAlbum.setText(descripcion)
        binding.spAutorAlbum.editText!!.setText(autor)
        binding.spGeneroAlbum.editText!!.setText(genero)
        autor_id = autor_id_temp
        genero_id = genero_id_temp
        imagen = "".toUri()
    }

    private fun setSpinnerAutor(){
        introAutor = ArrayList(introAutor.sortedWith(compareBy { it.nombre }))
        adapterAutor = ListAutorAdapter(this, R.layout.list_item, introAutor as ArrayList<ReadAutor>)
        (binding.spAutorAlbum.editText as? AutoCompleteTextView)?.setAdapter(adapterAutor)
    }

    private fun setSpinnerGenero(){
        introGenero = ArrayList(introGenero.sortedWith(compareBy { it.nombre }))
        adapterGenero = ListGeneroAdapter(this, R.layout.list_item, introGenero as ArrayList<ReadGenero>)
        (binding.spGeneroAlbum.editText as? AutoCompleteTextView)?.setAdapter(adapterGenero)
    }

    private fun comprobarCampos(): Boolean{
        if(binding.etNombreAlbum.text.isEmpty()){
            Toast.makeText(this, "Tienes que poner un nombre", Toast.LENGTH_LONG).show()
            return false
        }else{
            titulo = binding.etNombreAlbum.text.toString()
        }
        if(binding.etDescripcionAlbum.text.isEmpty()){
            descripcion = "No hay descripción disponible"
        }else{
            descripcion = binding.etDescripcionAlbum.text.toString()
        }
        if(autor_id == 0){
            autor_id = 1
            autor = "default"
        }else{
            autor = binding.spAutorAlbum.editText!!.text.toString()
        }
        if(genero_id == 0){
            genero_id = 1
            genero = "default"
        }else{
            genero = binding.spGeneroAlbum.editText!!.text.toString()
        }
        autor_id_temp = autor_id
        genero_id_temp = genero_id
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
                setSpinnerGenero()
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
                setSpinnerAutor()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun buscarId() {
        referenceAlbum.get()
        var query = referenceAlbum.orderByChild("id").equalTo(crearId.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    for (messageSnapshot in snapshot.children) {
                        key = messageSnapshot.key.toString()
                        actualizarAlbum()
                        return
                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun actualizarAlbum() {
        val storageRef = storage.reference
        if(imagen.toString() == ""){
            referenceAlbum.child(key).setValue(ReadAlbum(crearId, autor_id, titulo, foto, descripcion, genero_id))
            Toast.makeText(this, "Se ha actualizado el album correctamente", Toast.LENGTH_LONG).show()
            updateHecho = true
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.loadingPanel.visibility = View.GONE
            recuperarDatos()
        }else {
            val imageRef = storageRef.child("proyecto/album/${key}.png")
            val uploadTask = imageRef.putFile(imagen)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.loadingPanel.visibility = View.GONE
            }.addOnCompleteListener {
                var ruta =
                    "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/$key"
                referenceAlbum.child(key)
                    .setValue(ReadAlbum(crearId, autor_id, titulo, ruta, descripcion, genero_id))
                Toast.makeText(this, "Se ha actualizado el album correctamente", Toast.LENGTH_LONG)
                    .show()
                updateHecho = true
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                binding.loadingPanel.visibility = View.GONE
                recuperarDatos()
            }
        }

    }

    override fun onSupportNavigateUp() : Boolean{
        setResult(Activity.RESULT_OK)
        finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        return true
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
        referenceGenero = db.getReference("generos")
    }
}