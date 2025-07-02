package com.example.apptutorias.Screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apptutorias.network.RetrofitClient
import com.example.apptutorias.tutoria.Tutoria
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun TutoriasScreen(modifier: Modifier = Modifier) {
    var tutorias by remember { mutableStateOf<List<Tutoria>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var tutoriaEditando by remember { mutableStateOf<Tutoria?>(null) }
    var mostrarDialogoNuevaTutoria by remember { mutableStateOf(false) }

    var materia by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var nombreTutor by remember { mutableStateOf("") }
    var correoTutor by remember { mutableStateOf("") }

    fun cargarTutorias() {
        isLoading = true
        RetrofitClient.apiService.getTutorias().enqueue(object : Callback<List<Tutoria>> {
            override fun onResponse(call: Call<List<Tutoria>>, response: Response<List<Tutoria>>) {
                tutorias = response.body() ?: emptyList()
                isLoading = false
            }

            override fun onFailure(call: Call<List<Tutoria>>, t: Throwable) {
                Log.e("API", "Error: ${t.message}")
                isLoading = false
            }
        })
    }

    fun limpiarCampos() {
        materia = ""
        descripcion = ""
        hora = ""
        costo = ""
        nombreTutor = ""
        correoTutor = ""
    }

    LaunchedEffect(Unit) {
        cargarTutorias()
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(tutorias) { tutoria ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Materia: ${tutoria.materia}")
                            Text("Descripción: ${tutoria.descripcion}")
                            Text("Hora: ${tutoria.hora}")
                            Text("Costo: $${tutoria.costo}")
                            Text("Tutor: ${tutoria.nombreTutor}")
                            Text("Correo: ${tutoria.correoTutor}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                Button(onClick = {
                                    tutoriaEditando = tutoria
                                    materia = tutoria.materia ?: ""
                                    descripcion = tutoria.descripcion ?: ""
                                    hora = tutoria.hora ?: ""
                                    costo = tutoria.costo?.toString() ?: ""
                                    nombreTutor = tutoria.nombreTutor ?: ""
                                    correoTutor = tutoria.correoTutor ?: ""
                                }) {
                                    Text("Editar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(onClick = {
                                    tutoria.id?.let {
                                        RetrofitClient.apiService.eliminarTutoria(it).enqueue(object : Callback<Void> {
                                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                cargarTutorias()
                                            }

                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                Log.e("DELETE", "Error al eliminar", t)
                                            }
                                        })
                                    }
                                }) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    limpiarCampos()
                    mostrarDialogoNuevaTutoria = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("+")
            }
        }
    }

    tutoriaEditando?.let { editando ->
        AlertDialog(
            onDismissRequest = { tutoriaEditando = null },
            confirmButton = {
                Button(onClick = {
                    val actualizado = Tutoria(
                        id = editando.id,
                        materia = materia,
                        descripcion = descripcion,
                        hora = hora,
                        costo = costo.toDoubleOrNull() ?: 0.0,
                        nombreTutor = nombreTutor,
                        correoTutor = correoTutor
                    )

                    RetrofitClient.apiService.actualizarTutoria(editando.id!!, actualizado)
                        .enqueue(object : Callback<Tutoria> {
                            override fun onResponse(call: Call<Tutoria>, response: Response<Tutoria>) {
                                tutoriaEditando = null
                                cargarTutorias()
                            }

                            override fun onFailure(call: Call<Tutoria>, t: Throwable) {
                                Log.e("PUT", "Error al actualizar", t)
                            }
                        })
                }) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { tutoriaEditando = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar Tutoría") },
            text = {
                Column {
                    OutlinedTextField(value = materia, onValueChange = { materia = it }, label = { Text("Materia") })
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                    OutlinedTextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora") })
                    OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo") })
                    OutlinedTextField(value = nombreTutor, onValueChange = { nombreTutor = it }, label = { Text("Nombre del Tutor") })
                    OutlinedTextField(value = correoTutor, onValueChange = { correoTutor = it }, label = { Text("Correo del Tutor") })
                }
            }
        )
    }

    if (mostrarDialogoNuevaTutoria) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaTutoria = false },
            confirmButton = {
                Button(onClick = {
                    val nuevaTutoria = Tutoria(
                        id = null,
                        materia = materia,
                        descripcion = descripcion,
                        hora = hora,
                        costo = costo.toDoubleOrNull() ?: 0.0,
                        nombreTutor = nombreTutor,
                        correoTutor = correoTutor
                    )
                    RetrofitClient.apiService.crearTutoria(nuevaTutoria)
                        .enqueue(object : Callback<Tutoria> {
                            override fun onResponse(call: Call<Tutoria>, response: Response<Tutoria>) {
                                mostrarDialogoNuevaTutoria = false
                                cargarTutorias()
                            }

                            override fun onFailure(call: Call<Tutoria>, t: Throwable) {
                                Log.e("POST", "Error al agregar", t)
                            }
                        })
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoNuevaTutoria = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Agregar Tutoría") },
            text = {
                Column {
                    OutlinedTextField(value = materia, onValueChange = { materia = it }, label = { Text("Materia") })
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                    OutlinedTextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora") })
                    OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo") })
                    OutlinedTextField(value = nombreTutor, onValueChange = { nombreTutor = it }, label = { Text("Nombre del Tutor") })
                    OutlinedTextField(value = correoTutor, onValueChange = { correoTutor = it }, label = { Text("Correo del Tutor") })
                }
            }
        )
    }
}
