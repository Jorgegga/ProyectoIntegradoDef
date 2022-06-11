package com.example.proyectointegradodef.musica.crud.music

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.example.proyectointegradodef.databinding.ActivityUpdateMusicBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadGenero
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.musica.filtros.ListAlbumAdapter
import com.example.proyectointegradodef.musica.filtros.ListAutorAdapter
import com.example.proyectointegradodef.musica.filtros.ListGeneroAdapter
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class UpdateMusicActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateMusicBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var adapterAutor: ListAutorAdapter
    lateinit var adapterGenero: ListGeneroAdapter
    lateinit var adapterAlbum: ListAlbumAdapter
    var introAutor: MutableList<ReadAutor> = ArrayList()
    var introGenero: MutableList<ReadGenero> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()

    var imagen: Uri = "".toUri()
    var audio: Uri = "".toUri()
    var nombre = ""
    var descripcion = ""
    var autor_id_temp = 0
    var autor_id = 0
    var autor = ""
    var genero_id_temp = 0
    var genero_id = 0
    var genero = ""
    var album_id_temp = 0
    var album_id = 0
    var album = ""
    var numCancion = 0
    val PERMISO_CODE_FICHERO = 200
    val PERMISO_CODE_MUSICA = 250
    val PICK_IMAGE_REQUEST = 100
    val PICK_AUDIO_REQUEST = 150
    var crearId = 0
    var rutaImagen = ""
    var rutaAudio = ""
    var updateHecho = false
    var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        storage = Firebase.storage
        initDb()
        recogerDatosAlbum()
        recogerDatosAutor()
        recogerDatosGenero()
        recuperarDatos()
        listeners()
    }

    private fun listeners() {
        binding.btnResetMusic.setOnClickListener {
            recuperarDatos()
        }
        binding.ibMusicPortada.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero(PERMISO_CODE_FICHERO)
            }
        }
        binding.btnUpdateMusic.setOnClickListener {
            if(comprobarCampos()){
                buscarId()
            }
        }

        binding.btnCancion.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                subirMusica()
            }else{
                permisosFichero(PERMISO_CODE_MUSICA)
            }

        }

        binding.ddAutorMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                autor_id = adapterAutor.getItem(position)!!.id
            }

        binding.ddGeneroMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                genero_id = adapterGenero.getItem(position)!!.id
            }

        binding.ddAlbumMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                album_id = adapterAlbum.getItem(position)!!.id
                buscarAlbum()
            }

    }

    fun recuperarDatos(){
        val bundle = intent.extras
        if(!updateHecho) {
            crearId = bundle!!.getInt("id", 0)
            autor_id = bundle.getInt("autor_id", 0)
            autor = bundle.getString("autor", "Default")
            nombre = bundle.getString("nombre", "Default")
            rutaImagen = bundle.getString("portada", "Default")
            descripcion = bundle.getString("descripcion", "No hay descripción disponible")
            genero_id = bundle.getInt("genero_id", 0)
            genero = bundle.getString("genero", "Default")
            album_id = bundle.getInt("album_id", 0)
            album = bundle.getString("album", "Default")
            numCancion = bundle.getInt("numCancion", 0)
            rutaAudio = bundle.getString("ruta", "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/default")
            autor_id_temp = autor_id
            genero_id_temp = genero_id
            album_id_temp = album_id
        }
        if(rutaImagen == "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default" || rutaImagen == "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/portada/default"){
            binding.ibMusicPortada.setImageDrawable(
                AppCompatResources.getDrawable(
                this,
                R.drawable.default_album))
        }else{
            val gsReference2 = storage.getReferenceFromUrl("$rutaImagen.png")
            val option = RequestOptions().error(R.drawable.default_album)
            GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ibMusicPortada)
        }
        binding.etNombreMusic.setText(nombre)
        binding.etDescripcionMusic.setText(descripcion)
        binding.spAutorMusic.editText!!.setText(autor)
        binding.spGeneroMusic.editText!!.setText(genero)
        binding.spAlbumMusic.editText!!.setText(album)
        binding.etNumeroCancion.setText(""+numCancion)
        autor_id = autor_id_temp
        genero_id = genero_id_temp
        album_id = album_id_temp
        imagen = "".toUri()
        audio = "".toUri()
        binding.btnCancion.setBackgroundColor(resources.getColor(R.color.btn_negativo))
    }

    private fun setSpinnerAutor(){
        introAutor = ArrayList(introAutor.sortedWith(compareBy { it.nombre }))
        adapterAutor = ListAutorAdapter(this, R.layout.list_item, introAutor as ArrayList<ReadAutor>)
        (binding.spAutorMusic.editText as? AutoCompleteTextView)?.setAdapter(adapterAutor)
    }

    private fun setSpinnerGenero(){
        introGenero = ArrayList(introGenero.sortedWith(compareBy { it.nombre }))
        adapterGenero = ListGeneroAdapter(this, R.layout.list_item, introGenero as ArrayList<ReadGenero>)
        (binding.spGeneroMusic.editText as? AutoCompleteTextView)?.setAdapter(adapterGenero)
    }

    private fun setSpinnerAlbum(){
        introAlbum = ArrayList(introAlbum.sortedWith(compareBy { it.titulo }))
        adapterAlbum = ListAlbumAdapter(this, R.layout.list_item, introAlbum as ArrayList<ReadAlbum>)
        (binding.spAlbumMusic.editText as? AutoCompleteTextView)?.setAdapter(adapterAlbum)
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
                //binding.loadingPanel.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun recogerDatosAlbum(){
        introAlbum.clear()
        referenceAlbum.get()
        referenceAlbum.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAlbum.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)
                    if(tema != null){
                        introAlbum.add(tema)
                    }
                }
                setSpinnerAlbum()
                //binding.loadingPanel.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun isPermisosConcedidosFichero(): Boolean{
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun permisosFichero(codigo: Int){
        var checkPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkPermisos != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), codigo)
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
            PERMISO_CODE_MUSICA->{
                if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    subirMusica()
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
            binding.ibMusicPortada.setImageURI(filePath)
            rutaImagen = ""
        }else if(requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK){
            var filePath = data!!.data
            audio = filePath!!
            binding.btnCancion.setBackgroundColor(resources.getColor(R.color.btn_positivo))
        }
    }

    fun cogerFichero(){
        val i = Intent(Intent.ACTION_PICK)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(i, PICK_IMAGE_REQUEST)
    }

    private fun subirMusica(){
        val i = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, PICK_AUDIO_REQUEST)
    }

    private fun comprobarCampos(): Boolean{
        if(binding.etNombreMusic.text.isEmpty()){
            Toast.makeText(this, "Tienes que poner un nombre", Toast.LENGTH_LONG).show()
            return false
        }else{
            nombre = binding.etNombreMusic.text.toString()
        }
        if(binding.etDescripcionMusic.text.isEmpty()){
            descripcion = "No hay descripción disponible"
        }else{
            descripcion = binding.etDescripcionMusic.text.toString()
        }
        if(autor_id == 0){
            autor_id = 1
        }
        if(genero_id == 0){
            genero_id = 1
        }
        if(album_id == 0){
            album_id = 1
        }
        if(binding.etNumeroCancion.text.isNotEmpty()){
            numCancion = binding.etNumeroCancion.text.toString().toInt()
        }
        return true
    }

    private fun buscarAlbum(){
        var albumAgrupado = introAlbum.groupBy { it.id }
        var albumTemp = albumAgrupado[album_id]!![0]
        if(binding.spAutorMusic.editText!!.text.isEmpty()){
            var autorAgrupado = introAutor.groupBy { it.id }
            var autorTemp = autorAgrupado[albumTemp.autor_id]!![0]
            binding.spAutorMusic.editText!!.setText(autorTemp.nombre)
            autor_id = albumTemp.autor_id
        }
        if(binding.spGeneroMusic.editText!!.text.isEmpty()){
            var generoAgrupado = introGenero.groupBy { it.id }
            var generoTemp = generoAgrupado[albumTemp.genero_id]!![0]
            binding.spGeneroMusic.editText!!.setText(generoTemp.nombre)
            genero_id = albumTemp.genero_id
        }
        if(imagen == "".toUri()){
            rutaImagen = albumTemp.portada
        }
    }

    private fun buscarId() {
        referenceMusic.get()
        var query = referenceMusic.orderByChild("id").equalTo(crearId.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    key = messageSnapshot.key.toString()
                    actualizarMusic()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun actualizarMusic() {
        val storageRef = storage.reference
        if(audio.toString() == ""){
            if(imagen.toString() == "" && rutaImagen == ""){
                referenceMusic.child(key).setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, rutaImagen, rutaAudio))
                Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG).show()
                updateHecho = true
                recuperarDatos()
            }else if(rutaImagen != "") {
                referenceMusic.child(key)
                    .setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, rutaImagen, rutaAudio))
                Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                    .show()
                updateHecho = true
                recuperarDatos()
            }else if(imagen.toString() != ""){
                val imageRef = storageRef.child("proyecto/musica/portada/${key}.png")
                val uploadTask = imageRef.putFile(imagen)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
                    return@addOnFailureListener
                }.addOnCompleteListener {
                    rutaImagen =
                        "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/portada/$key"
                    referenceMusic.child(key)
                        .setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, rutaImagen, rutaAudio))
                    Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                        .show()
                    updateHecho = true
                    recuperarDatos()
                }
            }
        }else{
            rutaAudio = "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/$key"
            val musicRef = storageRef.child("proyecto/musica/$key.mp3")
            val uploadTask = musicRef.putFile(audio)
            uploadTask.addOnFailureListener{
                Toast.makeText(this, resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }.addOnSuccessListener {
                //Toast.makeText(this, resources.getString(R.string.audioSubido), Toast.LENGTH_LONG).show()
                if(imagen.toString() == "" && rutaImagen == ""){
                    referenceMusic.child(key).setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, rutaImagen, rutaAudio))
                    Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG).show()
                    updateHecho = true
                    recuperarDatos()
                }else if(rutaImagen != "") {
                    referenceMusic.child(key)
                        .setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, rutaImagen, rutaAudio))
                    Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                        .show()
                    updateHecho = true
                    recuperarDatos()
                }else if(imagen.toString() != ""){
                    val imageRef = storageRef.child("proyecto/musica/portada/${key}.png")
                    val uploadTask = imageRef.putFile(imagen)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
                        return@addOnFailureListener
                    }.addOnCompleteListener {
                        var ruta =
                            "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/portada/$key"
                        referenceMusic.child(key)
                            .setValue(ReadMusica(nombre, autor_id, album_id, descripcion, genero_id, crearId, numCancion, ruta, rutaAudio))
                        Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                            .show()
                        updateHecho = true
                        recuperarDatos()
                    }
                }
            }
        }



    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
        referenceGenero = db.getReference("generos")
        referenceMusic = db.getReference("music")
    }

    override fun onSupportNavigateUp() : Boolean{
        setResult(Activity.RESULT_OK)
        finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        return true
    }
}