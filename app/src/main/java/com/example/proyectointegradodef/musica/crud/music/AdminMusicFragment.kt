package com.example.proyectointegradodef.musica.crud.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentAdminMusicBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.musica.crud.album.AdminAlbumAdapter
import com.example.proyectointegradodef.musica.crud.album.CrearAlbumActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AdminMusicFragment : Fragment() {

    lateinit var binding: FragmentAdminMusicBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var storage: FirebaseStorage
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introMusic: MutableList<ReadMusica> = ArrayList()
    var introMusicAdapter: MutableList<ReadMusicaAlbumAutor> = ArrayList()

    var idMusic = 0
    var recyclerVacio = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminMusicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosAutor()
        recogerDatosAlbum()
        recogerDatosMusic()
        listeners()
        storage = Firebase.storage
    }

    private fun listeners(){
        binding.btnAnnadirMusic.setOnClickListener {
            var i = Intent(requireContext(), CrearMusicActivity::class.java)
            startActivity(i)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
    }

    private fun recogerDatosMusic() {
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

    private fun recogerDatosAlbum(){
        introAlbum.clear()
        referenceAlbum.get()
        referenceAlbum.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAlbum.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)
                    if(tema != null){
                        introAlbum.add(tema)
                    }
                }
                rellenarDatos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun recogerDatosAutor(){
        introAutor.clear()
        referenceAutor.get()
        referenceAutor.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                introAutor.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAutorId>(ReadAutorId::class.java)
                    if(tema != null){
                        introAutor.add(tema)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatos() {
        introMusicAdapter.clear()

        for (x in introMusic) {
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
            introMusicAdapter.add(temp)
        }
        //binding.loadingPanel.visibility = View.GONE
        setRecycler(introMusicAdapter as ArrayList<ReadMusicaAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudMusic.adapter = AdminMusicAdapter(lista,{
            //recogerNombreGenero(it)
        },{
            /*MaterialAlertDialogBuilder(requireContext())
                .setTitle("Borrar album")
                .setMessage("¿Quieres borrar el album " + it.titulo + " de la base de datos?")
                .setNeutralButton("Cancelar") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "No se ha borrado el album",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    borrarAlbum(it.id, it.portada)

                }
                .show()*/
        })
        binding.recyclerViewCrudMusic.scrollToPosition(0)
        binding.recyclerViewCrudMusic.layoutManager = linearLayoutManager
    }

    private fun recogerNombreGenero(album: ReadAlbumAutor){
        var genero = ""
        referenceGenero.get()
        var query = referenceGenero.orderByChild("id").equalTo(album.genero_id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children){
                    genero = messageSnapshot.child("nombre").value.toString()
                }
                //updateActivity(album, genero)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
        referenceGenero = db.getReference("generos")
        referenceMusic = db.getReference("music")
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(): AdminMusicFragment{
            return AdminMusicFragment()
        }
    }
}