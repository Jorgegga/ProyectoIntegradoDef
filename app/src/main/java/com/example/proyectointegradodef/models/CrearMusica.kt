package com.example.proyectointegradodef.models

class CrearMusica {
    constructor()
    constructor(album_id: Int, autor_id: Int, descripcion: String, genero_id: Int, numCancion: Int, portada: String, ruta: String, nombre: String, id: Int){
        this.album_id = album_id
        this.autor_id = autor_id
        this.descripcion = descripcion
        this.genero_id = genero_id
        this.numCancion = numCancion
        this.portada = portada
        this.ruta = ruta
        this.nombre = nombre
        this.id = id
    }
    var album_id = 0
    var autor_id = 0
    var descripcion = ""
    var genero_id = 0
    var numCancion = 0
    var portada = ""
    var ruta = ""
    var nombre = ""
    var id = 0
}