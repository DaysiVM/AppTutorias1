// TutoriasScreen.kt
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptutorias.tutoria.Tutoria
import com.example.apptutorias.viewmodel.TutoriaViewModel

@Composable
fun TutoriasScreen(
    viewModel: TutoriaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val tutorias by viewModel.tutorias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var tutoriaEditando by remember { mutableStateOf<Tutoria?>(null) }
    var mostrarDialogoNuevaTutoria by remember { mutableStateOf(false) }

    var materia by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var nombreTutor by remember { mutableStateOf("") }
    var correoTutor by remember { mutableStateOf("") }

    fun limpiarCampos() {
        materia = ""
        descripcion = ""
        hora = ""
        costo = ""
        nombreTutor = ""
        correoTutor = ""
    }

    LaunchedEffect(Unit) {
        viewModel.cargarTutorias()
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column {
                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
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
                                            viewModel.eliminarTutoria(it)
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
                        tutoriaEditando = null
                        mostrarDialogoNuevaTutoria = true
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                ) {
                    Text("+")
                }
            }
        }
    }

    // Dialogo para editar
    if (tutoriaEditando != null) {
        AlertDialog(
            onDismissRequest = { tutoriaEditando = null },
            confirmButton = {
                Button(onClick = {
                    val actualizado = Tutoria(
                        id = tutoriaEditando!!.id,
                        materia = materia,
                        descripcion = descripcion,
                        hora = hora,
                        costo = costo.toDoubleOrNull() ?: 0.0,
                        nombreTutor = nombreTutor,
                        correoTutor = correoTutor
                    )
                    viewModel.actualizarTutoria(actualizado.id!!, actualizado) {
                        tutoriaEditando = null
                    }
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

    // Dialogo para crear nueva tutoria
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
                    viewModel.crearTutoria(nuevaTutoria) {
                        mostrarDialogoNuevaTutoria = false
                    }
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
