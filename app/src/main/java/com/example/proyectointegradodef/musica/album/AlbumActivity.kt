package com.example.proyectointegradodef.musica.album

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.InicioActivity
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityAlbumBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.preferences.AppUse
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Album activity
 *
 * @constructor Create empty Album activity
 */
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
    var introMusic: MutableList<ReadMusica> = ArrayList()
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introTotal: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var introPlaylist: MutableList<AnnadirPlaylistMusic> = ArrayList()
    var album_id = 0
    var recyclerVacio = true
    var ruta = ""
    var idSong = 0
    var reproducir = false
    var crearId = 0
    var cambioMusic = false
    var nombre = ""
    var autor = ""
    var album = ""

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setHomeButtonEnabled(true);

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

    /**
     * On create options menu
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * On options item selected
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homeButton->{
                val i = Intent(this, InicioActivity::class.java)
                startActivity(i)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * On pause
     *
     */
    override fun onPause() {
        super.onPause()
        if(reproducir){
            player.playWhenReady = false
            reproducir = true
        }

    }

    /**
     * On resume
     *
     */
    override fun onResume() {
        super.onResume()
        if(reproducir) {
            player.playWhenReady = true
        }
    }

    /**
     * On is playing changed
     *
     * @param isPlaying
     */
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        reproducir = isPlaying
    }

    /**
     * On tracks changed
     *
     * @param trackGroups
     * @param trackSelections
     */
    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
        super.onTracksChanged(trackGroups, trackSelections)
        nombre =  player.mediaMetadata.title.toString()
        album = player.mediaMetadata.albumTitle.toString()
        autor = player.mediaMetadata.artist.toString()
        findViewById<TextView>(R.id.tv_player_nombre).text = "$nombre - $album - $autor"
    }

    /**
     * Introducir datos
     *
     */
    fun introducirDatos(){
        val bundle = intent.extras
        album_id = bundle!!.getInt("id", 0)
        val gsReference2 = storageFire.getReferenceFromUrl(bundle!!.getString("portada", "Default") + ".png")
        val option = RequestOptions().error(R.drawable.default_album)
        GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivAlbum)
        binding.tvTituloAlbum.text = bundle!!.getString("titulo", "Default")
        binding.tvAutorAlbum.text = bundle!!.getString("autor", "Default")

    }

    /**
     * Recoger datos musica
     *
     */
    fun recogerDatosMusica(){
        introMusic.clear()
        referenceMusic.get()
        referenceMusic.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introMusic.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)
                    if(tema != null){
                        introMusic.add(tema)
                    }
                }
                cambioMusic = true
                rellenarDatos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    /**
     * Recoger datos autor
     *
     */
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
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    /**
     * Recoger playlist
     *
     * @param music
     */
    suspend fun recogerPlaylist(music: ReadMusicaAlbumAutor) {
        var storageRef = storageFire.getReferenceFromUrl(music!!.ruta + ".mp3")
        storageRef.downloadUrl.addOnSuccessListener() {
            var metadata = MediaMetadata.Builder().setTitle(music.nombre).setAlbumTitle(music.album).setArtist(music.autor).build()
            var mediaItem = MediaItem.Builder().setUri(it).setMediaMetadata(metadata).build()
            introPlaylist.add(AnnadirPlaylistMusic(music.id, music.numCancion, mediaItem))
        }.await()
    }

    /**
     * Rellenar playlist
     *
     */
    fun rellenarPlaylist() {
        introPlaylist.sortBy { it.numCancion }
        var arrayMediaItems = introPlaylist.map { it.ruta }
        player.addMediaItems(arrayMediaItems)
    }

    /**
     * Filtrar datos
     *
     */
    fun filtrarDatos(){
        var musicaAgrupada = introMusic.groupBy { it.album_id }
        if(musicaAgrupada[album_id] != null) {
            introMusic = musicaAgrupada[album_id] as ArrayList
            introMusic.sortBy { it.numCancion }
            recyclerVacio = false
        }else {
            recyclerVacio = true
        }
    }

    /**
     * Buscar cancion
     *
     * @param id
     * @return
     */
    fun buscarCancion(id: Int): Int {
        return introPlaylist.indexOfFirst { it.id == id }
    }

    /**
     * Rellenar datos
     *
     */
    private fun rellenarDatos() {
        if (introMusic.isNotEmpty() && introAutor.isNotEmpty()) {
            filtrarDatos()
            if (!recyclerVacio) {
                introTotal.clear()
                if (cambioMusic) {
                    player.clearMediaItems()
                    introPlaylist.clear()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    for (x in introMusic) {

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
                                x.portada,
                                x.descripcion,
                                x.genero_id,
                                x.numCancion
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
                                x.portada,
                                x.descripcion,
                                x.genero_id,
                                x.numCancion
                            )
                        }
                        if (cambioMusic) {
                            recogerPlaylist(temp)
                        }
                        introTotal.add(temp)
                    }
                    cambioMusic = false
                    rellenarPlaylist()
                    if (idSong != 0) {
                        var tempMusic = introTotal.find { it.id == idSong }
                        if (tempMusic != null) {
                            actualizarReproductor(tempMusic)
                        }
                    }

                    setRecycler(introTotal as ArrayList<ReadMusicaAlbumAutor>)
                }
            }else{
                binding.loadingPanel.visibility = View.GONE
                Toast.makeText(this, R.string.albumVacio, Toast.LENGTH_LONG).show()
            }

        }
    }

    /**
     * Actualizar reproductor
     *
     * @param x
     */
    private fun actualizarReproductor(x: ReadMusicaAlbumAutor?) {
        if (findViewById<TextView>(R.id.tv_player_nombre).text != "") {
            findViewById<TextView>(R.id.tv_player_nombre).text = x!!.nombre + " - " + x.album + " - " + x.autor
        }
    }

    /**
     * Set recycler
     *
     * @param lista
     */
    fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(this)
        val musicaAdapter = AlbumActivityAdapter(lista,{
            ruta = it.ruta
            idSong = it.id
            nombre = it.nombre
            album = it.album
            autor = it.autor
            reproducir()
        }, {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.annadirPlaylist)
                .setMessage(resources.getString(R.string.annadirPlaylistPregunta, it.nombre))
                .setNeutralButton(R.string.cancelar) { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton(R.string.rechazar) { dialog, which ->
                    Toast.makeText(this, R.string.annadirPlaylistRefuse, Toast.LENGTH_LONG).show()
                }
                .setPositiveButton(R.string.aceptar) { dialog, which ->
                    comprobarExistePlaylist(it.id)
                }
                .show()

        })
        binding.loadingPanel.visibility = View.GONE
        binding.recyclerViewAlbum.adapter = musicaAdapter
        binding.recyclerViewAlbum.layoutManager = linearLayoutManager
        binding.recyclerViewAlbum.scrollToPosition(0)
    }

    private fun reproducir() {
        try {
            player.seekTo(buscarCancion(idSong), 0)
            player.prepare()
            player.playWhenReady = true
            binding.videoView.player = player
            binding.videoView.useArtwork = false
            findViewById<TextView>(R.id.tv_player_nombre).isSelected = true
            findViewById<TextView>(R.id.tv_player_nombre).text = "$nombre - $album - $autor"
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun buscarId(music: Int) {
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("id").limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        crearId =
                            messageSnapshot.getValue<ReadPlaylist>(ReadPlaylist::class.java)!!.id + 1
                        filtrarDatosPlaylist(music)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun filtrarDatosPlaylist(music: Int) {
        if (crearId != 0) {
            var randomString = UUID.randomUUID().toString()
            referencePlaylist.child(randomString)
                .setValue(ReadPlaylist(crearId, music, AppUse.user_id))
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
                            R.string.cancionRepetida,
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }
                crearId = 0
                buscarId(objectId)
                Toast.makeText(
                    applicationContext,
                    R.string.cancionAnnadida,
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