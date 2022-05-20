package com.example.proyectointegradodef.musica.autor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentAutorBinding
import com.example.proyectointegradodef.models.ReadAlbumAutor
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.models.ReadAutorId
import com.example.proyectointegradodef.musica.album.AlbumAdapter
import com.google.firebase.database.*

class AutorFragment : Fragment() {
    lateinit var binding: FragmentAutorBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    var autor: MutableList<ReadAutor> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosAutor()
    }

    private fun recogerDatosAutor(){
        autor.clear()
        reference.get()
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                autor.clear()
                for(messageSnapshot in snapshot.children){
                    val tema = messageSnapshot.getValue<ReadAutor>(ReadAutor::class.java)
                    if(tema != null){
                        autor.add(tema)
                    }
                }
                binding.loadingPanel.visibility = View.GONE
                setRecycler(autor as ArrayList<ReadAutor>)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("autors")
    }

    private fun setRecycler(lista: ArrayList<ReadAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewAutor.adapter = AutorAdapter(lista){
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putString("nombre", it.nombre)
            var intent = Intent(context, AutorActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.recyclerViewAutor.scrollToPosition(0)
        binding.recyclerViewAutor.layoutManager = linearLayoutManager

    }

    companion object {
        @JvmStatic
        fun newInstance() : AutorFragment{
            return AutorFragment()
        }
    }
}