package com.example.proyectointegradodef.musica.crud.autor

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
import com.example.proyectointegradodef.databinding.FragmentAdminAutorBinding
import com.example.proyectointegradodef.models.ReadAutor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * Admin autor fragment
 *
 * @constructor Create empty Admin autor fragment
 */
class AdminAutorFragment : Fragment() {

    lateinit var binding: FragmentAdminAutorBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var storage: FirebaseStorage
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
        storage = Firebase.storage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000 -> {
                if(resultCode == Activity.RESULT_OK){
                    setRecycler(autor as ArrayList<ReadAutor>)
                }
            }
        }

    }

    private fun listeners(){
        binding.btnAnnadirAutor.setOnClickListener {
            var i = Intent(requireContext(), CrearAutorActivity::class.java)
            startActivity(i)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
    }

    private fun recogerDatosAutor(){
        autor.clear()
        referenceAutor.get()
        referenceAutor.addValueEventListener(object: ValueEventListener {
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
        binding.recyclerViewCrudAutor.adapter = AdminAutorAdapter(lista,{
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putString("nombre", it.nombre)
            bundle.putString("foto", it.foto)
            bundle.putString("descripcion", it.descripcion)
            var intent = Intent(context, UpdateAutorActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, 1000)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        },{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Borrar autor")
                .setMessage("¿Quieres borrar el autor " + it.nombre + " de la base de datos?")
                .setNeutralButton("Cancelar") { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton("Rechazar") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "No se ha borrado el autor",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Aceptar") { dialog, which ->
                    borrarAutor(it.id, it.foto)
                    ponerDefault(it.id)
                }
                .show()
        })
        binding.loadingPanel.visibility = View.GONE
        binding.recyclerViewCrudAutor.scrollToPosition(0)
        binding.recyclerViewCrudAutor.layoutManager = linearLayoutManager

    }

    private fun borrarAutor(id: Int, foto: String){
        referenceAutor.get()
        var query = referenceAutor.orderByChild("id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    for (messageSnapshot in snapshot.children) {
                        if (messageSnapshot.child("id").value.toString() == id.toString()) {
                            val storageRef = storage.reference
                            if(foto != "gs://proyectointegradodam-eef79.appspot.com/proyecto/autor/default") {
                                val imageRef =
                                    storageRef.child("proyecto/autor/${messageSnapshot.key}.png")
                                imageRef.delete()
                            }
                            messageSnapshot.ref.removeValue()
                            Toast.makeText(requireContext(), "Se ha borrado el autor correctamente", Toast.LENGTH_LONG).show()
                            setRecycler(autor as ArrayList<ReadAutor>)
                            return
                        }
                    }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun ponerDefault(id: Int){
        referenceMusic.get()
        referenceAlbum.get()
        val values = HashMap<String, Any>()
        values["autor_id"] = 1
        var query = referenceMusic.orderByChild("autor_id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children) {
                    messageSnapshot.ref.updateChildren(values)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        var query2 = referenceAlbum.orderByChild("autor_id").equalTo(id.toDouble())
        query2.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children) {
                    messageSnapshot.ref.updateChildren(values)
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
        referenceMusic = db.getReference("music")
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : AdminAutorFragment{
            return AdminAutorFragment()
        }
    }
}