package com.example.proyectointegradodef.musica.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.databinding.FragmentAlbumBinding
import com.example.proyectointegradodef.models.ReadAlbum
import com.google.firebase.database.*


class AlbumFragment : Fragment() {
    lateinit var binding: FragmentAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    var album: MutableList<ReadAlbum> = ArrayList()

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
        rellenarDatos()
    }

    private fun rellenarDatos(){
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
                setRecycler(album as ArrayList<ReadAlbum>)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setRecycler(lista: ArrayList<ReadAlbum>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = AlbumAdapter(lista)
        binding.recyclerview.scrollToPosition(lista.size-1)

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("albums")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AlbumFragment{
            return AlbumFragment()
        }
    }
}