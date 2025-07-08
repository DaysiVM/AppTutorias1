package com.example.apptutorias.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptutorias.viewmodel.TutoriaViewModel
import kotlinx.coroutines.delay

@Composable
fun EstudianteScreen(
    viewModel: TutoriaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val tutorias by viewModel.tutorias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var mensaje by remember { mutableStateOf("") }
    var filtroMateria by remember { mutableStateOf("") }
    val tutoriasRegistradas = remember { mutableStateListOf<String>() }
    var showRegistradas by remember { mutableStateOf(false) }
    var tutorCorreo by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarTutorias()
    }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2000)
            mensaje = ""
        }
    }

    val fondo = Color.White
    val tarjetaColor = Color(0xFFF1F8E9)
    val botonColor = Color(0xFF388E3C)
    val textoColor = Color.Black

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // 🔍 Filtro
                OutlinedTextField(
                    value = filtroMateria,
                    onValueChange = { filtroMateria = it },
                    label = { Text("Buscar por materia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )


                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    val tutoriasFiltradas = if (filtroMateria.isBlank()) {
                        tutorias
                    } else {
                        tutorias.filter {
                            it.materia?.contains(filtroMateria, ignoreCase = true) == true
                        }
                    }

                    items(tutoriasFiltradas) { tutoria ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = tarjetaColor),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Materia: ${tutoria.materia ?: "-"}", color = textoColor, fontWeight = FontWeight.Bold)
                                Text("Descripción: ${tutoria.descripcion ?: "-"}", color = textoColor)
                                Text("Hora: ${tutoria.hora ?: "-"}", color = textoColor)
                                Text("Costo: $${tutoria.costo ?: 0.0}", color = textoColor)
                                Text("Tutor: ${tutoria.nombreTutor ?: "-"}", color = textoColor)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            mensaje = "Te registraste en la tutoría ✅"
                                            tutoria.materia?.let { tutoriasRegistradas.add(it) }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = botonColor)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HowToReg,
                                            contentDescription = "Registrarse",
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Registrarse", color = Color.White)
                                    }

                                    IconButton(
                                        onClick = {
                                            tutorCorreo = "${tutoria.correoTutor ?: "No disponible"}"
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Ver correo"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para ver tutorías registradas
                Button(
                    onClick = { showRegistradas = !showRegistradas },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = botonColor)
                ) {
                    Text(
                        text = if (showRegistradas) "Ocultar mis tutorías registradas" else "Ver mis tutorías registradas",
                        color = Color.White
                    )
                }

                AnimatedVisibility(
                    visible = showRegistradas && tutoriasRegistradas.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Mis tutorías registradas:", fontWeight = FontWeight.Bold, color = textoColor)
                        tutoriasRegistradas.distinct().forEach {
                            Text("• $it", color = textoColor)
                        }
                    }
                }
            }
        }


        AnimatedVisibility(
            visible = mensaje.isNotEmpty(),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = botonColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(
                    text = mensaje,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }


        if (tutorCorreo != null) {
            AlertDialog(
                onDismissRequest = { tutorCorreo = null },
                confirmButton = {
                    TextButton(onClick = { tutorCorreo = null }) {
                        Text("OK")
                    }
                },
                title = { Text("Correo del tutor") },
                text = { Text(tutorCorreo ?: "-") }
            )
        }
    }
}
