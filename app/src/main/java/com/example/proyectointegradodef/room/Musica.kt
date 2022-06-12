package com.example.proyectointegradodef.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Musica(
    @PrimaryKey(autoGenerate = true)
    val uid : Int? = null,
    val nombre: String,
    val autor: String,
    var album: String,
    val musica: String
)
