package ipca.example.recipes.ui.recipes

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.recipes.models.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import ipca.example.recipes.models.Recipe

data class RecipesDatabaseState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipesDatabaseViewModel : ViewModel() {
    var uiState = mutableStateOf(RecipesDatabaseState())
        private set

    private val client = OkHttpClient()

    fun fetchRecipes(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = AppDatabase.getInstance(context)?.recipeDao()?.getAll()
            viewModelScope.launch(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    recipes = recipes ?: emptyList(),
                )
            }
        }

    }

}
