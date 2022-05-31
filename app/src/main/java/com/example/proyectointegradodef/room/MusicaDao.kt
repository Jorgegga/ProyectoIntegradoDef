package com.example.proyectointegradodef.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectointegradodef.room.Musica

@Dao
interface MusicaDao {

    @Query("SELECT * FROM Musica")
    fun getAllMusic(): List<Musica>

    @Insert
    fun insertMusic(music: Musica)

    @Delete
    fun deleteMusic(music: Musica)
}