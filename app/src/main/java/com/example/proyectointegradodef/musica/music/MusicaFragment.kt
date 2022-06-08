package com.example.proyectointegradodef.musica.music

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentMusicaBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDatabase
import com.example.proyectointegradodef.room.MusicaRoomAdapter
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

class MusicaFragment : Fragment(), Player.Listener {
    lateinit var binding: FragmentMusicaBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceMusic: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var referencePlaylist: DatabaseReference
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
    var reproducir = false

    var nombre = ""
    var autor = ""
    var idAutor = 0
    var cancion = ""
    var idSong = 0
    var recyclerVacio = false
    var crearId = 0
    var existeCancion = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMusicaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        binding.btnReproducir.isEnabled = false
        binding.tvNombreReproductor.isSelected = true
        binding.tvAutorReproductor.isSelected = true

        renderersFactory = DefaultRenderersFactory(requireContext())
        trackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelectSelector = DefaultTrackSelector(requireContext(), trackSelectionFactory)
        loadControl = DefaultLoadControl()
        player = ExoPlayer.Builder(requireContext()).build()
        player.addListener(this)
        dataSourceFactory = DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
        extractorsFactory = DefaultExtractorsFactory()
        recogerBundle()
        rellenarDatosAlbum()
        rellenarDatosAutor()
        rellenarDatosMusic()

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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)

    }

    fun reproducir() {
        try {
            player.seekTo(AppUse.recyclerPosition, 0)
            player.prepare()
            player.playWhenReady = true
            binding.videoView.player = player
            binding.videoView.useArtwork = false
            binding.tvAutorReproductor.text = autor
            binding.tvNombreReproductor.text = nombre
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("Escuchando audio...", "Escuchando audio...")
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

    fun filtrarDatos() {
        var musicaAgrupada = introMusic.groupBy { it.autor_id }
        if (musicaAgrupada[idAutor] != null) {
            introMusic = musicaAgrupada[idAutor] as ArrayList
            recyclerVacio = false
        } else {
            recyclerVacio = true
        }
    }

    private fun rellenarDatos() {
        introTotal.clear()
        if (idAutor != 0) {
            filtrarDatos()
        }
        player.clearMediaItems()
        for (x in introMusic) {
            var storageRef = storageFire.getReferenceFromUrl(x!!.ruta + ".mp3")
            storageRef.downloadUrl.addOnSuccessListener() {
                var url = it.toString()
                player.addMediaItem(
                    MediaItem.Builder().setUri(Uri.parse(url)).build()
                )
            }
            var alb: ReadAlbum? = introAlbum.find { it.id == x.album_id }
            var aut: ReadAutorId? = introAutor.find { it.id == x.autor_id }
            var temp: ReadMusicaAlbumAutor
            if (alb != null && aut != null) {
                temp = ReadMusicaAlbumAutor(
                    x.id,
                    x.nombre,
                    x.album_id,
                    alb.titulo,
                    x.autor_id,
                    aut.nombre,
                    x.ruta,
                    x.portada,
                    x.descripcion
                )
            } else {
                temp = ReadMusicaAlbumAutor(
                    x.id,
                    "default",
                    x.album_id,
                    alb!!.titulo,
                    x.autor_id,
                    "default",
                    x.ruta,
                    x.portada,
                    x.descripcion
                )
            }
            introTotal.add(temp)
        }
        if (idSong != 0) {
            var tempMusic = introMusic.find { it.id == idSong }
            var tempAutor = introAutor.find { it.id == tempMusic!!.autor_id }
            if (tempMusic != null) {
                actualizarReproductorCancion(tempMusic)
            }
            if (tempAutor != null) {
                actualizarReproductorAutor(tempAutor)
            }
        }
        binding.loadingPanel.visibility = View.GONE
        setRecycler(introTotal as ArrayList<ReadMusicaAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>) {
        val linearLayoutManager = LinearLayoutManager(context)
        var musica = MusicaAdapter(lista, {
            nombre = it.nombre
            autor = it.autor
            cancion = it.ruta
            idSong = it.id
            reproducir()
        }, {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Añadir a la playlist")
                .setMessage("¿Quieres añadir la cancion " + it.nombre + " a tu playlist?")
                .setNeutralButton("Cancelar") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "No se ha añadido la canción a tu playlist",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    comprobarExistePlaylist(it.id)

                }
                .show()
        })
        binding.recyclerview.adapter = musica
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.scrollToPosition(0)

    }

    private fun actualizarReproductorCancion(x: ReadMusica) {
        if (binding.tvNombreReproductor.text != "") {
            binding.tvNombreReproductor.text = x.nombre
        }
    }

    private fun actualizarReproductorAutor(x: ReadAutorId) {
        if (binding.tvAutorReproductor.text != "") {
            binding.tvAutorReproductor.text = x.nombre
        }

    }

    private fun buscarId(music: Int) {
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("id").limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        crearId = messageSnapshot.getValue<ReadPlaylist>(ReadPlaylist::class.java)!!.id + 1
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

    private fun comprobarExistePlaylist(objectId: Int) {
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("user_id").equalTo(AppUse.user_id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    if (messageSnapshot.child("music_id").value.toString() == objectId.toString()) {
                        Toast.makeText(
                            requireContext(),
                            "Esa cancion ya esta en tu playlist",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }
                crearId = 0
                buscarId(objectId)
                Toast.makeText(
                    requireContext(),
                    "Se ha añadido la cancion a tu playlist",
                    Toast.LENGTH_LONG
                ).show()
                rellenarDatosMusic()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initDb() {
        db =
            FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceMusic = db.getReference("music")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
        referencePlaylist = db.getReference("playlists")
    }

    private fun recogerBundle() {
        if (arguments != null) {
            if (arguments?.getInt("id", 0) != 0) {
                idAutor = arguments?.getInt("id", 0)!!
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(): MusicaFragment {
            return MusicaFragment()
        }

    }
}