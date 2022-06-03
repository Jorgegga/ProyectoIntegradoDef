package com.example.proyectointegradodef.musica.crud.genero

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.databinding.FragmentAdminGeneroBinding
import com.example.proyectointegradodef.models.ReadGenero
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class AdminGeneroFragment : Fragment() {

    lateinit var binding : FragmentAdminGeneroBinding
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var storage: FirebaseStorage

    var introGenero: MutableList<ReadGenero> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminGeneroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosGenero()
    }

    private fun recogerDatosGenero(){
        introGenero.clear()
        reference.get()
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                introGenero.clear()
                for(messageSnapshot in snapshot.children){
                    val genero = messageSnapshot.getValue<ReadGenero>(ReadGenero::class.java)
                    if(genero != null){
                        introGenero.add(genero)
                    }
                }
                setRecycler(introGenero as ArrayList<ReadGenero>)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setRecycler(lista: ArrayList<ReadGenero>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudGenero.adapter = AdminGeneroAdapter(lista, {

        },{

        })
        binding.recyclerViewCrudGenero.scrollToPosition(0)
        binding.recyclerViewCrudGenero.layoutManager = linearLayoutManager
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("generos")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AdminGeneroFragment{
            return AdminGeneroFragment()
        }
    }
}