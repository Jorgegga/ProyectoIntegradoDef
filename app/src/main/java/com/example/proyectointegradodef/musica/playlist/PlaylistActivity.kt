package com.example.proyectointegradodef.musica.playlist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityPlaylistBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.musica.MusicaActivity
import com.example.proyectointegradodef.musica.music.MusicaAdapter
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDatabase
import com.example.proyectointegradodef.room.MusicaRoomAdapter
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Playlist activity
 *
 * @constructor Create empty Playlist activity
 */
class PlaylistActivity : AppCompatActivity(), Player.Listener {

    lateinit var binding: ActivityPlaylistBinding
    lateinit var database: MusicaDatabase
    lateinit var allMusic: List<Musica>
    lateinit var db: FirebaseDatabase
    lateinit var referenceMusic: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var referencePlaylist: DatabaseReference
    lateinit var referencePerfil: DatabaseReference
    lateinit var player: ExoPlayer
    lateinit var dataSourceFactory: DefaultDataSourceFactory
    lateinit var extractorsFactory: DefaultExtractorsFactory
    lateinit var renderersFactory: DefaultRenderersFactory
    lateinit var trackSelectionFactory: AdaptiveTrackSelection.Factory
    lateinit var trackSelectSelector: DefaultTrackSelector
    lateinit var loadControl: DefaultLoadControl

    var storageFire = FirebaseStorage.getInstance()
    var introMusic: MutableList<ReadMusica> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introTotal: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var introPlaylist: MutableList<ReadPlaylist> = ArrayList()
    var musicTemp: MutableList<ReadMusica> = ArrayList()
    var musicFiltrada: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var reproducir = false

    val user = Firebase.auth.currentUser
    var userId = 0
    var nombre = ""
    var autor = ""
    var album = ""
    var idAutor = 0
    var cancion = ""
    var idSong = 0
    var recyclerVacio = false
    var cambioPlaylist = false

    var actualizarRoom : MutableLiveData<List<Musica>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        database = Room.databaseBuilder(this, MusicaDatabase::class.java, "musica_database")
            .allowMainThreadQueries().build()
        renderersFactory = DefaultRenderersFactory(this)
        trackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelectSelector = DefaultTrackSelector(this, trackSelectionFactory)
        loadControl = DefaultLoadControl()
        player = ExoPlayer.Builder(this).build()
        player.addListener(this)
        dataSourceFactory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        extractorsFactory = DefaultExtractorsFactory()

        initDb()
        allMusic = database.MusicaDao().getAllMusic()
        rellenarDatosAlbum()
        rellenarDatosAutor()
        rellenarDatosPlaylist()
        rellenarDatosMusic()
        cambioObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_playlist, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.musicaButton->{
                val i = Intent(this, MusicaActivity::class.java)
                startActivity(i)
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }

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

    override fun onPause() {
        super.onPause()
        if (reproducir) {
            player.playWhenReady = false
            reproducir = true
        }

    }

    override fun onResume() {
        super.onResume()
        if (reproducir) {
            player.playWhenReady = true
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        reproducir = isPlaying
    }

    /**
     * Cambio observer
     *
     */
    fun cambioObserver(){
        actualizarRoom.observe(this, Observer {
            allMusic = actualizarRoom.value!!
        })
    }

    /**
     * Reproducir
     *
     */
    fun reproducir() {
        try {
            player.seekTo(AppUse.recyclerPosition, 0)
            player.prepare()
            player.playWhenReady = true
            binding.videoView.player = player
            binding.videoView.useArtwork = false
            findViewById<TextView>(R.id.tv_player_nombre).isSelected = true
            findViewById<TextView>(R.id.tv_player_nombre).text = "$nombre - $album - $autor"
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("Escuchando audio...", "Escuchando audio...")
    }

    private fun rellenarDatosPlaylist() {
        introPlaylist.clear()
        referencePlaylist.get()
        referencePlaylist.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introPlaylist.clear()
                cambioPlaylist = true
                for (messageSnapshot in snapshot.children) {
                    val playlist = messageSnapshot.getValue<ReadPlaylist>(ReadPlaylist::class.java)
                    if (playlist != null) {
                        introPlaylist.add(playlist)
                    }
                }
                cambioPlaylist = false
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatosMusic() {
        introMusic.clear()
        referenceMusic.get()
        referenceMusic.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introMusic.clear()
                for (messageSnapshot in snapshot.children) {
                    val music = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)
                    if (music != null) {
                        introMusic.add(music)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatosAlbum() {
        introAlbum.clear()
        referenceAlbum.get()
        referenceAlbum.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAlbum.clear()
                for (messageSnapshot in snapshot.children) {
                    val album = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)
                    if (album != null) {
                        introAlbum.add(album)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatosAutor() {
        introAutor.clear()
        referenceAutor.get()
        referenceAutor.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAutor.clear()
                for (messageSnapshot in snapshot.children) {
                    val music = messageSnapshot.getValue<ReadAutorId>(ReadAutorId::class.java)
                    if (music != null) {
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
     * Room playlist
     *
     */
    fun roomPlaylist() {
        for (x in allMusic) {
            var metadata = MediaMetadata.Builder().setTitle(x.nombre).setAlbumTitle(x.album).setArtist(x.autor).build()
            player.addMediaItem(
                MediaItem.Builder().setUri(Uri.parse(x!!.musica)).setMediaMetadata(metadata).build()
            )
        }
    }

    /**
     * Recoger playlist
     *
     * @param musica
     */
    suspend fun recogerPlaylist(musica: ReadMusicaAlbumAutor){
            var storageRef = storageFire.getReferenceFromUrl(musica!!.ruta + ".mp3")
            storageRef.downloadUrl.addOnSuccessListener() {
                var url = it.toString()
                var metadata = MediaMetadata.Builder().setTitle(musica.nombre).setAlbumTitle(musica.album).setArtist(musica.autor).build()
                player.addMediaItem(
                    MediaItem.Builder().setUri(Uri.parse(url)).setMediaMetadata(metadata).build()
                )
                musicFiltrada.add(musica!!)
            }.await()

    }

    /**
     * Filtrar datos
     *
     */
    suspend fun filtrarDatos() {
        var playlistAgrupada = introPlaylist.groupBy { it.user_id }

        player.clearMediaItems()
        roomPlaylist()
        musicTemp.clear()
        musicFiltrada.clear()
            if (playlistAgrupada[AppUse.user_id] != null) {
                introPlaylist = playlistAgrupada[AppUse.user_id] as ArrayList
                recyclerVacio = false
                introPlaylist.sortByDescending { it.id }
                //if(musicTemp.isNotEmpty()) {
                //    musicFiltrada.addAll(musicTemp.toMutableList())
                //}
            } else {
                recyclerVacio = true
            }

    }

    private fun rellenarDatos() {
        if(introMusic.isNotEmpty() && introAlbum.isNotEmpty() && introAutor.isNotEmpty() && introPlaylist.isNotEmpty() && !cambioPlaylist) {
            CoroutineScope(Dispatchers.Main).launch {
                introTotal.clear()
                filtrarDatos()
                if (!recyclerVacio) {
                    for (x in introPlaylist) {
                        var music = introMusic.find { it.id == x.music_id }
                        var alb: ReadAlbum? = introAlbum.find { it.id == music!!.album_id }
                        var aut: ReadAutorId? = introAutor.find { it.id == music!!.autor_id }
                        var temp: ReadMusicaAlbumAutor
                        if (alb != null && aut != null) {
                            temp = ReadMusicaAlbumAutor(
                                music!!.id,
                                music.nombre,
                                music.album_id,
                                alb.titulo,
                                music.autor_id,
                                aut.nombre,
                                music.ruta,
                                music.portada,
                                music.descripcion,
                                music.genero_id,
                                music.numCancion
                            )
                        } else {
                            temp = ReadMusicaAlbumAutor(
                                x.id,
                                "default",
                                music!!.album_id,
                                "default",
                                music.autor_id,
                                "default",
                                music.ruta,
                                music.portada,
                                music.descripcion,
                                music.genero_id,
                                music.numCancion
                            )
                        }
                        if(musicFiltrada.find { it.id == music.id } == null) {
                            recogerPlaylist(temp)
                        }
                        introTotal.add(temp)
                    }
                    if (idSong != 0) {
                        var tempMusic = introTotal.find { it.id == idSong }
                        if (tempMusic != null) {
                            actualizarReproductor(tempMusic)
                        }
                    }
                } else if (allMusic.isEmpty()) {
                    Toast.makeText(this@PlaylistActivity, "No hay ninguna cancion en la playlist", Toast.LENGTH_LONG)
                        .show()
                }
                binding.progressBar.visibility = View.GONE
                setRecycler(introTotal as ArrayList<ReadMusicaAlbumAutor>)
            }
        }
    }

    private fun actualizarReproductor(x: ReadMusicaAlbumAutor?) {
        if (findViewById<TextView>(R.id.tv_player_nombre).text != "") {
            findViewById<TextView>(R.id.tv_player_nombre).text = x!!.nombre + " - " + x.album + " - " + x.autor
        }
    }

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>) {
        val linearLayoutManager = LinearLayoutManager(this)
        var musica = MusicaAdapter(lista, {
            nombre = it.nombre
            autor = it.autor
            album = it.album
            cancion = it.ruta
            idSong = it.id
            AppUse.recyclerPosition = allMusic.size + AppUse.recyclerPosition
            reproducir()
        }, {
            MaterialAlertDialogBuilder(this)
                .setTitle("Borrar de la playlist")
                .setMessage("¿Quieres borrar la cancion " + it.nombre + " de tu playlist?")
                .setNeutralButton("Cancelar") { dialog, which ->
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        this,
                        "No se ha borrado la canción de tu playlist",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    player.clearMediaItems()
                    cambioPlaylist = true
                    borrarPlaylist(it.id)
                    Toast.makeText(
                        this,
                        "Se ha borrado la canción ${it.nombre} de tu playlist",
                        Toast.LENGTH_LONG
                    ).show()

                }
                .show()

        })

        var musicaRoom = MusicaRoomAdapter(allMusic as ArrayList<Musica>, {
            album = ""
            nombre = it.nombre
            autor = it.autor
            cancion = it.musica
            reproducir()
        }, {
            MaterialAlertDialogBuilder(this)
                .setTitle("Borrar de la playlist")
                .setMessage("¿Quieres borrar la cancion " + it.nombre + " de tu playlist?")
                .setNeutralButton("Cancelar") { dialog, which ->
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        this,
                        "No se ha borrado la canción de tu playlist",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    borrarRoom(Musica(it.uid, it.nombre, it.autor, it.album, it.musica))
                    Toast.makeText(this,"Se ha borrado la canción ${it.nombre} de tu playlist", Toast.LENGTH_LONG).show()
                    rellenarDatos()
                }
                .show()

        })
        var total = ConcatAdapter(musicaRoom, musica)
        binding.recyclerview.adapter = total
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.scrollToPosition(0)


    }

    override fun onDestroy() {
        super.onDestroy()
        introMusic.clear()
    }

    private fun borrarPlaylist(objectId: Int) {
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("user_id").equalTo(AppUse.user_id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    if (messageSnapshot.child("music_id").value.toString() == objectId.toString()) {
                        messageSnapshot.ref.removeValue()
                        return
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun borrarRoom(cancion: Musica){
        database.MusicaDao().deleteMusic(Musica(cancion.uid, cancion.nombre, cancion.autor, cancion.album, cancion.musica))
        actualizarRoom.value = database.MusicaDao().getAllMusic()
    }

    private fun initDb() {
        db =
            FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceMusic = db.getReference("music")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
        referencePlaylist = db.getReference("playlists")
        referencePerfil = db.getReference("perfil")
    }
}