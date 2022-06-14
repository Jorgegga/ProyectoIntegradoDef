package com.example.proyectointegradodef.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectointegradodef.room.Musica

/**
 * Musica dao
 *
 * @constructor Create empty Musica dao
 */
@Dao
interface MusicaDao {

    /**
     * Get all music
     *
     * @return
     */
    @Query("SELECT * FROM Musica")
    fun getAllMusic(): List<Musica>

    /**
     * Insert music
     *
     * @param music
     */
    @Insert
    fun insertMusic(music: Musica)

    /**
     * Delete music
     *
     * @param music
     */
    @Delete
    fun deleteMusic(music: Musica)
}