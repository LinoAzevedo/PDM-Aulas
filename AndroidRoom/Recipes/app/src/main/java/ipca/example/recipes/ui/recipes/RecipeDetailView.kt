package ipca.example.recipes.ui.recipes

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage                             // usa coil3 (um só import)
import ipca.example.recipes.models.Recipe
import androidx.compose.ui.res.stringResource
import ipca.example.recipes.R

// ícones para as estrelas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.unit.Dp

@Composable
fun RecipeDetailView(
    id: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: RecipeDetailViewModel = viewModel()
) {
    LaunchedEffect(id) { vm.load(id) }
    val state by vm.uiState

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.error_prefix, state.error!!))
                Spacer(Modifier.height(12.dp))
                Button(onClick = { vm.load(id) }) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
        state.recipe != null -> RecipeDetailContent(
            recipe = state.recipe!!,
            modifier = modifier
        )
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Título
        Text(
            recipe.name ?: "Sem nome",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(10.dp))

        // Imagem
        if (!recipe.image.isNullOrBlank()) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
        }

        // ⭐ Avaliação (esq)  |  N.º reviews (dir)
        RatingRow(rating = recipe.rating, reviews = recipe.reviewCount)
        Spacer(Modifier.height(8.dp))

        // Dificuldade (esq) | Culinária (dir)
        InfoRow(
            leftLabel = "Dificuldade", leftValue = recipe.difficulty,
            rightLabel = "Culinária",  rightValue = recipe.cuisine
        )

        // Doses (esq) | Calorias/porção (dir)
        InfoRow(
            leftLabel = "Doses", leftValue = recipe.servings?.toString(),
            rightLabel = "Calorias/porção", rightValue = recipe.caloriesPerServing?.toString()
        )

        // Tempo (esq) | Refeição (dir)
        val tempo = listOfNotNull(
            recipe.prepTimeMinutes?.let { "prep ${it}m" },
            recipe.cookTimeMinutes?.let { "coz ${it}m" }
        ).joinToString(" • ")
        InfoRow(
            leftLabel = "Tempo", leftValue = tempo.takeIf { it.isNotBlank() },
            rightLabel = "Refeição", rightValue = recipe.mealType?.joinToString(", ")
        )

        // ID (linha simples à esquerda)
        KeyValue("ID", recipe.id?.toString())
        // Tags (linha simples à esquerda)
        KeyValue("Tags", recipe.tags?.joinToString(", "))

        // Ingredientes
        if (!recipe.ingredients.isNullOrEmpty()) {
            Spacer(Modifier.height(12.dp))
            SectionTitle("Ingredientes")
            Spacer(Modifier.height(6.dp))
            recipe.ingredients!!.forEach { Text("• $it") }
        }

        // Instruções
        if (!recipe.instructions.isNullOrEmpty()) {
            Spacer(Modifier.height(12.dp))
            SectionTitle("Instruções")
            Spacer(Modifier.height(6.dp))
            recipe.instructions!!.forEachIndexed { i, step ->
                Text("${i + 1}. $step")
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

/* ---------- Helpers ---------- */

@Composable
private fun RatingRow(rating: Double?, reviews: Int?) {
    if (rating == null) return
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RatingStars(rating = rating)
            Spacer(Modifier.width(6.dp))
            Text(" %.1f".format(rating), fontWeight = FontWeight.SemiBold)
        }
        if (reviews != null) {
            Text("$reviews reviews", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun RatingStars(rating: Double, size: Dp = 18.dp, max: Int = 5) {
    val full = rating.toInt().coerceIn(0, max)
    val frac = (rating - full).coerceIn(0.0, 1.0)
    val half = if (frac >= 0.25 && frac < 0.75) 1 else 0
    val empty = (max - full - half).coerceAtLeast(0)

    repeat(full)  { Icon(Icons.Filled.Star,      contentDescription = null, modifier = Modifier.size(size)) }
    repeat(half)  { Icon(Icons.Filled.StarHalf,  contentDescription = null, modifier = Modifier.size(size)) }
    repeat(empty) { Icon(Icons.Outlined.Star,    contentDescription = null, modifier = Modifier.size(size)) }
}

@Composable
private fun InfoRow(
    leftLabel: String, leftValue: String?,
    rightLabel: String, rightValue: String?
) {
    if (leftValue.isNullOrBlank() && rightValue.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        KeyValue(leftLabel, leftValue, Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        KeyValue(rightLabel, rightValue, Modifier.weight(1f), TextAlign.End)
    }
}

@Composable
private fun KeyValue(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start
) {
    if (value.isNullOrBlank()) return
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = align)
        Text(value, style = MaterialTheme.typography.bodyMedium, textAlign = align)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
}
