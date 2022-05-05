package com.example.proyectointegradodef.musica.album

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.AlbumLayoutBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.ReadAlbum
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AlbumViewHolder(v: View): RecyclerView.ViewHolder(v)  {
    private val binding = AlbumLayoutBinding.bind(v)
    lateinit var db: FirebaseDatabase
    lateinit var reference: DatabaseReference

    fun render(album: ReadAlbum){
        binding.tvRecyclerAlbum.text = album.titulo

        /*var referencia2 = ""
        reference.child().child("ruta").get().addOnSuccessListener {
            if (it.value == null) {
                binding.ivCamara.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.keystoneback
                    )
                )
            } else {
                referencia2 = it.value as String
                val gsReference2 = storageFire.getReferenceFromUrl(referencia2 + ".png")
                val option = RequestOptions().error(R.drawable.keystoneback)
                GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(binding.ivCamara)
            }
        }*/
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("albums")
    }
}