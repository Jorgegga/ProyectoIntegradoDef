package com.example.proyectointegradodef.models

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem

/**
 * Annadir playlist music
 *
 * @constructor Create empty Annadir playlist music
 */
class AnnadirPlaylistMusic {
    constructor()
    constructor(id: Int, numCancion: Int, ruta: MediaItem){
        this.id = id
        this.ruta = ruta
        this.numCancion = numCancion
    }
    var id = 0
    var ruta = MediaItem.EMPTY
    var numCancion = 0
}