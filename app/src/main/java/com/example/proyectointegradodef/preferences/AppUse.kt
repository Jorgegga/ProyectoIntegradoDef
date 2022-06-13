package com.example.proyectointegradodef.preferences

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData

class AppUse: Application() {
    companion object{
        lateinit var appContext : Context
        lateinit var prefs: Prefs
        var nombre = ""
        var autor = ""
        var cancion = ""
        var id = 0
        var album_id = 0
        var autor_id = 0
        var user_id = 0
        var recyclerPosition = 0
        var permisos = 0
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        appContext = applicationContext
    }
}