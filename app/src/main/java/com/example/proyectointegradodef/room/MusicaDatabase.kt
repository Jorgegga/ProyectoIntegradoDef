package com.example.proyectointegradodef.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectointegradodef.room.Musica
import com.example.proyectointegradodef.room.MusicaDao

@Database(entities = [Musica::class], version = 2)
abstract class MusicaDatabase : RoomDatabase() {
    abstract fun MusicaDao(): MusicaDao
}