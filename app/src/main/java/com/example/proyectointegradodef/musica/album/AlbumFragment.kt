package com.example.proyectointegradodef.musica.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectointegradodef.databinding.FragmentAlbumBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.example.proyectointegradodef.models.ReadAutorId
import com.google.firebase.database.*


class AlbumFragment : Fragment() {
    lateinit var binding: FragmentAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    var album: MutableList<ReadAlbum> = ArrayList()
    var autor: MutableList<ReadAutorId> = ArrayList()
    var albumAdapter: MutableList<ReadAlbumAutor> = ArrayList()

    var idClick = 0
    var autorIdClick = 0
    var tituloClick = ""
    var autorClick = ""
    var portadaClick = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosAutor()
        recogerDatosAlbum()

    }

    private fun recogerDatosAlbum(){
        album.clear()
        reference.get()
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                album.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAlbum>(ReadAlbum::class.java)
                    if(tema != null){
                        album.add(tema)
                    }
                }
                rellenarDatos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun recogerDatosAutor(){
        autor.clear()
        reference2.get()
        reference2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                autor.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAutorId>(ReadAutorId::class.java)
                    if(tema != null){
                        autor.add(tema)
                    }
                }
                rellenarDatos()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun rellenarDatos(){
        albumAdapter.clear()
        for(x in album){
            var aut : ReadAutorId? = autor.find{it.id == x.idautor}
            var temp : ReadAlbumAutor
            if(aut != null) {
                temp = ReadAlbumAutor(x.id, x.idautor, aut.nombre, x.titulo, x.portada)
            }else{
                temp = ReadAlbumAutor(x.id, x.idautor,"default", x.titulo, x.portada)
            }
            if (temp != null){
                albumAdapter.add(temp)
            }
        }
        binding.loadingPanel.visibility = View.GONE
        setRecycler(albumAdapter as ArrayList<ReadAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadAlbumAutor>){
        binding.recyclerview.adapter = AlbumAdapter(lista){
            idClick = it.id
            autorIdClick = it.autorId
            tituloClick = it.titulo
            autorClick = it.autor
            portadaClick = it.portada
            Log.d("aaaaaaaaaaaaaaaaaaaaaaaaa", tituloClick)
        }
        binding.recyclerview.scrollToPosition(0)

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("albums")
        reference2 = db.getReference("autors")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AlbumFragment{
            return AlbumFragment()
        }
    }
}