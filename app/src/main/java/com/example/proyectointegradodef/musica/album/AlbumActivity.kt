package com.example.proyectointegradodef.musica.album

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityAlbumBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AlbumActivity : AppCompatActivity() {
    lateinit var binding: ActivityAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    var storageFire = FirebaseStorage.getInstance()

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
    }

    fun introducirDatos(){
        val bundle = intent.extras

        val gsReference2 = storageFire.getReferenceFromUrl(bundle!!.getString("portada", "Default") + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivAlbum)
        binding.tvTituloAlbum.text = bundle!!.getString("titulo", "Default")

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("albums")
        reference2 = db.getReference("autors")
    }

    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}