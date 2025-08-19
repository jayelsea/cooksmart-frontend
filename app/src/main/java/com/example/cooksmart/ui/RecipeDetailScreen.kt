package com.example.cooksmart.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.cooksmart.model.NinjasRecipeResponse
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(recipe: NinjasRecipeResponse, navController: NavController) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (visible) 1f else 0.95f, animationSpec = tween(500))
    LaunchedEffect(Unit) { visible = true }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        TopAppBar(
            title = { Text(text = "Detalles de la receta", color = Color.Black) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFD700), titleContentColor = Color.Black)
        )
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .scale(scale)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!recipe.image.isNullOrEmpty()) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = rememberAsyncImagePainter(recipe.image),
                                contentDescription = recipe.title,
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White)
                                    .border(BorderStroke(6.dp, Color(0xFFFFD700)), RoundedCornerShape(24.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "", // No hay descripción en NinjasRecipeResponse
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF222222),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFFFD700), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingredientes:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Text(
                        text = recipe.ingredients ?: "No disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFFFD700), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Instrucciones:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Text(
                        text = recipe.instructions ?: "No disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
