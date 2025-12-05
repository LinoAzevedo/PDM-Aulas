package ipca.example.recipes.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder

@Entity
data class Recipe(
    @PrimaryKey
    var id: Int?,
    var name: String?,
    var ingredients: List<String>? = null,
    var instructions: List<String>? = null,
    var prepTimeMinutes: Int? = null,
    var cookTimeMinutes: Int? = null,
    var servings: Int? = null,
    var difficulty: String? = null,
    var cuisine: String? = null,
    var caloriesPerServing: Int? = null,
    var tags: List<String>? = null,
    var userId: Int? = null,
    var image: String? = null,
    var rating: Double? = null,
    var reviewCount: Int? = null,
    var mealType: List<String>? = null,
){


companion object {
        fun fromJson(json: JSONObject): Recipe {

            fun optIntOrNull(key: String): Int? =
                if (json.has(key) && !json.isNull(key)) json.getInt(key) else null

            fun optDoubleOrNull(key: String): Double? =
                if (json.has(key) && !json.isNull(key)) json.getDouble(key) else null

            fun optStringOrNull(key: String): String? =
                json.optString(key, null)?.takeIf { it.isNotBlank() }

            fun optStringListOrNull(key: String): List<String>? =
                json.optJSONArray(key)?.let { arr ->
                    List(arr.length()) { i -> arr.optString(i) }
                }

            return Recipe(
                id = optIntOrNull("id"),
                name = optStringOrNull("name"),
                ingredients = optStringListOrNull("ingredients"),
                instructions = optStringListOrNull("instructions"),
                prepTimeMinutes = optIntOrNull("prepTimeMinutes"),
                cookTimeMinutes = optIntOrNull("cookTimeMinutes"),
                servings = optIntOrNull("servings"),
                difficulty = optStringOrNull("difficulty"),
                cuisine = optStringOrNull("cuisine"),
                caloriesPerServing = optIntOrNull("caloriesPerServing"),
                tags = optStringListOrNull("tags"),
                userId = optIntOrNull("userId"),
                image = optStringOrNull("image") ?: optStringOrNull("imageURL"),
                rating = optDoubleOrNull("rating"),
                reviewCount = optIntOrNull("reviewCount"),
                mealType = optStringListOrNull("mealType")
            )
        }
    }
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE id=:id")
    fun loadById(id: IntArray): List<Recipe>

    @Insert
    fun insert(recipe: Recipe)

    @Delete
    fun delete(recipe: Recipe)
}


fun String.encodeUrl() : String {
    return URLEncoder.encode(this, "UTF-8")
}

fun String.decodeUrl() : String {
    return URLDecoder.decode(this, "UTF-8")
}
