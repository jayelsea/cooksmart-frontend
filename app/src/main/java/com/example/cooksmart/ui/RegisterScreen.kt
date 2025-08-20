package com.example.cooksmart.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cooksmart.model.RegisterRequest
import com.example.cooksmart.model.RegisterResponse
import com.example.cooksmart.network.RecipeApiClient
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun registerUser(email: String, password: String) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val response = RecipeApiClient.apiService.register(RegisterRequest(email, password))
                isLoading = false
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.success == true && registerResponse.userId != null) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess()
                    } else {
                        errorMessage = registerResponse?.message ?: "No se pudo registrar"
                        Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
                    }
                } else {
                    errorMessage = "Error de registro"
                    Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error de red: ${e.localizedMessage}"
                Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFD600)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .width(280.dp)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Registro de usuario",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", color = Color(0xFF000000)) },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFFFFD600)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF000000),
                            unfocusedTextColor = Color(0xFF000000),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFFFD600),
                            unfocusedIndicatorColor = Color(0xFF000000),
                            focusedLabelColor = Color(0xFF000000),
                            unfocusedLabelColor = Color(0xFF000000),
                            cursorColor = Color(0xFF000000)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = Color(0xFF000000)) },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFFFD600)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF000000),
                            unfocusedTextColor = Color(0xFF000000),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFFFD600),
                            unfocusedIndicatorColor = Color(0xFF000000),
                            focusedLabelColor = Color(0xFF000000),
                            unfocusedLabelColor = Color(0xFF000000),
                            cursorColor = Color(0xFF000000)
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { registerUser(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600), contentColor = Color(0xFF000000))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color(0xFF000000), modifier = Modifier.size(18.dp))
                        } else {
                            Text("Registrarse", color = Color(0xFF000000))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = onBackToLogin) {
                        Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF000000))
                    }
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = it, color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
