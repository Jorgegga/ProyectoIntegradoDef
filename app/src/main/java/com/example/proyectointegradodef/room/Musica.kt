package com.example.proyectointegradodef.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Musica
 *
 * @property uid
 * @property nombre
 * @property autor
 * @property album
 * @property musica
 * @constructor Create empty Musica
 */
@Entity
data class Musica(
    @PrimaryKey(autoGenerate = true)
    val uid : Int? = null,
    val nombre: String,
    val autor: String,
    var album: String,
    val musica: String
)
