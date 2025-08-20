package com.example.cooksmart.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input. PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cooksmart.network.RecipeApiClient
import kotlinx.coroutines.launch
import com.example.cooksmart.model.Recipe
import com.example.cooksmart.model.LoginRequest
import com.example.cooksmart.model.LoginResponse

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    isModal: Boolean = false // Nuevo parámetro para distinguir si es modal
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun loginUser(email: String, password: String) {
        emailError = if (!validateEmail(email)) "Email inválido" else null
        passwordError = if (!validatePassword(password)) "Contraseña muy corta" else null
        if (emailError != null || passwordError != null) return

        errorMessage = null
        isLoading = true
        coroutineScope.launch {
            try {
                val response = RecipeApiClient.apiService.login(LoginRequest(email, password))
                isLoading = false
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true && loginResponse.userId != null) {
                        if (!isModal) Toast.makeText(context, "Bienvenido!", Toast.LENGTH_LONG).show()
                        onLoginSuccess()
                    } else {
                        errorMessage = loginResponse?.message ?: "Credenciales incorrectas"
                        if (!isModal) Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
                    }
                } else {
                    errorMessage = "Error de autenticación"
                    if (!isModal) Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error de red: ${e.localizedMessage}"
                if (!isModal) Toast.makeText(context, errorMessage!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111)), // Fondo general negro
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isModal,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            Card(
                modifier = Modifier
                    .width(280.dp)
                    .padding(8.dp)
                    .graphicsLayer { shadowElevation = 16f },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "CookSmart",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email", color = Color(0xFF000000)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email", tint = Color(0xFFFFD600)) },
                        isError = emailError != null,
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
                    if (emailError != null) {
                        Text(emailError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Contraseña", color = Color(0xFF000000)) },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña", tint = Color(0xFFFFD600)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Color(0xFFFFD600)
                                )
                            }
                        },
                        isError = passwordError != null,
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
                    if (passwordError != null) {
                        Text(passwordError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { loginUser(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600), contentColor = Color(0xFF000000))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color(0xFF000000), modifier = Modifier.size(18.dp))
                        } else {
                            Text("Iniciar sesión", color = Color(0xFF000000))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes cuenta? Regístrate", color = Color(0xFF000000))
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
