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
import com.example.proyectointegradodef.models.ReadMusica
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
    lateinit var reference: DatabaseReference
    lateinit var database : MusicaDatabase
    lateinit var allMusic : List<Musica>
    var storageFire = FirebaseStorage.getInstance()
    var intro: MutableList<ReadMusica> = ArrayList()
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
        rellenarDatos()
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

    private fun rellenarDatos(){
        intro.clear()
        reference.get()
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                intro.clear()
                for(messageSnapshot in snapshot.children){
                    val music = messageSnapshot.getValue<ReadMusica>(ReadMusica::class.java)
                    if(music != null){
                        intro.add(music)
                    }
                }
                binding.loadingPanel.visibility = View.GONE
                setRecycler(intro as ArrayList<ReadMusica>)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setRecycler(lista: ArrayList<ReadMusica>){
        val linearLayoutManager = LinearLayoutManager(context)
        var total = ConcatAdapter(MusicaAdapter(lista), MusicaRoomAdapter(allMusic))
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = total
        binding.recyclerview.scrollToPosition(0)

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("music")
    }

    companion object {

        @JvmStatic
        fun newInstance(): ReadFragment{
            return ReadFragment()
        }

    }
}