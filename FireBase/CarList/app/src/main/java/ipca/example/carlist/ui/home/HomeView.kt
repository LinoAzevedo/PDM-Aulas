package ipca.example.carlist.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ipca.example.carlist.models.Car
import ipca.example.carlist.ui.components.CarViewCell
import ipca.example.carlist.ui.theme.theme.CarListTheme

@Composable
fun HomeView(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState

    HomeViewContent(modifier, uiState, navController)

    LaunchedEffect(Unit) {
        viewModel.fetchCars()
    }
}

@Composable
fun HomeViewContent(
    modifier: Modifier = Modifier,
    uiState: HomeViewState,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp) // margens laterais suaves
    ) {

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "A carregar carros...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
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
                    Spacer(modifier = Modifier.height(8.dp))
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopBar(
                        modifier = Modifier
                            .fillMaxWidth(),
                        navController = navController
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.cars
                        ) { _, car ->
                            CarViewCell(car) {
                                navController.navigate("car/${car.idCar}")
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
fun HomeViewSuccessPreview() {
    val mockCars = listOf(
        Car(
            idCar = "1",
            brand = "Tesla",
            model = "Model 3",
            price = 45000.0,
            image = "dummy_url"
        ),
        Car(
            idCar = "2",
            brand = "Ford",
            model = "Mustang",
            price = 55000.0,
            image = "dummy_url"
        ),
        Car(
            idCar = "3",
            brand = "Porsche",
            model = "911",
            price = 120000.0,
            image = "dummy_url"
        )
    )

    val successState = HomeViewState(
        isLoading = false,
        error = null,
        cars = mockCars
    )

    CarListTheme {
        HomeViewContent(
            uiState = successState,
            navController = rememberNavController()
        )
    }
}
