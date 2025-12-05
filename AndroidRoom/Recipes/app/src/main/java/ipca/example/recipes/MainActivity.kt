package ipca.example.recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import ipca.example.recipes.ui.recipes.RecipesListView
import ipca.example.recipes.ui.components.MyBottomBar
import ipca.example.recipes.ui.theme.RecipesTheme
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import ipca.example.recipes.models.AppDatabase
import ipca.example.recipes.models.Recipe
import ipca.example.recipes.ui.recipes.RecipeDetailView
import ipca.example.recipes.ui.recipes.RecipesDatabaseView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            var navTitle by remember { mutableStateOf("Recipes") }
            var isHome by remember { mutableStateOf(true) }
            var recipeOnScreen by remember { mutableStateOf<Recipe?>(null) }

            val scope = rememberCoroutineScope()

            RecipesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(navTitle)
                            },

                            navigationIcon = {
                                if (!isHome) {
                                    IconButton(onClick = {
                                        navController.popBackStack()
                                    }) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },

                            actions = {
                                if( !isHome )
                                IconButton(
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            AppDatabase.getInstance(context)?.recipeDao()?.insert(recipeOnScreen!!)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorite"
                                    )
                                }
                            }

                        )
                    },
                    bottomBar = {
                        MyBottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = "All"
                    ) {

                        composable("All") {
                            navTitle = "Recipes"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = null         // <- sem filtro
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Breakfast") {
                            navTitle = "Breakfast"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = "Breakfast",
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Lunch") {
                            navTitle = "Lunch"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = "Lunch",
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Dinner") {
                            navTitle = "Dinner"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = "Dinner",
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Dessert") {
                            navTitle = "Dessert"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = "Dessert",
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Beverage") {
                            navTitle = "Beverage"
                            isHome = true
                            RecipesListView(
                                navController = navController,
                                mealType = "Beverage",
                            ){
                                recipeOnScreen = it
                            }
                        }

                        composable("Favorites") {
                            navTitle = "Favorites"
                            isHome = true
                            RecipesDatabaseView(
                                navController = navController
                            )
                        }

                        composable(
                            route = "recipe/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType }),
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments!!.getInt("id")
                            isHome = false
                            RecipeDetailView(id = id, navController = navController)

                        }
                    }
                }
            }
        }
    }
}
