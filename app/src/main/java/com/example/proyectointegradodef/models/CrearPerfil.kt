package com.example.proyectointegradodef.models

/**
 * Crear perfil
 *
 * @constructor Create empty Crear perfil
 */
class CrearPerfil {
    constructor()
    constructor(id: Int, permisos: Int){
        this.permisos = permisos
        this.id = id
    }
    var permisos = 0
    var id = 0
}