package com.example.apptutorias.network

import com.example.apptutorias.network.model.LoginRequest
import com.example.apptutorias.network.model.LoginResponse
import com.example.apptutorias.tutoria.Tutoria
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface TutoriasApiService {

    // Endpoint de login
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Obtener todas las tutorías
    @GET("tutorias")
    fun getTutorias(): Call<List<Tutoria>>

    // Crear una tutoría
    @POST("tutorias")
    fun crearTutoria(@Body tutoria: Tutoria): Call<Tutoria>

    // Eliminar una tutoría
    @DELETE("tutorias/{id}")
    fun eliminarTutoria(@Path("id") id: Long): Call<Void>

    // Actualizar una tutoría
    @PUT("tutorias/{id}")
    fun actualizarTutoria(@Path("id") id: Long, @Body tutoria: Tutoria): Call<Tutoria>
    abstract fun agregarTutoria(nuevaTutoria: Tutoria): Any

}


