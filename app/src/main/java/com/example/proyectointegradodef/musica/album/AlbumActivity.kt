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
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.musica.music.MusicaAdapter
import com.example.proyectointegradodef.preferences.AppUse
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AlbumActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var referencePlaylist: DatabaseReference

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
    var introPlaylist: MutableList<ReadPlaylist> = ArrayList()
    var ruta = ""
    var reproducir = false
    var crearId = 0

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
        }, {
            MaterialAlertDialogBuilder(this)
                .setTitle("Añadir a tu playlist")
                .setMessage("¿Quieres añadir la cancion " + it.nombre + " de tu playlist?")
                .setNeutralButton("Cancelar") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(this, "No se ha añadido la canción a tu playlist", Toast.LENGTH_LONG).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    comprobarExistePlaylist(it.id)

                }
                .show()

        })
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

    private fun buscarId(music: Int){
        introPlaylist.clear()
        referencePlaylist.get()
        referencePlaylist.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                introPlaylist.clear()
                if(crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        var playlist = messageSnapshot.getValue<ReadPlaylist>(ReadPlaylist::class.java)
                        if (playlist != null) {
                            introPlaylist.add(playlist)
                        }
                    }
                    filtrarDatosPlaylist(music)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun filtrarDatosPlaylist(music: Int){
        if(crearId == 0) {
            var tempPlaylist = introPlaylist.maxByOrNull { it.id }
            crearId = tempPlaylist!!.id + 1
            var randomString = UUID.randomUUID().toString()
            referencePlaylist.child(randomString).setValue(ReadPlaylist(crearId, music, AppUse.user_id))
        }
    }

    private fun comprobarExistePlaylist(objectId: Int){
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("user_id").equalTo(AppUse.user_id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children){
                    if(messageSnapshot.child("music_id").value.toString() == objectId.toString()){
                        Toast.makeText(
                            applicationContext,
                            "Esa cancion ya esta en tu playlist",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }
                crearId = 0
                buscarId(objectId)
                Toast.makeText(
                    applicationContext,
                    "Se ha añadido la cancion a tu playlist",
                    Toast.LENGTH_LONG
                ).show()
                recogerDatosMusica()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
        referenceMusic = db.getReference("music")
        referencePlaylist = db.getReference("playlists")
    }

    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}