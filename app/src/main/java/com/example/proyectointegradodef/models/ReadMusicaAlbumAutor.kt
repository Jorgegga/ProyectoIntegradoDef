package com.example.proyectointegradodef.models

class ReadMusicaAlbumAutor {
    constructor()
    constructor(id: Int, nombre: String, album_id: Int, album: String, autor_id:Int, autor:String, ruta: String, portada: String, descripcion: String, genero_id: Int, numCancion: Int){
        this.id = id
        this.nombre = nombre
        this.album_id = album_id
        this.album = album
        this.autor_id = autor_id
        this.autor = autor
        this.ruta = ruta
        this.portada = portada
        this.descripcion = descripcion
        this.genero_id = genero_id
        this.numCancion = numCancion
    }
    var id = 0
    var nombre = ""
    var album_id = 0
    var album = ""
    var autor_id = 0
    var autor = ""
    var ruta = ""
    var portada = ""
    var descripcion = ""
    var genero_id = 0
    var numCancion = 0
}