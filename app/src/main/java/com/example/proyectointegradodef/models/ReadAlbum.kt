package com.example.proyectointegradodef.models

class ReadAlbum {
    constructor()
    constructor(id: Int, autor_id: Int, titulo: String, portada: String, descripcion: String, genero_id: Int){
        this.id = id
        this.autor_id = autor_id
        this.titulo = titulo
        this.portada = portada
        this.descripcion = descripcion
        this.genero_id = genero_id
    }
    var id = 0
    var autor_id = 0
    var titulo = ""
    var portada = ""
    var descripcion = ""
    var genero_id = 0

    override fun toString(): String {
        return titulo
    }
}