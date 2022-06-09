package com.example.proyectointegradodef.models

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem

class AnnadirPlaylistMusic {
    constructor()
    constructor(id: Int, ruta: MediaItem){
        this.id = id
        this.ruta = ruta
    }
    var id = 0
    var ruta = MediaItem.EMPTY
}