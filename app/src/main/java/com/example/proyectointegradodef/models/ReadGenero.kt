package com.example.proyectointegradodef.models

class ReadGenero {
    constructor()
    constructor(id: Int, nombre: String, portada: String){
        this.id = id
        this.nombre = nombre
        this.portada = portada
    }
    var id = 0
    var nombre = ""
    var portada = ""

    override fun toString(): String {
        return nombre
    }
}