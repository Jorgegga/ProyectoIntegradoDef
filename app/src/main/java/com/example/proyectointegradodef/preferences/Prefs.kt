package com.example.proyectointegradodef.preferences

import android.content.Context

class Prefs(c: Context) {
    val FILE_DATA_NAME="fdn"
    val SHARED_EMAIL = "email"
    val storage = c.getSharedPreferences(FILE_DATA_NAME, 0)

    fun guardarEmail(email: String){
        storage.edit().putString(SHARED_EMAIL, email).apply()
    }
    fun leerEmail(): String?{
        return storage.getString(SHARED_EMAIL, null)
    }
    fun borrarTodo(){
        storage.edit().clear().apply()
    }
}