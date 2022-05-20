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
import com.example.proyectointegradodef.databinding.FragmentReadBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDatabase
import com.example.proyectointegradodef.room.MusicaRoomAdapter
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class MusicaFragment : Fragment(), Player.Listener {
    lateinit var binding : FragmentReadBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceMusic: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var database : MusicaDatabase
    lateinit var allMusic : List<Musica>
    lateinit var player: ExoPlayer
    lateinit var dataSourceFactory: DefaultDataSourceFactory
    lateinit var extractorsFactory: DefaultExtractorsFactory

    lateinit var renderersFactory: DefaultRenderersFactory
    lateinit var trackSelectionFactory: AdaptiveTrackSelection.Factory
    lateinit var trackSelectSelector: DefaultTrackSelector
    lateinit var loadControl : DefaultLoadControl

    var storageFire = FirebaseStorage.getInstance()
    var introMusic: MutableList<ReadMusica> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introTotal: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var reproducir = false

    var nombre = ""
    var autor = ""
    var idAutor = 0
    var cancion = ""
    var idSong = 0
    var recyclerVacio = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(requireContext(), MusicaDatabase::class.java, "musica_database").allowMainThreadQueries().build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReadBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        allMusic = database.MusicaDao().getAllMusic()
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
        setListener()

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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)

    }

    fun setListener(){
        binding.btnReproducir.setOnClickListener {
            reproducir()
        }
    }

    fun reproducir(){
            try {
                var audioUrl = cancion
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
                    binding.tvAutorReproductor.text = autor
                    binding.tvNombreReproductor.text = nombre
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Escuchando audio...", "Escuchando audio...")
    }

    fun reproducirRoom(){
            try {
                var uri = Uri.parse(cancion)
                var mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
                player.setMediaSource(mediaSource)
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

    private fun rellenarDatosMusic(){
        introMusic.clear()
        referenceMusic.get()
        referenceMusic.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                introMusic.clear()
                for(messageSnapshot in snapshot.children){
                    val music = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)
                    if(music != null){
                        introMusic.add(music)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatosAlbum(){
        introAlbum.clear()
        referenceAlbum.get()
        referenceAlbum.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                introAlbum.clear()
                for(messageSnapshot in snapshot.children){
                    val music = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)
                    if(music != null){
                        introAlbum.add(music)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatosAutor(){
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

    fun filtrarDatos(){
        var musicaAgrupada = introMusic.groupBy { it.autor_id }
        if(musicaAgrupada[idAutor] != null) {
            introMusic = musicaAgrupada[idAutor] as ArrayList
            recyclerVacio = false
        }else {
            recyclerVacio = true
        }
    }

    private fun rellenarDatos(){
        introTotal.clear()
        if(idAutor != 0){
            filtrarDatos()
        }
        for (x in introMusic){
            var alb : ReadAlbum? = introAlbum.find{it.id == x.album_id}
            var aut : ReadAutorId? = introAutor.find{it.id == x.autor_id}
            var temp : ReadMusicaAlbumAutor
            if(alb != null && aut != null){
                temp = ReadMusicaAlbumAutor(x.id, x.nombre, x.album_id, alb.titulo, x.autor_id, aut.nombre, x.ruta, x.portada)
            }else{
                temp = ReadMusicaAlbumAutor(x.id, "default", x.album_id, alb!!.titulo, x.autor_id, "default", x.ruta, x.portada)
            }
            introTotal.add(temp)
        }
        if(idSong != 0) {
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

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        var musica = MusicaAdapter(lista) {
            nombre = it.nombre
            autor = it.autor
            cancion = it.ruta
            idSong = it.id
            reproducir()
        }

        if(idAutor != 0){
            binding.recyclerview.adapter = musica
        }else {
            var musicaRoom = MusicaRoomAdapter(allMusic as ArrayList<Musica>){
                nombre = it.nombre
                autor = it.autor
                cancion = it.musica
                reproducirRoom()
            }
            var total = ConcatAdapter(musica, musicaRoom)
            binding.recyclerview.adapter = total
        }
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.scrollToPosition(0)

    }

    private fun actualizarReproductorCancion(x: ReadMusica){
        if (binding.tvNombreReproductor.text != ""){
            binding.tvNombreReproductor.text = x.nombre
        }
    }

    private fun actualizarReproductorAutor(x: ReadAutorId){
        if (binding.tvAutorReproductor.text != ""){
            binding.tvAutorReproductor.text = x.nombre
        }

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceMusic = db.getReference("music")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
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
        fun newInstance(): MusicaFragment{
            return MusicaFragment()
        }

    }
}