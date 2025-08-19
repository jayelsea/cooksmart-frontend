package com.example.cooksmart.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.painter.Painter
import com.example.cooksmart.R
import com.example.cooksmart.model.Recipe
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cooksmart.model.NinjasRecipeResponse
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel,
    isAuthenticated: Boolean,
    onRequestLogin: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "list") {
        composable("list") {
            RecipeListScreen(
                viewModel = viewModel,
                isAuthenticated = isAuthenticated,
                onRequestLogin = onRequestLogin,
                navController = navController
            )
        }
        composable("detail") {
            val recipe = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.cooksmart.model.Recipe>("selectedRecipe")
            val ninjasRecipe = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.cooksmart.model.NinjasRecipeResponse>("selectedNinjasRecipe")
            when {
                recipe != null -> RecipeDetailScreenForRecipe(recipe = recipe, navController = navController)
                ninjasRecipe != null -> RecipeDetailScreen(recipe = ninjasRecipe, navController = navController)
            }
        }
    }
}

@Composable
fun RecipeListScreen(
    viewModel: RecipeViewModel,
    isAuthenticated: Boolean,
    onRequestLogin: () -> Unit,
    navController: NavController
) {
    val ninjasRecipes by viewModel.ninjasRecipes.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchKidsRecipesNinjas() }
    var ingredient by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Rápida") }
    var selectedCountry by remember { mutableStateOf("Ninguno") }
    val recipes by viewModel.recipes.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val categories = listOf("Recom.", "Bebida", "Niños")
    val countries = listOf("Ninguno", "México", "Italia", "Japón", "India", "Francia", "Estados Unidos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Nombre de la app y mensaje motivacional
        Text(
            text = "CookSmart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        )
        Text(
            text = "¡Comemos lo que queremos en casa! \uD83C\uDF54",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF6C63FF),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        // Selector de país más abajo
        Text(
            text = "Filtrar recetas por país",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF222222),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 4.dp,
            color = Color(0xFFF8F8FF),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Text(
                    text = selectedCountry,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF6C63FF),
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { expanded = true }
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Seleccionar país",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                                // Actualizar la búsqueda automáticamente al seleccionar país
                                val countryFilter = if (country == "Ninguno") null else country
                                viewModel.fetchRecipesByCategory(selectedCategory, ingredient, countryFilter)
                            }
                        )
                    }
                }
            }
        }
        // Eliminar Spacer innecesario aquí
        // Filtro por país en la búsqueda
        OutlinedTextField(
            value = if (selectedCountry == "Ninguno") "" else selectedCountry,
            onValueChange = {},
            label = { Text("País") },
            enabled = false,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Barra de búsqueda
        OutlinedTextField(
            value = ingredient,
            onValueChange = { ingredient = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            placeholder = { Text("Buscar comida...") },
            shape = RoundedCornerShape(16.dp),
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val countryFilter = if (selectedCountry == "Ninguno") null else selectedCountry
                viewModel.searchByCategory(selectedCategory, ingredient, countryFilter)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Buscar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Chips de categorías
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                        when (category) {
                            "Recom." -> viewModel.fetchRandomRecipes()
                            "Bebida" -> viewModel.fetchBeverageRecipes()
                            "Niños" -> viewModel.fetchKidsRecipes()
                            else -> viewModel.fetchRecipes(null)
                        }
                    },
                    label = { Text(category) },
                    leadingIcon = {
                        when (category) {
                            "Recom." -> Icon(Icons.Outlined.Fastfood, contentDescription = null)
                            "Bebida" -> Icon(Icons.Outlined.LocalDrink, contentDescription = null)
                            "Niños" -> Icon(Icons.Outlined.ChildCare, contentDescription = null)
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFD700),
                        containerColor = Color(0xFFF8F8FF)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Mostrar mensaje de error si existe
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        // Lista de recetas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    isFavorite = favorites.contains(recipe.id.toString()),
                    onFavoriteClick = {
                        if (isAuthenticated) {
                            viewModel.toggleFavorite(recipe.id.toString())
                        } else {
                            onRequestLogin()
                        }
                    },
                    onCardClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedRecipe", recipe)
                        navController.navigate("detail")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Mostrar recetas de Ninjas en tarjetas
        LazyColumn {
            items(ninjasRecipes.orEmpty()) { recipe ->
                if (recipe != null && !recipe.title.isNullOrEmpty()) {
                    var pressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (pressed) 0.96f else 1f,
                        animationSpec = tween(durationMillis = 180)
                    )
                    val coroutineScope = rememberCoroutineScope()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .scale(scale)
                            .clickable {
                                pressed = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(180)
                                    pressed = false
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedNinjasRecipe", recipe)
                                    navController.navigate("detail")
                                }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (!recipe.image.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(recipe.image),
                                    contentDescription = recipe.title,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = recipe.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF6650a4),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Cambia RecipeCard para aceptar onEditClick, onDeleteClick y showEditDelete
@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    showEditDelete: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                BorderStroke(3.dp, Color(0xFFFFD700)), // Borde dorado
                RoundedCornerShape(24.dp)
            )
            .background(Color.White)
            .clickable { onCardClick?.invoke() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen circular grande
            Image(
                painter = if (recipe.imageUrl != null) rememberAsyncImagePainter(recipe.imageUrl) else painterResource(android.R.drawable.ic_menu_gallery),
                contentDescription = recipe.title,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, Color(0xFFFFD700)), CircleShape)
                    .background(Color(0xFFF8F8FF)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    recipe.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    recipe.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF888888),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Favorito", tint = if (isFavorite) Color(0xFFFFD700) else Color(0xFFCCCCCC), modifier = Modifier.size(20.dp).clickable { onFavoriteClick() })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver detalles", color = Color(0xFF6C63FF), modifier = Modifier.clickable { onCardClick?.invoke() })
                }
            }
        }
    }
}
