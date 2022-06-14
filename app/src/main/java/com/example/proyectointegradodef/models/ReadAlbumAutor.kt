package com.example.proyectointegradodef.models

/**
 * Read album autor
 *
 * @constructor Create empty Read album autor
 */
class ReadAlbumAutor {
    constructor()
    constructor(id: Int, autor_id: Int, autor: String, titulo: String, portada: String, descripcion: String, genero_id: Int){
        this.id = id
        this.autor_id = autor_id
        this.autor = autor
        this.titulo = titulo
        this.portada = portada
        this.descripcion = descripcion
        this.genero_id = genero_id
    }
    var id = 0
    var autor_id = 0
    var autor = ""
    var titulo = ""
    var portada = ""
    var descripcion = ""
    var genero_id = 0
}