package com.example.apptutorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apptutorias.Screen.LoginScreen
import com.example.apptutorias.Screen.RoleSelectionScreen
import com.example.apptutorias.Screen.TutoriasScreen
import com.example.apptutorias.ui.theme.AppTutoriasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTutoriasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "role_selection") {
                        composable("role_selection") {
                            RoleSelectionScreen { selectedRole ->
                                navController.navigate("login/$selectedRole")
                            }
                        }
                        composable("login/{role}") { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: "student"
                            LoginScreen(
                                role = role,
                                onLoginSuccess = {
                                    navController.navigate("tutorias") {
                                        popUpTo("login/$role") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("tutorias") {
                            TutoriasScreen()
                        }
                    }
                }
            }
        }
    }
}
