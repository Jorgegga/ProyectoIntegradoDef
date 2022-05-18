package com.example.proyectointegradodef.musica.album

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityAlbumBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadMusica
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class AlbumActivity : AppCompatActivity() {
    lateinit var binding: ActivityAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    var storageFire = FirebaseStorage.getInstance()

    var music: MutableList<ReadMusica> = ArrayList()

    var album_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initDb()
        introducirDatos()
        recogerDatosMusica()

    }

    fun introducirDatos(){
        val bundle = intent.extras
        album_id = bundle!!.getInt("id", 0)
        val gsReference2 = storageFire.getReferenceFromUrl(bundle!!.getString("portada", "Default") + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivAlbum)
        binding.tvTituloAlbum.text = bundle!!.getString("titulo", "Default")

    }

    fun recogerDatosMusica(){
        music.clear()
        referenceMusic.get()
        referenceMusic.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                music.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)
                    if(tema != null){
                        music.add(tema)
                    }
                }
                rellenarDatos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun rellenarDatos(){
        var musicaAgrupada = music.groupBy { it.album_id }
        music = musicaAgrupada[album_id] as ArrayList
        for(x in music){
            Log.d("aaaaaaaaaaaaaaaaaaaaa", x.nombre)
        }
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
        referenceMusic = db.getReference("music")
    }

    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}