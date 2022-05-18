package com.example.proyectointegradodef.models

class ReadAlbumAutor {
    constructor()
    constructor(id: Int, autorId: Int, autor: String, titulo: String, portada: String){
        this.id = id
        this.autorId = id
        this.autor = autor
        this.titulo = titulo
        this.portada = portada
    }
    var id = 0
    var autorId = 0
    var autor = ""
    var titulo = ""
    var portada = ""
}