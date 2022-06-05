package com.example.proyectointegradodef.models

class ReadAlbum {
    constructor()
    constructor(id: Int, idautor: Int, titulo: String, portada: String, descripcion: String){
        this.id = id
        this.idautor = idautor
        this.titulo = titulo
        this.portada = portada
        this.descripcion = descripcion
    }
    var id = 0
    var idautor = 0
    var titulo = ""
    var portada = ""
    var descripcion = ""

}