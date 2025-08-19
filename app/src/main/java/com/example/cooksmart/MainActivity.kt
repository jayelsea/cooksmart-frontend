package com.example.cooksmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cooksmart.ui.theme.CookSmartTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooksmart.ui.LoginScreen
import com.example.cooksmart.ui.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CookSmartTheme {
                // Estado global de autenticación
                var isAuthenticated by remember { mutableStateOf(false) }
                var showRegister by remember { mutableStateOf(false) }
                var showLoginModal by remember { mutableStateOf(false) }
                val recipeViewModel: com.example.cooksmart.ui.RecipeViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application))

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pantalla principal de recetas
                    com.example.cooksmart.ui.RecipeScreen(
                        viewModel = recipeViewModel,
                        isAuthenticated = isAuthenticated,
                        onRequestLogin = { showLoginModal = true }
                    )
                    // Modal de login
                    if (showLoginModal) {
                        AlertDialog(
                            onDismissRequest = { showLoginModal = false },
                            confirmButton = {},
                            dismissButton = {},
                            title = { Text("Iniciar sesión") },
                            text = {
                                LoginScreen(
                                    onLoginSuccess = {
                                        isAuthenticated = true
                                        showLoginModal = false
                                    },
                                    onNavigateToRegister = {
                                        showLoginModal = false
                                        showRegister = true
                                    },
                                    isModal = true // Nuevo parámetro para distinguir modal
                                )
                            }
                        )
                    }
                    // Pantalla de registro (opcional)
                    if (showRegister) {
                        com.example.cooksmart.ui.RegisterScreen(
                            onRegisterSuccess = {
                                isAuthenticated = true
                                showRegister = false
                            },
                            onBackToLogin = {
                                showRegister = false
                                showLoginModal = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CookSmartTheme {
        Greeting("Android")
    }
}