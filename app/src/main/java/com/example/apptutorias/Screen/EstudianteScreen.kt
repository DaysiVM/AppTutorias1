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

    LaunchedEffect(Unit) {
        viewModel.cargarTutorias()
    }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2500)
            mensaje = ""
        }
    }

    val fondo = Color.White
    val tarjetaColor = Color(0xFFE8F5E9)
    val botonColor = Color(0xFF388E3C)

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

                // 🔍 Filtro materia
                OutlinedTextField(
                    value = filtroMateria,
                    onValueChange = { filtroMateria = it },
                    label = { Text("Buscar por materia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f).padding(16.dp)
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
                                Text("Materia: ${tutoria.materia}")
                                Text("Descripción: ${tutoria.descripcion}")
                                Text("Hora: ${tutoria.hora}")
                                Text("Costo: $${tutoria.costo}")
                                Text("Tutor: ${tutoria.nombreTutor}")

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            mensaje = "Te registraste en la tutoría ✅"
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

                                    if ((tutoria.costo ?: 0.0) > 0.0) {
                                        OutlinedButton(
                                            onClick = {
                                                mensaje = "Contactar a ${tutoria.nombreTutor}: ${tutoria.correoTutor}"
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Contactar"
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Contactar")
                                        }
                                    }
                                }
                            }
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
    }
}
