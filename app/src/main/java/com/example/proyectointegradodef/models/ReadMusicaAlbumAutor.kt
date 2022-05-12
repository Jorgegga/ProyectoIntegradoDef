package com.example.proyectointegradodef.models

class ReadMusicaAlbumAutor {
    constructor()
    constructor(nombre: String, album: String, autor:String, ruta: String, portada: String){
        this.nombre = nombre
        this.album = album
        this.autor = autor
        this.ruta = ruta
        this.portada = portada
    }
    var nombre = ""
    var album = ""
    var autor = ""
    var ruta = ""
    var portada = ""
}