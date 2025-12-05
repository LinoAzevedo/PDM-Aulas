package ipca.example.recipes.models

import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder

data class Recipe(
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
                // 👇 dummyjson usa "image"; se n houver, tenta "imageURL"
                image = optStringOrNull("image") ?: optStringOrNull("imageURL"),
                rating = optDoubleOrNull("rating"),
                reviewCount = optIntOrNull("reviewCount"),
                mealType = optStringListOrNull("mealType")
            )
        }
    }
}



fun String.encodeUrl() : String {
    return URLEncoder.encode(this, "UTF-8")
}

fun String.decodeUrl() : String {
    return URLDecoder.decode(this, "UTF-8")
}
