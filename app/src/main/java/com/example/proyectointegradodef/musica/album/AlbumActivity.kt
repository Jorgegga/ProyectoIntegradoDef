package com.example.proyectointegradodef.musica.album

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityAlbumBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAutorId
import com.example.proyectointegradodef.models.ReadMusica
import com.example.proyectointegradodef.models.ReadMusicaAlbumAutor
import com.example.proyectointegradodef.musica.music.MusicaAdapter
import com.example.proyectointegradodef.preferences.AppUse
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class AlbumActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceMusic: DatabaseReference

    lateinit var player: ExoPlayer
    lateinit var renderersFactory: DefaultRenderersFactory
    lateinit var trackSelectionFactory: AdaptiveTrackSelection.Factory
    lateinit var trackSelectSelector: DefaultTrackSelector
    lateinit var loadControl : DefaultLoadControl
    lateinit var dataSourceFactory: DefaultDataSourceFactory
    lateinit var extractorsFactory: DefaultExtractorsFactory

    var storageFire = FirebaseStorage.getInstance()
    var music: MutableList<ReadMusica> = ArrayList()
    var album_id = 0
    var recyclerVacio = true
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introTotal: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var ruta = ""
    var reproducir = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        renderersFactory = DefaultRenderersFactory(this)
        trackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelectSelector = DefaultTrackSelector(this, trackSelectionFactory)
        loadControl = DefaultLoadControl()
        player = ExoPlayer.Builder(this).build()
        player.addListener(this)
        dataSourceFactory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        extractorsFactory = DefaultExtractorsFactory()

        initDb()
        introducirDatos()
        recogerDatosMusica()
        recogerDatosAutor()

    }

    fun introducirDatos(){
        val bundle = intent.extras
        album_id = bundle!!.getInt("id", 0)
        val gsReference2 = storageFire.getReferenceFromUrl(bundle!!.getString("portada", "Default") + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivAlbum)
        binding.tvTituloAlbum.text = bundle!!.getString("titulo", "Default")
        binding.tvAutorAlbum.text = bundle!!.getString("autor", "Default")

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
                filtrarDatos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onPause() {
        super.onPause()
        if(reproducir){
            player.playWhenReady = false
            reproducir = true
        }

    }

    override fun onResume() {
        super.onResume()
        if(reproducir) {
            player.playWhenReady = true
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        reproducir = isPlaying
    }

    private fun recogerDatosAutor(){
        introAutor.clear()
        referenceAutor.get()
        referenceAutor.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                introAutor.clear()
                for(messageSnapshot in snapshot.children){
                    val music = messageSnapshot.getValue<ReadAutorId>(ReadAutorId::class.java)
                    if(music != null){
                        introAutor.add(music)
                    }
                }
                filtrarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun filtrarDatos(){
        var musicaAgrupada = music.groupBy { it.album_id }
        if(musicaAgrupada[album_id] != null) {
            music = musicaAgrupada[album_id] as ArrayList
            recyclerVacio = false
        }else {
            recyclerVacio = true
        }
        rellenarDatos()
    }

    private fun rellenarDatos(){
        introTotal.clear()
        if(!recyclerVacio){
        for (x in music) {
            //var alb : ReadAlbum? = introAlbum.find{it.id == x.album_id}
            var aut: ReadAutorId? = introAutor.find { it.id == x.autor_id }
            var temp: ReadMusicaAlbumAutor
            if (aut != null) {
                temp = ReadMusicaAlbumAutor(
                    x.id,
                    x.nombre,
                    x.album_id,
                    "",
                    x.autor_id,
                    aut.nombre,
                    x.ruta,
                    x.portada
                )
            } else {
                temp = ReadMusicaAlbumAutor(
                    x.id,
                    "default",
                    x.album_id,
                    "",
                    x.autor_id,
                    "default",
                    x.ruta,
                    x.portada
                )
            }
            introTotal.add(temp)
        }
        }
        /*if(AppUse.id != 0) {
            var tempMusic = introMusic.find { it.id == AppUse.id }
            var tempAutor = introAutor.find { it.id == tempMusic!!.autor_id }
            if (tempMusic != null) {
                actualizarReproductorCancion(tempMusic)
            }
            if (tempAutor != null) {
                actualizarReproductorAutor(tempAutor)
            }
        }*/
        //binding.loadingPanel.visibility = View.GONE
        setRecycler(introTotal as ArrayList<ReadMusicaAlbumAutor>)
    }

    fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(this)
        val musicaAdapter = MusicaAdapter(lista,{
            ruta = it.ruta
            reproducir()
        }, { Toast.makeText(this, "Click largo en album activity", Toast.LENGTH_LONG).show()})
        binding.recyclerViewAlbum.adapter = musicaAdapter
        binding.recyclerViewAlbum.layoutManager = linearLayoutManager
        binding.recyclerViewAlbum.scrollToPosition(0)
    }

    private fun reproducir() {
        try {
            var audioUrl = ruta
            var storageRef = storageFire.getReferenceFromUrl("$audioUrl.mp3")
            storageRef.downloadUrl.addOnSuccessListener() {
                var url = it.toString()
                var mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
                player.setMediaSource(mediaSource)
                player.prepare()
                player.playWhenReady = true
                binding.videoView.player = player
                binding.videoView.useArtwork = false
                //binding.tvAutorReproductor.text = autor
                //binding.tvNombreReproductor.text = nombre
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("Escuchando audio...", "Escuchando audio...")
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