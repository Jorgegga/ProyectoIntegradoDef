package com.example.proyectointegradodef.musica.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectointegradodef.databinding.FragmentAlbumBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.example.proyectointegradodef.models.ReadAutorId
import com.google.firebase.database.*


/**
 * Album fragment
 *
 * @constructor Create empty Album fragment
 */
class AlbumFragment : Fragment() {
    lateinit var binding: FragmentAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    var album: MutableList<ReadAlbum> = ArrayList()
    var autor: MutableList<ReadAutorId> = ArrayList()
    var albumAdapter: MutableList<ReadAlbumAutor> = ArrayList()

    var idAutor = 0
    var recyclerVacio = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * On view created
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerBundle()
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

    /**
     * Filtrar datos
     *
     */
    fun filtrarDatos(){
        var albumsAgrupados = album.groupBy { it.autor_id }
        if(albumsAgrupados[idAutor] != null) {
            album = albumsAgrupados[idAutor] as ArrayList
            recyclerVacio = false
        }else {
            recyclerVacio = true
        }
    }

    private fun rellenarDatos(){
        albumAdapter.clear()
        if(idAutor != 0){
            filtrarDatos()
        }
        for(x in album){
            var aut : ReadAutorId? = autor.find{it.id == x.autor_id}
            var temp : ReadAlbumAutor
            if(aut != null) {
                temp = ReadAlbumAutor(x.id, x.autor_id, aut.nombre, x.titulo, x.portada, x.descripcion, x.genero_id)
            }else{
                temp = ReadAlbumAutor(x.id, x.autor_id,"default", x.titulo, x.portada, x.descripcion, x.genero_id)
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
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putInt("autorId", it.autor_id)
            bundle.putString("titulo", it.titulo)
            bundle.putString("autor", it.autor)
            bundle.putString("portada", it.portada)
            var intent = Intent(context, AlbumActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.recyclerview.scrollToPosition(0)

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("albums")
        reference2 = db.getReference("autors")
    }

    //Al ser reutilizado este fragment en el onclick de autor, para que enseñe cosas
    private fun recogerBundle() {
        if (arguments != null) {
            if (arguments?.getInt("id", 0) != 0) {
                idAutor = arguments?.getInt("id", 0)!!
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() : AlbumFragment{
            return AlbumFragment()
        }
    }
}