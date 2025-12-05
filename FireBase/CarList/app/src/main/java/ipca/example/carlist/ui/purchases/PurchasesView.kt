package ipca.example.carlist.ui.purchases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ipca.example.carlist.models.Purchase
import ipca.example.carlist.ui.components.TopBackBar
import ipca.example.carlist.ui.theme.theme.CarListTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PurchasesView(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: PurchasesViewModel = viewModel()
    val state by viewModel.uiState

    PurchasesViewContent(
        modifier = modifier,
        navController = navController,
        state = state
    )
}

@Composable
fun PurchasesViewContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    state: PurchasesState
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopBackBar(title = "Minhas Compras", navController = navController)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.purchases.isEmpty() -> {
                    Text(
                        text = "Ainda não efetuou nenhuma compra.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.purchases) { purchase ->
                            PurchaseRow(purchase = purchase)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseRow(purchase: Purchase) {
    val sdf = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    val dateText = purchase.createdAt?.toDate()?.let { sdf.format(it) } ?: "-"

    val totalItems = purchase.items.sumOf { it.quantity }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Compra #${purchase.id ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Data: $dateText",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Artigos: $totalItems",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: € ${String.format("%.2f", purchase.total)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchasesViewPreview() {
    CarListTheme {
        PurchasesView(
            navController = rememberNavController()
        )
    }
}
