package com.example.proyectointegradodef.preferences

import android.content.Context

/**
 * Prefs
 *
 * @constructor
 *
 * @param c
 */
class Prefs(c: Context) {
    val FILE_DATA_NAME="fdn"
    val SHARED_EMAIL = "email"
    val storage = c.getSharedPreferences(FILE_DATA_NAME, 0)

    /**
     * Guardar email
     *
     * @param email
     */
    fun guardarEmail(email: String){
        storage.edit().putString(SHARED_EMAIL, email).apply()
    }

    /**
     * Leer email
     *
     * @return
     */
    fun leerEmail(): String?{
        return storage.getString(SHARED_EMAIL, null)
    }

    /**
     * Borrar todo
     *
     */
    fun borrarTodo(){
        storage.edit().clear().apply()
    }
}