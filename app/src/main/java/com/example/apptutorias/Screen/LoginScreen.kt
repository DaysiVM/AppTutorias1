package com.example.apptutorias.Screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.apptutorias.util.getRolesFromJWT
import com.example.apptutorias.viewmodel.LoginViewModel
import com.example.apptutorias.viewmodel.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    role: String,
    onLoginSuccess: (String, List<String>) -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(role))
    val loginResult by viewModel.loginResult.observeAsState()

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var bienvenidaMostrada by remember { mutableStateOf(false) }

    val backgroundColor = when (role) {
        "student" -> Color(0xFFBBDEFB)
        "tutor" -> Color(0xFFC8E6C9)
        else -> Color.White
    }

    val buttonColor = when (role) {
        "student" -> Color(0xFF1976D2)
        "tutor" -> Color(0xFF388E3C)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = buttonColor,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = buttonColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = buttonColor,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = buttonColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor ingresa usuario y contraseña"
                    } else {
                        isLoading = true
                        errorMessage = null
                        bienvenidaMostrada = false
                        viewModel.login(username, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Ingresar", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = buttonColor
                )
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            loginResult?.let { result ->
                isLoading = false
                result.onSuccess { token ->
                    val roles = getRolesFromJWT(token)

                    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("jwt", token).apply()
                    prefs.edit().putStringSet("roles", roles.toSet()).apply()

                    if (!bienvenidaMostrada) {
                        Toast.makeText(context, "¡Hola $username!", Toast.LENGTH_LONG).show()
                        bienvenidaMostrada = true
                    }

                    onLoginSuccess(token, roles)
                }
                result.onFailure { error ->

                    errorMessage = "Credenciales o datos incorrectos"
                    isLoading = false
                }
            }
        }
    }
}
