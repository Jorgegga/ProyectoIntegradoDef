package com.example.proyectointegradodef.musica.crud.music

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectointegradodef.databinding.ActivityCrearMusicBinding

class CrearMusicActivity : AppCompatActivity() {

    lateinit var binding: ActivityCrearMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}