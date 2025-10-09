package com.example.app1.model

data class User(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val edad: Int
) {
    // Constructor secundario para cuando no tenemos ID (nuevo usuario)
    constructor(nombre: String, email: String, edad: Int) : this(0, nombre, email, edad)
}