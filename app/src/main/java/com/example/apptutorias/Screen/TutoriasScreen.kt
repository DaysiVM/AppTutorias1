package com.example.apptutorias.Screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptutorias.tutoria.Tutoria
import com.example.apptutorias.viewmodel.TutoriaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    var tutoriaAEliminar by remember { mutableStateOf<Tutoria?>(null) }
    var mensajeExito by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

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

    LaunchedEffect(mensajeExito) {
        if (mensajeExito.isNotEmpty()) {
            delay(2500)
            mensajeExito = ""
        }
    }

    val tutorColor = Color(0xFF388E3C)
    val textColor = Color.Black
    val cardBackground = Color(0xFFE8F5E9) // verde bajito

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    items(tutorias) { tutoria ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Materia: ${tutoria.materia}", color = textColor, fontWeight = FontWeight.SemiBold)
                                Text("Descripción: ${tutoria.descripcion}", color = textColor)
                                Text("Hora: ${tutoria.hora}", color = textColor)
                                Text("Costo: $${tutoria.costo}", color = textColor)
                                Text("Tutor: ${tutoria.nombreTutor}", color = textColor)
                                Text("Correo: ${tutoria.correoTutor}", color = textColor)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row {
                                    IconButton(
                                        onClick = {
                                            tutoriaEditando = tutoria
                                            materia = tutoria.materia ?: ""
                                            descripcion = tutoria.descripcion ?: ""
                                            hora = tutoria.hora ?: ""
                                            costo = tutoria.costo?.toString() ?: ""
                                            nombreTutor = tutoria.nombreTutor ?: ""
                                            correoTutor = tutoria.correoTutor ?: ""
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = tutorColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            tutoriaAEliminar = tutoria
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.Red
                                        )
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
                        .padding(16.dp),
                    containerColor = tutorColor,
                    contentColor = Color.White
                ) {
                    Text("+")
                }
            }
        }

        // ✔️ Notificación centrada animada
        AnimatedVisibility(
            visible = mensajeExito.isNotEmpty(),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = tutorColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = mensajeExito,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Dialogo Editar
    if (tutoriaEditando != null) {
        AlertDialog(
            onDismissRequest = { tutoriaEditando = null },
            confirmButton = {
                Button(onClick = {
                    if (materia.isBlank() || descripcion.isBlank()) {
                        mensajeExito = "❌ Completa todos los campos"
                        return@Button
                    }
                    isSubmitting = true
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
                        isSubmitting = false
                        tutoriaEditando = null
                        mensajeExito = "✅ Tutoría actualizada"
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = tutorColor)) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Guardar cambios", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { tutoriaEditando = null }) {
                    Text("Cancelar", color = textColor)
                }
            },
            title = { Text("Editar Tutoría", color = textColor) },
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

    // Dialogo Eliminar
    if (tutoriaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { tutoriaAEliminar = null },
            confirmButton = {
                Button(
                    onClick = {
                        tutoriaAEliminar?.id?.let {
                            viewModel.eliminarTutoria(it)
                            mensajeExito = "✅ Tutoría eliminada"
                        }
                        tutoriaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sí, eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    mensajeExito = "❌ Cancelado"
                    tutoriaAEliminar = null
                }) {
                    Text("Cancelar", color = textColor)
                }
            },
            title = { Text("⚠️ Confirmar eliminación", color = textColor) },
            text = { Text("¿Estás seguro de eliminar esta tutoría?", color = textColor) }
        )
    }

    // Dialogo Nueva Tutoría
    if (mostrarDialogoNuevaTutoria) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaTutoria = false },
            confirmButton = {
                Button(onClick = {
                    if (materia.isBlank() || descripcion.isBlank()) {
                        mensajeExito = "❌ Completa todos los campos"
                        return@Button
                    }
                    isSubmitting = true
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
                        isSubmitting = false
                        mostrarDialogoNuevaTutoria = false
                        mensajeExito = "✅ Tutoría creada"
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = tutorColor)) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Agregar", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoNuevaTutoria = false }) {
                    Text("Cancelar", color = textColor)
                }
            },
            title = { Text("Agregar Tutoría", color = textColor) },
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
