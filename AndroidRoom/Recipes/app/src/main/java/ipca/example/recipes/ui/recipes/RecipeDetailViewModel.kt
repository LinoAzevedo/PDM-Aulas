package ipca.example.recipes.ui.recipes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import ipca.example.recipes.models.Recipe

data class RecipeDetailState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipeDetailViewModel : ViewModel() {
    var uiState = mutableStateOf(RecipeDetailState())
        private set

    private val client = OkHttpClient()

    fun load(id: Int) {
        // início: loading
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val req = Request.Builder()
            .url("https://dummyjson.com/recipes/$id")
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                viewModelScope.launch(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(isLoading = false, error = e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { r ->
                    val body = r.body?.string()
                    if (!r.isSuccessful || body.isNullOrEmpty()) {
                        viewModelScope.launch(Dispatchers.Main) {
                            uiState.value = uiState.value.copy(isLoading = false, error = "HTTP ${r.code}")
                        }
                        return
                    }
                    val obj = JSONObject(body)
                    val recipe = Recipe.fromJson(obj)

                    viewModelScope.launch(Dispatchers.Main) {
                        uiState.value = uiState.value.copy(isLoading = false, recipe = recipe)
                    }
                }
            }
        })
    }
}
