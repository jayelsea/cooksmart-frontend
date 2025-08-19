package com.example.cooksmart.ui

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooksmart.data.FavoriteManager
import com.example.cooksmart.model.Recipe
import com.example.cooksmart.network.RecipeApiService
import com.example.cooksmart.network.RecipeApiClient
import com.example.cooksmart.network.CocktailApiService
import com.example.cooksmart.network.NinjasApiService
import com.example.cooksmart.model.CocktailResponse
import com.example.cooksmart.model.NinjasRecipeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeViewModel(app: Application) : AndroidViewModel(app) {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val favoriteManager = FavoriteManager(app.applicationContext)

    // Usar la instancia de Retrofit ya configurada en RecipeApiClient
    private val api = RecipeApiClient.apiService
    private val cocktailApi = Retrofit.Builder()
        .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CocktailApiService::class.java)
    private val ninjasApi = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val ninjasApiService = ninjasApi.create(NinjasApiService::class.java)

    private val _ninjasRecipes = MutableStateFlow<List<NinjasRecipeResponse>>(emptyList())
    val ninjasRecipes: StateFlow<List<NinjasRecipeResponse>> = _ninjasRecipes

    init {
        viewModelScope.launch {
            favoriteManager.favoritesFlow.collectLatest {
                _favorites.value = it
            }
        }
    }

    fun fetchRecipes(ingredients: List<String>?) {
        viewModelScope.launch {
            try {
                val result = if (ingredients.isNullOrEmpty()) {
                    api.getAllRecipes().body() ?: emptyList()
                } else {
                    api.getExternalRecipes(ingredients.joinToString(",")).body() ?: emptyList()
                }
                _recipes.value = result
            } catch (e: Exception) {
                _recipes.value = emptyList()
            }
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            if (_favorites.value.contains(recipeId)) {
                favoriteManager.removeFavorite(recipeId)
            } else {
                favoriteManager.addFavorite(recipeId)
            }
        }
    }

    // Estado para formularios modales
    private val _showCreateDialog = MutableStateFlow(false)
    private val _showEditDialog = MutableStateFlow(false)
    private val _editRecipe = MutableStateFlow<Recipe?>(null)

    // Para Compose
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog
    val showEditDialog: StateFlow<Boolean> = _showEditDialog
    val editRecipe: StateFlow<Recipe?> = _editRecipe

    fun showCreateRecipeForm() {
        _showCreateDialog.value = true
    }
    fun showEditRecipeForm(recipe: Recipe) {
        _editRecipe.value = recipe
        _showEditDialog.value = true
    }
    fun hideRecipeDialogs() {
        _showCreateDialog.value = false
        _showEditDialog.value = false
        _editRecipe.value = null
    }
    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                val response = api.deleteRecipe(recipeId)
                if (response.isSuccessful) {
                    fetchRecipes(null)
                }
            } catch (_: Exception) {}
        }
    }
    fun createRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val response = api.createRecipe(recipe)
                if (response.isSuccessful) {
                    fetchRecipes(null)
                    hideRecipeDialogs()
                }
            } catch (_: Exception) {}
        }
    }
    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val response = api.updateRecipe(recipe.id, recipe)
                if (response.isSuccessful) {
                    fetchRecipes(null)
                    hideRecipeDialogs()
                }
            } catch (_: Exception) {}
        }
    }
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    fun fetchRandomRecipes(count: Int = 5) {
        viewModelScope.launch {
            val randomRecipes = mutableListOf<Recipe>()
            _errorMessage.value = null // Limpiar error previo
            try {
                repeat(count) {
                    val response = api.getRandomRecipe()
                    val mealsList = response.body()?.meals
                    if (!mealsList.isNullOrEmpty()) {
                        val meal = mealsList.firstOrNull()
                        if (meal != null) {
                            randomRecipes.add(
                                Recipe(
                                    id = meal.idMeal?.toLongOrNull() ?: 0L,
                                    title = meal.strMeal ?: "Receta",
                                    description = meal.strCategory ?: "Recom.",
                                    ingredients = null,
                                    instructions = meal.strInstructions ?: "Sin instrucciones",
                                    imageUrl = meal.strMealThumb ?: ""
                                )
                            )
                        }
                    } else {
                        _errorMessage.value = "No se encontraron recomendaciones. Intenta de nuevo más tarde."
                    }
                }
                if (randomRecipes.isEmpty()) {
                    _errorMessage.value = "No se pudieron obtener recomendaciones. Verifica tu conexión o intenta más tarde."
                }
                _recipes.value = randomRecipes
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al obtener recomendaciones: ${e.message}"
                _recipes.value = emptyList()
            }
        }
    }
    fun fetchBeverageRecipes() {
        viewModelScope.launch {
            try {
                val allRecipes = api.getAllRecipes().body() ?: emptyList()
                val beverages = allRecipes.filter {
                    it.description?.contains("Bebida", ignoreCase = true) == true
                }
                _recipes.value = beverages
            } catch (e: Exception) {
                _recipes.value = emptyList()
            }
        }
    }

    fun fetchKidsRecipes() {
        viewModelScope.launch {
            try {
                val allRecipes = api.getAllRecipes().body() ?: emptyList()
                val kids = allRecipes.filter {
                    it.description?.contains("Niños", ignoreCase = true) == true
                }
                _recipes.value = kids
            } catch (e: Exception) {
                _recipes.value = emptyList()
            }
        }
    }
    @Composable
    fun CreateEditRecipeDialog() {
        val showCreate by showCreateDialog.collectAsState()
        val showEdit by showEditDialog.collectAsState()
        val recipeToEdit by editRecipe.collectAsState()
        // Inicializa los estados solo cuando se muestra el diálogo
        val initialTitle = if (showEdit && recipeToEdit != null) recipeToEdit!!.title else ""
        val initialDescription = if (showEdit && recipeToEdit != null) recipeToEdit!!.description ?: "" else ""
        val initialInstructions = if (showEdit && recipeToEdit != null) recipeToEdit!!.instructions ?: "" else ""
        val initialIngredients = if (showEdit && recipeToEdit != null) recipeToEdit!!.ingredients?.joinToString(", ") ?: "" else ""
        val initialImageUrl = if (showEdit && recipeToEdit != null) recipeToEdit!!.imageUrl ?: "" else ""
        val titleState = remember(showCreate, showEdit) { mutableStateOf(initialTitle) }
        val descriptionState = remember(showCreate, showEdit) { mutableStateOf(initialDescription) }
        val instructionsState = remember(showCreate, showEdit) { mutableStateOf(initialInstructions) }
        val ingredientsState = remember(showCreate, showEdit) { mutableStateOf(initialIngredients) }
        val imageUrlState = remember(showCreate, showEdit) { mutableStateOf(initialImageUrl) }
        val title = titleState.value
        val description = descriptionState.value
        val instructions = instructionsState.value
        val ingredients = ingredientsState.value
        val imageUrl = imageUrlState.value
        if (showCreate || showEdit) {
            AlertDialog(
                onDismissRequest = { hideRecipeDialogs() },
                confirmButton = {
                    Button(onClick = {
                        val recipe = Recipe(
                            id = recipeToEdit?.id ?: 0L,
                            title = title,
                            description = description,
                            ingredients = ingredients.split(",").map { it.trim() },
                            instructions = instructions,
                            imageUrl = imageUrl
                        )
                        if (showCreate) createRecipe(recipe) else updateRecipe(recipe)
                    }) {
                        Text(if (showCreate) "Crear" else "Actualizar")
                    }
                },
                dismissButton = {
                    Button(onClick = { hideRecipeDialogs() }) { Text("Cancelar") }
                },
                title = { Text(if (showCreate) "Crear receta" else "Editar receta") },
                text = {
                    Column {
                        OutlinedTextField(value = title, onValueChange = { titleState.value = it }, label = { Text("Título") })
                        OutlinedTextField(value = description, onValueChange = { descriptionState.value = it }, label = { Text("Descripción") })
                        OutlinedTextField(value = instructions, onValueChange = { instructionsState.value = it }, label = { Text("Instrucciones") })
                        OutlinedTextField(value = ingredients, onValueChange = { ingredientsState.value = it }, label = { Text("Ingredientes (separados por coma)") })
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrlState.value = it }, label = { Text("URL de imagen") })
                    }
                }
            )
        }
    }

    fun fetchRecipesByCategory(category: String, ingredient: String? = null, country: String? = null) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                when (category) {
                    "Recom." -> {
                        fetchRandomRecipes()
                    }
                    "Bebida" -> {
                        val response = cocktailApi.getRandomCocktail()
                        val drinks = response.body()?.drinks
                        if (drinks != null && drinks.isNotEmpty()) {
                            _recipes.value = drinks.map {
                                Recipe(
                                    id = it.idDrink.toLongOrNull() ?: 0L,
                                    title = it.strDrink,
                                    description = it.strCategory ?: "Bebida",
                                    ingredients = null,
                                    instructions = it.strInstructions ?: "Sin instrucciones",
                                    imageUrl = it.strDrinkThumb ?: ""
                                )
                            }
                        } else {
                            _recipes.value = emptyList()
                            _errorMessage.value = "No se encontraron bebidas."
                        }
                    }
                    "Niños" -> {
                        val response = ninjasApiService.getKidsRecipes()
                        val recipes = response.body()
                        if (recipes != null && recipes.isNotEmpty()) {
                            _recipes.value = recipes.map { ninja ->
                                Recipe(
                                    id = 0L,
                                    title = ninja.title,
                                    description = "Para niños",
                                    ingredients = ninja.ingredients?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
                                    instructions = ninja.instructions ?: "Sin instrucciones",
                                    imageUrl = ninja.image ?: ""
                                )
                            }
                        } else {
                            _recipes.value = emptyList()
                            _errorMessage.value = "No se encontraron recetas para niños."
                        }
                    }
                    else -> {
                        val ingredientsList = ingredient?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                        fetchRecipes(ingredientsList)
                    }
                }
            } catch (e: Exception) {
                _recipes.value = emptyList()
                _errorMessage.value = "Error al obtener recetas: ${e.message}"
            }
        }
    }

    fun searchByCategory(category: String, query: String, country: String?) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                when (category) {
                    "Recom." -> {
                        fetchRandomRecipes()
                    }
                    "Bebida" -> {
                        val response = when {
                            query.isBlank() -> cocktailApi.getRandomCocktail()
                            query.contains(":") -> {
                                val ingredient = query.substringBefore(":").trim()
                                cocktailApi.searchCocktailByIngredient(ingredient)
                            }
                            else -> cocktailApi.searchCocktailByName(query)
                        }
                        val drinks = response.body()?.drinks
                        if (drinks != null && drinks.isNotEmpty()) {
                            _recipes.value = drinks.map {
                                Recipe(
                                    id = it.idDrink.toLongOrNull() ?: 0L,
                                    title = it.strDrink,
                                    description = it.strCategory ?: "Bebida",
                                    ingredients = null,
                                    instructions = it.strInstructions ?: "Sin instrucciones",
                                    imageUrl = it.strDrinkThumb ?: ""
                                )
                            }
                        } else {
                            _recipes.value = emptyList()
                            _errorMessage.value = "No se encontraron bebidas."
                        }
                    }
                    "Niños" -> {
                        val response = ninjasApiService.getKidsRecipes()
                        val recipes = response.body()
                        if (recipes != null && recipes.isNotEmpty()) {
                            val filtered = if (query.isBlank()) recipes else recipes.filter {
                                it.title.contains(query, ignoreCase = true) ||
                                (it.ingredients?.contains(query, ignoreCase = true) == true)
                            }
                            _recipes.value = filtered.map { ninja ->
                                Recipe(
                                    id = 0L,
                                    title = ninja.title,
                                    description = "Para niños",
                                    ingredients = ninja.ingredients?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
                                    instructions = ninja.instructions ?: "Sin instrucciones",
                                    imageUrl = ninja.image ?: ""
                                )
                            }
                        } else {
                            _recipes.value = emptyList()
                            _errorMessage.value = "No se encontraron recetas para niños."
                        }
                    }
                    else -> {
                        val ingredientsList = query.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        fetchRecipes(ingredientsList)
                    }
                }
            } catch (e: Exception) {
                _recipes.value = emptyList()
                _errorMessage.value = "Error al buscar recetas: ${e.message}"
            }
        }
    }

    fun fetchKidsRecipesNinjas() {
        viewModelScope.launch {
            try {
                val response = ninjasApiService.getKidsRecipes()
                if (response.isSuccessful) {
                    _ninjasRecipes.value = response.body() ?: emptyList()
                } else {
                    _ninjasRecipes.value = emptyList()
                }
            } catch (e: Exception) {
                _ninjasRecipes.value = emptyList()
            }
        }
    }
}
