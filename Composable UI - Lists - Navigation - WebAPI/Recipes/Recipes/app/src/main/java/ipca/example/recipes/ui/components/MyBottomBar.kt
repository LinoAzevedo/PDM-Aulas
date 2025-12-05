package ipca.example.recipes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MyBottomBar(
    navController: NavController
) {
    // Rota atual do NavController.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomAppBar {
        NavigationBarItem(
            selected = currentRoute == "Breakfast",
            icon = {
                Icon(
                    imageVector = Icons.Default.FreeBreakfast,
                    contentDescription = "Breakfast"
                )
            },
            label = { Text("Breakfast") },
            onClick = {
                navController.navigate("Breakfast") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            selected = currentRoute == "Lunch",
            icon = {
                Icon(
                    imageVector = Icons.Default.LunchDining,
                    contentDescription = "Lunch"
                )
            },
            label = { Text("Lunch") },
            onClick = {
                navController.navigate("Lunch") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            selected = currentRoute == "All",
            icon = {
                Icon(
                    Icons.Filled.Restaurant,
                    contentDescription = "All Recipes"
                )
            },
            label = { Text("All Recipes") },
            onClick = {
                navController.navigate("All") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            selected = currentRoute == "Dinner",
            icon = {
                Icon(
                    imageVector = Icons.Default.DinnerDining,
                    contentDescription = "Dinner"
                )
            },
            label = { Text("Dinner") },
            onClick = {
                navController.navigate("Dinner") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            selected = currentRoute == "Beverage",
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiFoodBeverage,
                    contentDescription = "Beverage"
                )
            },
            label = { Text("Beverage") },
            onClick = {
                navController.navigate("Beverage") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
