package com.example.proyectointegradodef.models

/**
 * Read autor
 *
 * @constructor Create empty Read autor
 */
class ReadAutor {
    constructor()
    constructor(id: Int, nombre: String, foto: String, descripcion: String){
        this.id = id
        this.nombre = nombre
        this.foto = foto
        this.descripcion = descripcion
    }


    var id = 0
    var nombre = ""
    var foto = ""
    var descripcion = ""

    override fun toString(): String {
        return nombre
    }


}