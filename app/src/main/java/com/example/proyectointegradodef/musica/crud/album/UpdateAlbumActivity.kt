package com.example.proyectointegradodef.musica.crud.album

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectointegradodef.databinding.ActivityUpdateAlbumBinding

class UpdateAlbumActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}