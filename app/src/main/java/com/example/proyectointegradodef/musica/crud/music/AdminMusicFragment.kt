package com.example.proyectointegradodef.musica.crud.music

import android.app.Activity
import android.content.Intent
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * Admin music fragment
 *
 * @constructor Create empty Admin music fragment
 */
class AdminMusicFragment : Fragment() {

    lateinit var binding: FragmentAdminMusicBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var referencePlaylist: DatabaseReference
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
        if(introMusic.isNotEmpty() && introAlbum.isNotEmpty() && introAutor.isNotEmpty()) {
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
                        x.descripcion,
                        x.genero_id,
                        x.numCancion
                    )
                } else {
                    temp = ReadMusicaAlbumAutor(
                        x.id,
                        "default",
                        x.album_id,
                        "default",
                        x.autor_id,
                        "default",
                        x.ruta,
                        x.portada,
                        x.descripcion,
                        x.genero_id,
                        x.numCancion
                    )
                }
                introMusicAdapter.add(temp)
            }
            setRecycler(introMusicAdapter as ArrayList<ReadMusicaAlbumAutor>)
        }
    }

    private fun setRecycler(lista: ArrayList<ReadMusicaAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudMusic.adapter = AdminMusicAdapter(lista,{
            recogerNombreGenero(it)
        },{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Borrar cancion")
                .setMessage("¿Quieres borrar la cancion " + it.nombre + " de la base de datos?")
                .setNeutralButton("Cancelar") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "No se ha borrado la cancion",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    borrarMusic(it.id, it.portada, it.ruta)
                    borrarPlaylist(it.id)
                }
                .show()
        })
        binding.loadingPanel.visibility = View.GONE
        binding.recyclerViewCrudMusic.scrollToPosition(0)
        binding.recyclerViewCrudMusic.layoutManager = linearLayoutManager
    }

    private fun recogerNombreGenero(music: ReadMusicaAlbumAutor){
        var genero = ""
        referenceGenero.get()
        var query = referenceGenero.orderByChild("id").equalTo(music.genero_id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children){
                    genero = messageSnapshot.child("nombre").value.toString()
                }
                updateActivity(music, genero)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateActivity(music: ReadMusicaAlbumAutor, genero: String){
        val bundle = Bundle()
        bundle.putInt("id", music.id)
        bundle.putString("genero", genero)
        bundle.putString("nombre", music.nombre)
        bundle.putInt("album_id", music.album_id)
        bundle.putString("album", music.album)
        bundle.putInt("autor_id", music.autor_id)
        bundle.putString("autor", music.autor)
        bundle.putString("ruta", music.ruta)
        bundle.putString("portada", music.portada)
        bundle.putString("descripcion", music.descripcion)
        bundle.putInt("genero_id", music.genero_id)
        bundle.putInt("numCancion", music.numCancion)
        var intent = Intent(context, UpdateMusicActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 1000)
        requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }

    private fun borrarMusic(id: Int, foto: String, ruta: String){
        referenceMusic.get()
        var query = referenceMusic.orderByChild("id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    if (messageSnapshot.child("id").value.toString() == id.toString()) {
                        val storageRef = storage.reference
                        if(!foto.contains("gs://proyectointegradodam-eef79.appspot.com/proyecto/album") && foto != "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/portada/default") {
                            val imageRef =
                                storageRef.child("proyecto/musica/portada/${messageSnapshot.key}.png")
                            imageRef.delete()
                        }

                        if(ruta != "gs://proyectointegradodam-eef79.appspot.com/proyecto/musica/default"){
                            val rutaRef = storageRef.child("proyecto/musica/${messageSnapshot.key}.mp3")
                            rutaRef.delete()
                        }
                        messageSnapshot.ref.removeValue()
                        Toast.makeText(requireContext(), "Se ha borrado la cancion correctamente", Toast.LENGTH_LONG).show()
                        setRecycler(introMusicAdapter as ArrayList<ReadMusicaAlbumAutor>)
                        return
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000 -> {
                if(resultCode == Activity.RESULT_OK){
                    setRecycler(introMusicAdapter as ArrayList<ReadMusicaAlbumAutor>)
                }
            }
        }

    }


    private fun borrarPlaylist(id: Int){
        referencePlaylist.get()
        var query = referencePlaylist.orderByChild("music_id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children) {
                    messageSnapshot.ref.removeValue()
                }
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
        referencePlaylist = db.getReference("playlists")

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(): AdminMusicFragment{
            return AdminMusicFragment()
        }
    }
}