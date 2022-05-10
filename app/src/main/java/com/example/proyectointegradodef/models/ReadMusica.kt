package com.example.proyectointegradodef.models

class ReadMusica {
    constructor()
    constructor(nombre: String, autor_id: Int, album_id: Int, descripcion: String, genero_id: Int, id: Int, numCancion: Int, portada: String, ruta: String){
        this.nombre = nombre
        this.autor_id = autor_id
        this.album_id = album_id
        this.descripcion = descripcion
        this.genero_id = genero_id
        this.id = id
        this.numCancion = numCancion
        this.portada = portada
        this.ruta = ruta
    }
    var nombre = ""
    var autor_id = 0
    var album_id = 0
    var descripcion = ""
    var genero_id = 0
    var id = 0
    var numCancion = 0
    var portada = ""
    var ruta = ""
}