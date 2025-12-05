package ipca.example.carlist.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ipca.example.carlist.models.CartItem
import ipca.example.carlist.ui.components.TopBackBar
import ipca.example.carlist.ui.theme.theme.CarListTheme
import ipca.example.carlist.ui.theme.theme.Green
import kotlinx.coroutines.launch

@Composable
fun CartView(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: CartViewModel = viewModel()
    val uiState by viewModel.uiState
    val coroutineScope = rememberCoroutineScope()

    CartViewContent(
        modifier = modifier,
        navController = navController,
        uiState = uiState,
        cartItems = uiState.items,
        totalCart = viewModel.total(),
        onRemoveItem = { item ->
            item.id?.let { id ->
                viewModel.removeItem(id)
            }
        },
        onFinalizeOrder = {
            coroutineScope.launch {
                viewModel.finalizeOrder()
            }
        },
        onQuantityChange = { item, change ->
            item.id?.let { id ->
                when (change) {
                    1 -> viewModel.increaseQuantity(id)
                    -1 -> viewModel.decreaseQuantity(id)
                }
            }
        }
    )
}

@Composable
fun CartViewContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    uiState: CartState,
    cartItems: List<CartItem>,
    onRemoveItem: (CartItem) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onFinalizeOrder: () -> Unit,
    totalCart: Double
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 0.dp) // espaço controlado pelo footer
    ) {
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "A carregar o carrinho...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ups, ocorreu um erro",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Top bar
                    TopBackBar(
                        title = "Detalhes do Carrinho",
                        navController = navController
                    )

                    if (cartItems.isEmpty()) {
                        // Estado vazio
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "O seu carrinho está vazio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Adicione carros para começar a sua compra.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Mesmo com vazio, não mostramos resumo nem botão
                    } else {
                        // Lista de items
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            items(cartItems) { item ->
                                CartItemRow(
                                    item = item,
                                    onRemoveItem = { onRemoveItem(item) },
                                    onQuantityChange = { change ->
                                        onQuantityChange(item, change)
                                    }
                                )
                            }
                        }

                        // Resumo + botão fixo em baixo
                        Surface(
                            tonalElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(bottom = 12.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Total do carrinho",
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "IVA incluído, se aplicável",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "€ ${String.format("%.2f", totalCart)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Button(
                                    onClick = onFinalizeOrder,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Green,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        "Finalizar compra",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartViewPreview() {
    CarListTheme {
        CartView(
            navController = rememberNavController()
        )
    }
}
