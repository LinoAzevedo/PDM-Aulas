package ipca.example.recipes.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.recipes.models.Recipe
import ipca.example.recipes.ui.theme.RecipesTheme

@Composable
fun RecipesListView(
    modifier: Modifier = Modifier,
    navController: NavController,
    mealType: String? = null
) {

    val viewModel : RecipesListViewModel = viewModel()
    val uiState by viewModel.uiState

    RecipesListViewContent(
        modifier = modifier,
        uiState = uiState,
        navController = navController
    )

    LaunchedEffect(Unit) {
        viewModel.fetchRecipes(mealType)
    }
}

@Composable
fun RecipesListViewContent(
    modifier: Modifier = Modifier,
    uiState: RecipesListState,
    navController: NavController,
) {
    Box(modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text(uiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ) {
                itemsIndexed(
                    items = uiState.recipes,
                ) { index, recipe ->
                    RecipeViewCell(recipe){
                        navController.navigate("recipe/${recipe.id ?: 0}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesListViewPreview(){
    RecipesTheme {
        val navController = rememberNavController()
        RecipesListViewContent(
            uiState = RecipesListState(
                recipes = listOf(
                    Recipe(
                        id= 1,
                        name = "Delicious Pasta",
                        image = "https://example.com/pasta.jpg",
                    ),
                    Recipe(
                        id= 2,
                        name = "Tasty Salad",
                        image = "https://example.com/salad.jpg",
                    )
                )
            ),
            navController = navController
        )
    }


}