package com.example.proyectointegradodef.musica.crud.autor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.databinding.FragmentAdminAutorBinding
import com.example.proyectointegradodef.models.ReadAutor
import com.example.proyectointegradodef.musica.autor.AutorActivity
import com.google.firebase.database.*

class AdminAutorFragment : Fragment() {

    lateinit var binding: FragmentAdminAutorBinding
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
        binding = FragmentAdminAutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        recogerDatosAutor()
        listeners()

    }

    private fun listeners(){
        binding.btnAnnadirAutor.setOnClickListener {
            var i = Intent(requireContext(), CrearAutorActivity::class.java)
            startActivity(i)
        }
    }

    private fun findView(){
        //var mRevealLayout = activity.findViewById<RevealLayout>()
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
                //binding.loadingPanel.visibility = View.GONE
                setRecycler(autor as ArrayList<ReadAutor>)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setRecycler(lista: ArrayList<ReadAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudAutor.adapter = AdminAutorAdapter(lista){
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putString("nombre", it.nombre)
            var intent = Intent(context, AutorActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.recyclerViewCrudAutor.scrollToPosition(0)
        binding.recyclerViewCrudAutor.layoutManager = linearLayoutManager

    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("autors")
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : AdminAutorFragment{
            return AdminAutorFragment()
        }
    }
}