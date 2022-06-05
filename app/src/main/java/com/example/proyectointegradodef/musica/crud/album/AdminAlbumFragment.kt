package com.example.proyectointegradodef.musica.crud.album

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentAdminAlbumBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadAutorId
import com.example.proyectointegradodef.musica.album.AlbumActivity
import com.example.proyectointegradodef.musica.album.AlbumAdapter
import com.example.proyectointegradodef.musica.crud.autor.CrearAutorActivity
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AdminAlbumFragment : Fragment() {

    lateinit var binding: FragmentAdminAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var storage: FirebaseStorage
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introAlbumAdapter: MutableList<ReadAlbumAutor> = ArrayList()

    var idAutor = 0
    var recyclerVacio = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosAutor()
        recogerDatosAlbum()
        listeners()
        storage = Firebase.storage
    }

    private fun listeners(){
        binding.btnAnnadirAlbum.setOnClickListener {
            var i = Intent(requireContext(), CrearAlbumActivity::class.java)
            startActivity(i)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
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

    fun filtrarDatos(){
        var albumsAgrupados = introAlbum.groupBy { it.idautor }
        if(albumsAgrupados[idAutor] != null) {
            introAlbum = albumsAgrupados[idAutor] as ArrayList
            recyclerVacio = false
        }else {
            recyclerVacio = true
        }
    }

    private fun rellenarDatos(){
        introAlbumAdapter.clear()
        if(idAutor != 0){
            filtrarDatos()
        }
        for(x in introAlbum){
            var aut : ReadAutorId? = introAutor.find{it.id == x.idautor}
            var temp : ReadAlbumAutor
            if(aut != null) {
                temp = ReadAlbumAutor(x.id, x.idautor, aut.nombre, x.titulo, x.portada, x.descripcion)
            }else{
                temp = ReadAlbumAutor(x.id, x.idautor,"default", x.titulo, x.portada, x.descripcion)
            }
            if (temp != null){
                introAlbumAdapter.add(temp)
            }
        }
        //binding.loadingPanel.visibility = View.GONE
        setRecycler(introAlbumAdapter as ArrayList<ReadAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudAlbum.adapter = AdminAlbumAdapter(lista,{
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putInt("autorId", it.autorId)
            bundle.putString("titulo", it.titulo)
            bundle.putString("autor", it.autor)
            bundle.putString("portada", it.portada)
            var intent = Intent(context, AlbumActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        },{

        })
        binding.recyclerViewCrudAlbum.scrollToPosition(0)
        binding.recyclerViewCrudAlbum.layoutManager = linearLayoutManager
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        referenceAutor = db.getReference("autors")
        referenceAlbum = db.getReference("albums")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AdminAlbumFragment{
            return AdminAlbumFragment()
        }
    }
}