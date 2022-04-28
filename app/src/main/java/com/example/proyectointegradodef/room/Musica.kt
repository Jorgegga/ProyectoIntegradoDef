package com.example.proyectointegradodef.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Musica(
    @PrimaryKey(autoGenerate = true) val uid : Int = 1,
    val nombre: String,
    val autor: String,
    val musica: String
)
