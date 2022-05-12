package com.example.proyectointegradodef.musica.music

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.proyectointegradodef.databinding.FragmentReadBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDatabase
import com.example.proyectointegradodef.room.MusicaRoomAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class ReadFragment : Fragment() {
    lateinit var binding : FragmentReadBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceMusic: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceAutor: DatabaseReference
    lateinit var database : MusicaDatabase
    lateinit var allMusic : List<Musica>
    var storageFire = FirebaseStorage.getInstance()
    var introMusic: MutableList<ReadMusica> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introTotal: MutableList<ReadMusicaAlbumAutor> = ArrayList()
    var reproducir = false
    var mediaPlayer = MediaPlayer()

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
        rellenarDatosAlbum()
        rellenarDatosAutor()
        rellenarDatosMusic()
        reproductor()
        setListener()

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.reset()
    }

    fun reproductor(){
        AppUse.reproduciendo.observe(requireActivity(), Observer {
            if(AppUse.reproduciendo.value == true) {
                binding.btnReproducir.isEnabled = true
                mediaPlayer.stop()
                mediaPlayer.reset()
                reproducir = false
                reproducir()
            }
        })

        AppUse.reproduciendoLocal.observe(requireActivity(), Observer {
            if(AppUse.reproduciendoLocal.value == true){
                binding.btnReproducir.isEnabled = true
                mediaPlayer.stop()
                mediaPlayer.reset()
                reproducir = false
                reproducirRoom()
            }
        })
    }

    fun setListener(){
        binding.btnReproducir.setOnClickListener {
            reproducir()
        }
    }

    fun reproducir(){
        if (!reproducir) {
            reproducir = true
            binding.btnReproducir.setImageResource(android.R.drawable.ic_media_pause)
            try {
                if(mediaPlayer.currentPosition > 1) {
                        mediaPlayer.start()
                    }else{
                        var audioUrl = AppUse.cancion
                        var storageRef = storageFire.getReferenceFromUrl("$audioUrl.mp3")
                        storageRef.downloadUrl.addOnSuccessListener() {
                            var url = it.toString()
                            mediaPlayer.reset()
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                            try {
                                mediaPlayer.setDataSource(url)
                                mediaPlayer.prepareAsync()
                            }catch (e: IOException){
                                Toast.makeText(context, "No se ha encontrado el audio", Toast.LENGTH_SHORT).show()
                                e.printStackTrace()
                            }
                            mediaPlayer.setOnPreparedListener(OnPreparedListener {
                                mediaPlayer.start()
                            })

                            binding.tvAutorReproductor.text = AppUse.autor
                            binding.tvNombreReproductor.text = AppUse.nombre
                        }
                    }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Escuchando audio...", "Escuchando audio...")
        } else {
            mediaPlayer.pause()
            binding.btnReproducir.setImageResource(android.R.drawable.ic_media_play)
            reproducir = false

        }
    }

    fun reproducirRoom(){
        if (!reproducir) {
            reproducir = true
            binding.btnReproducir.setImageResource(android.R.drawable.ic_media_pause)
            try {
                if(mediaPlayer.currentPosition > 1) {
                    mediaPlayer.start()
                }else{
                    var uri = Uri.parse(AppUse.cancion)
                        mediaPlayer.reset()
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                        mediaPlayer.setDataSource(requireContext(), uri)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        binding.tvAutorReproductor.text = AppUse.autor
                        binding.tvNombreReproductor.text = AppUse.nombre

                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("Escuchando audio...", "Escuchando audio...")
        } else {
            mediaPlayer.pause()
            binding.btnReproducir.setImageResource(android.R.drawable.ic_media_play)
            reproducir = false

        }
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

    private fun rellenarDatos(){
        introTotal.clear()
        for (x in introMusic){
            var alb : ReadAlbum? = introAlbum.find{it.id == x.album_id}
            var aut : ReadAutorId? = introAutor.find{it.id == x.autor_id}
            var temp : ReadMusicaAlbumAutor
            if(alb != null && aut != null){
                temp = ReadMusicaAlbumAutor(x.nombre, alb.titulo, aut.nombre, x.ruta, x.portada)
            }else{
                temp = ReadMusicaAlbumAutor("default", alb!!.titulo, "default", x.ruta, x.portada)
            }
            introTotal.add(temp)
        }
        binding.loadingPanel.visibility = View.GONE
        setRecycler(introTotal as ArrayList<ReadMusicaAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        var total = ConcatAdapter(MusicaAdapter(lista), MusicaRoomAdapter(allMusic))
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = total
        binding.recyclerview.scrollToPosition(0)

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceMusic = db.getReference("music")
        referenceAlbum = db.getReference("albums")
        referenceAutor = db.getReference("autors")
    }

    companion object {
        @JvmStatic
        fun newInstance(): ReadFragment{
            return ReadFragment()
        }

    }
}