package com.example.proyectointegradodef.musica.crud.album

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.FragmentAdminAlbumBinding
import com.example.proyectointegradodef.models.*
import com.example.proyectointegradodef.musica.album.AlbumActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * Admin album fragment
 *
 * @constructor Create empty Admin album fragment
 */
class AdminAlbumFragment : Fragment() {

    lateinit var binding: FragmentAdminAlbumBinding
    lateinit var db: FirebaseDatabase
    lateinit var referenceAutor: DatabaseReference
    lateinit var referenceAlbum: DatabaseReference
    lateinit var referenceGenero: DatabaseReference
    lateinit var referenceMusic: DatabaseReference
    lateinit var storage: FirebaseStorage
    var introAutor: MutableList<ReadAutorId> = ArrayList()
    var introAlbum: MutableList<ReadAlbum> = ArrayList()
    var introAlbumAdapter: MutableList<ReadAlbumAutor> = ArrayList()


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000 -> {
                if(resultCode == Activity.RESULT_OK){
                    setRecycler(introAlbumAdapter as ArrayList<ReadAlbumAutor>)
                }
            }
        }

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

    private fun rellenarDatos(){
        if(introAlbum.isNotEmpty() && introAutor.isNotEmpty()) {
            introAlbumAdapter.clear()
            for (x in introAlbum) {
                var aut: ReadAutorId? = introAutor.find { it.id == x.autor_id }
                var temp: ReadAlbumAutor
                if (aut != null) {
                    temp = ReadAlbumAutor(
                        x.id,
                        x.autor_id,
                        aut.nombre,
                        x.titulo,
                        x.portada,
                        x.descripcion,
                        x.genero_id
                    )
                } else {
                    temp = ReadAlbumAutor(
                        x.id,
                        x.autor_id,
                        "default",
                        x.titulo,
                        x.portada,
                        x.descripcion,
                        x.genero_id
                    )
                }
                if (temp != null) {
                    introAlbumAdapter.add(temp)
                }
            }
        }
        setRecycler(introAlbumAdapter as ArrayList<ReadAlbumAutor>)
    }

    private fun setRecycler(lista: ArrayList<ReadAlbumAutor>){
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewCrudAlbum.adapter = AdminAlbumAdapter(lista,{
            recogerNombreGenero(it)
        },{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.borrarAlbum)
                .setMessage(resources.getString(R.string.borrarAlbumPregunta, it.titulo))
                .setNeutralButton(R.string.cancelar) { dialog, which ->
                    // Respond to neutral button press
                }
                .setNegativeButton(R.string.rechazar) { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        R.string.borrarAlbumRefuse,
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton(R.string.aceptar) { dialog, which ->
                    borrarAlbum(it.id, it.portada)
                    ponerDefault(it.id)
                }
                .show()
        })
        binding.loadingPanel.visibility = View.GONE
        binding.recyclerViewCrudAlbum.scrollToPosition(0)
        binding.recyclerViewCrudAlbum.layoutManager = linearLayoutManager
    }

    private fun recogerNombreGenero(album: ReadAlbumAutor){
        var genero = ""
        referenceGenero.get()
        var query = referenceGenero.orderByChild("id").equalTo(album.genero_id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children){
                    genero = messageSnapshot.child("nombre").value.toString()
                }
                updateActivity(album, genero)
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateActivity(album: ReadAlbumAutor, genero: String){
        val bundle = Bundle()
        bundle.putString("genero", genero)
        bundle.putInt("id", album.id)
        bundle.putInt("autor_id", album.autor_id)
        bundle.putString("autor", album.autor)
        bundle.putString("titulo", album.titulo)
        bundle.putString("portada", album.portada)
        bundle.putString("descripcion", album.descripcion)
        bundle.putInt("genero_id", album.genero_id)
        var intent = Intent(context, UpdateAlbumActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 1000)
        requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }

    private fun borrarAlbum(id: Int, foto: String){
        referenceAlbum.get()
        var query = referenceAlbum.orderByChild("id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (messageSnapshot in snapshot.children) {
                    if (messageSnapshot.child("id").value.toString() == id.toString()) {
                        val storageRef = storage.reference
                        if(foto != "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default") {
                            val imageRef =
                                storageRef.child("proyecto/album/${messageSnapshot.key}.png")
                            imageRef.delete()
                        }
                        messageSnapshot.ref.removeValue()
                        Toast.makeText(requireContext(), R.string.albumBorrado, Toast.LENGTH_LONG).show()
                        setRecycler(introAlbumAdapter as ArrayList<ReadAlbumAutor>)
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
        val values = HashMap<String, Any>()
        values["album_id"] = 1
        var query = referenceMusic.orderByChild("album_id").equalTo(id.toDouble())
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(messageSnapshot in snapshot.children) {
                    if(messageSnapshot.child("ruta").value.toString().contains("gs://proyectointegradodam-eef79.appspot.com/proyecto/album") && messageSnapshot.child("ruta").value.toString() != "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"){
                        values["ruta"] = "gs://proyectointegradodam-eef79.appspot.com/proyecto/album/default"
                    }else{
                        values.remove("ruta")
                    }
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
        referenceGenero = db.getReference("generos")
        referenceMusic = db.getReference("music")
    }

    companion object {
        @JvmStatic
        fun newInstance() : AdminAlbumFragment{
            return AdminAlbumFragment()
        }
    }
}