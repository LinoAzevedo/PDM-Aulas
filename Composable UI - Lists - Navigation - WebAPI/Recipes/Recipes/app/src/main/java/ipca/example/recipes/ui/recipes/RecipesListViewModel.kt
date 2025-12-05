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

data class RecipesListState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipesListViewModel : ViewModel() {
    var uiState = mutableStateOf(RecipesListState())
        private set

    private val client = OkHttpClient()

    fun fetchRecipes(mealType: String?) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val request = Request.Builder()
            .url("https://dummyjson.com/recipes")
            .build()

        client.newCall(request).enqueue(object : Callback {
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

                    val wanted = mealType?.trim()?.takeIf { it.isNotEmpty() }  // null -> sem filtro
                    val root = JSONObject(body)
                    val arr = root.getJSONArray("recipes")

                    val list = buildList<Recipe> {
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            if (wanted == null) {
                                add(Recipe.fromJson(obj))  // todas
                            } else {
                                val mtArray = obj.optJSONArray("mealType")
                                val matches = if (mtArray != null) {
                                    (0 until mtArray.length()).any { j ->
                                        mtArray.optString(j).equals(wanted, ignoreCase = true)
                                    }
                                } else {
                                    obj.optString("mealType", null)?.equals(wanted, ignoreCase = true) == true
                                }
                                if (matches) add(Recipe.fromJson(obj))
                            }
                        }
                    }

                    viewModelScope.launch(Dispatchers.Main) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            recipes = list,
                            error = if (list.isEmpty() && wanted != null) "Sem resultados para \"$wanted\"" else null
                        )
                    }
                }
            }
        })
    }

}
