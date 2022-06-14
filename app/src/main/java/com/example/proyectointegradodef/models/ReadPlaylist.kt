package com.example.proyectointegradodef.models

/**
 * Read playlist
 *
 * @constructor Create empty Read playlist
 */
class ReadPlaylist {
    constructor()
    constructor(id: Int, music_id: Int, user_id: Int){
        this.id = id
        this.music_id = music_id
        this.user_id = user_id
    }
    var id = 0
    var music_id = 0
    var user_id = 0
}