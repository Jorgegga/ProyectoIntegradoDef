package com.example.proyectointegradodef.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDao

/**
 * Musica database
 *
 * @constructor Create empty Musica database
 */
@Database(entities = [Musica::class], version = 1)
abstract class MusicaDatabase : RoomDatabase() {
    /**
     * Musica dao
     *
     * @return
     */
    abstract fun MusicaDao(): MusicaDao
}