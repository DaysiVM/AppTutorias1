package com.example.apptutorias.tutoria

data class Tutoria(
    val id: Long? = null,
    val materia: String,
    val descripcion: String?,
    val hora: String,
    val costo: Double,
    val nombreTutor: String?,
    val correoTutor: String?
)
