package com.example.proyectointegradodef.models

class ReadMusica {
    constructor()
    constructor(nom: String, aut: String, canc: String){
        nombre = nom
        autor = aut
        ruta = canc
    }
    var nombre = ""
    var autor = ""
    var ruta = ""

    fun leerNombre(): String{
        return nombre
    }
    fun leerAutor(): String{
        return autor
    }
    fun leerCancion(): String{
        return ruta
    }
}