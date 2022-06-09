package com.example.proyectointegradodef.musica.crud.music

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityCrearMusicBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadGenero
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.musica.filtros.ListAutorAdapter
import com.example.proyectointegradodef.musica.filtros.ListGeneroAdapter
import com.example.proyectointegradodef.musica.filtros.ListAlbumAdapter
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class CrearMusicActivity : AppCompatActivity() {

    lateinit var binding: ActivityCrearMusicBinding
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
    var autor = 0
    var genero = 0
    var album = 0
    var numCancion = 0
    val PERMISO_CODE_FICHERO = 200
    val PICK_IMAGE_REQUEST = 100
    val PICK_AUDIO_REQUEST = 150
    var crearId = 0
    var rutaImagen = ""
    var rutaAudio = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearMusicBinding.inflate(layoutInflater)
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
        listeners()
    }

    private fun listeners() {
        binding.btnResetMusic.setOnClickListener {
            limpiar()
        }
        binding.ibMusicPortada.setOnClickListener {
            if(isPermisosConcedidosFichero()){
                cogerFichero()
            }else{
                permisosFichero()
            }
        }
        binding.btnCrearMusic.setOnClickListener {
            if(comprobarCampos()){
                buscarId()
            }
        }

        binding.btnCancion.setOnClickListener {
            subirMusica()
        }

        binding.ddAutorMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                autor = adapterAutor.getItem(position)!!.id
            }

        binding.ddGeneroMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                genero = adapterGenero.getItem(position)!!.id
            }

        binding.ddAlbumMusic.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                album = adapterAlbum.getItem(position)!!.id
                buscarAlbum()
            }

    }

    private fun limpiar() {
        binding.etNombreMusic.text.clear()
        binding.etDescripcionMusic.text.clear()
        binding.spGeneroMusic.editText!!.text.clear()
        binding.spAutorMusic.editText!!.text.clear()
        binding.spAlbumMusic.editText!!.text.clear()
        binding.ibMusicPortada.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.default_album
            ))
        binding.etNumeroCancion.text.clear()
        imagen = "".toUri()
        nombre = ""
        descripcion = ""
        crearId = 0
        autor = 0
        genero = 0
        album = 0
        numCancion = 0
        audio = "".toUri()
        rutaImagen = ""
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

    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        return true
    }

    private fun buscarId() {
        referenceMusic.get()
        var query = referenceMusic.orderByChild("id").limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        crearId = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)!!.id + 1
                        annadirMusic()
                        return
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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
        if(autor == 0){
            autor = 1
        }
        if(genero == 0){
            genero = 1
        }
        if(album == 0){
            album = 1
        }
        return true
    }

    private fun buscarAlbum(){
        var albumAgrupado = introAlbum.groupBy { it.id }
        var albumTemp = albumAgrupado[album]!![0]
        if(binding.spAutorMusic.editText!!.text.isEmpty()){
            var autorAgrupado = introAutor.groupBy { it.id }
            var autorTemp = autorAgrupado[albumTemp.autor_id]!![0]
            binding.spAutorMusic.editText!!.setText(autorTemp.nombre)
            autor = albumTemp.autor_id
        }
        if(binding.spGeneroMusic.editText!!.text.isEmpty()){
            var generoAgrupado = introGenero.groupBy { it.id }
            var generoTemp = generoAgrupado[albumTemp.genero_id]!![0]
            binding.spGeneroMusic.editText!!.setText(generoTemp.nombre)
            genero = albumTemp.genero_id
        }
        if(imagen == "".toUri()){
            rutaImagen = albumTemp.portada
        }
    }

    private fun annadirMusic() {
        val storageRef = storage.reference
        var randomString = UUID.randomUUID().toString()
        if(audio.toString() == ""){
            rutaAudio = "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/default"
        }else{
            rutaAudio = "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/$randomString"
            val musicRef = storageRef.child("proyecto/musica/$randomString.mp3")
            val uploadTask = musicRef.putFile(audio)
            uploadTask.addOnFailureListener{
                Toast.makeText(this, resources.getString(R.string.noSeHaPodidoSubirElArchivo), Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }.addOnSuccessListener {
                Toast.makeText(this, resources.getString(R.string.audioSubido), Toast.LENGTH_LONG).show()
            }
        }
        if(imagen.toString() == "" && rutaImagen == ""){
            var ruta = "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"
            referenceMusic.child(randomString).setValue(ReadMusica(nombre, autor, album, descripcion, genero, crearId, numCancion, ruta, rutaAudio))
            Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG).show()
        }else if(rutaImagen != "") {
            referenceMusic.child(randomString)
                .setValue(ReadMusica(nombre, autor, album, descripcion, genero, crearId, numCancion, rutaImagen, rutaAudio))
            Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                .show()
        }else if(imagen.toString() != ""){
            val imageRef = storageRef.child("proyecto/musica/portada/${randomString}.png")
            val uploadTask = imageRef.putFile(imagen)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "No se ha podido subir la imagen", Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }.addOnCompleteListener {
                var ruta =
                    "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/$randomString"
                referenceMusic.child(randomString)
                    .setValue(ReadMusica(nombre, autor, album, descripcion, genero, crearId, numCancion, ruta, rutaAudio))
                Toast.makeText(this, "Se ha subido la canción correctamente", Toast.LENGTH_LONG)
                    .show()
            }
        }

        limpiar()

    }


    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
        referenceGenero = db.getReference("generos")
        referenceMusic = db.getReference("music")
    }
}