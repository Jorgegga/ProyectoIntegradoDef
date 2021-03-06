package com.example.proyectointegradodef.musica.crud.genero

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
import com.example.proyectointegradodef.databinding.FragmentAdminGeneroBinding
import com.example.proyectointegradodef.models.ReadGenero
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * Admin genero fragment
 *
 * @constructor Create empty Admin genero fragment
 */
class AdminGeneroFragment : Fragment() {

    lateinit var binding : FragmentAdminGeneroBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceGenero: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
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
        listeners()
        recogerDatosGenero()
        storage = Firebase.storage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000 -> {
                if(resultCode == Activity.RESULT_OK){
                    setRecycler(introGenero as ArrayList<ReadGenero>)
                }
            }
        }

    }

    private fun listeners(){
        binding.btnAnnadirGenero.setOnClickListener {
            var i = Intent(requireContext(), CrearGeneroActivity::class.java)
            startActivity(i)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
    }

    private fun recogerDatosGenero(){
        introGenero.clear()
        referenceGenero.get()
        referenceGenero.addValueEventListener(object: ValueEventListener{
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
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            bundle.putString("nombre", it.nombre)
            bundle.putString("portada", it.portada)
            var intent = Intent(context, UpdateGeneroActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, 1000)
            requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        },{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.borrarGenero)
                .setMessage(resources.getString(R.string.borrarGeneroPregunta, it.nombre))
                .setNeutralButton(R.string.cancelar) { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton(R.string.rechazar) { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        R.string.borrarGeneroRefuse,
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton(R.string.aceptar) { dialog, which ->
                    borrarGenero(it.id, it.portada)
                    ponerDefault(it.id)
                }
                .show()
        })
        binding.loadingPanel.visibility = View.GONE
        binding.recyclerViewCrudGenero.scrollToPosition(0)
        binding.recyclerViewCrudGenero.layoutManager = linearLayoutManager
    }

    private fun borrarGenero(id: Int, foto: String){
        referenceGenero.get()
        var query = referenceGenero.orderByChild("id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    if (messageSnapshot.child("id").value.toString() == id.toString()) {
                        val storageRef = storage.reference
                        if(foto != "gs://proyectointegradodam-eef79.appspot.com/proyecto/genero/default") {
                            val imageRef =
                                storageRef.child("proyecto/genero/${messageSnapshot.key}.png")
                            imageRef.delete()
                        }
                        messageSnapshot.ref.removeValue()
                        Toast.makeText(requireContext(), R.string.generoBorrado, Toast.LENGTH_LONG).show()
                        setRecycler(introGenero as ArrayList<ReadGenero>)
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
        values["genero_id"] = 1
        var query = referenceMusic.orderByChild("genero_id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children) {
                    messageSnapshot.ref.updateChildren(values)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        var query2 = referenceAlbum.orderByChild("genero_id").equalTo(id.toDouble())
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
        referenceGenero = db.getReference("generos")
        referenceAlbum = db.getReference("albums")
        referenceMusic = db.getReference("music")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AdminGeneroFragment{
            return AdminGeneroFragment()
        }
    }
}