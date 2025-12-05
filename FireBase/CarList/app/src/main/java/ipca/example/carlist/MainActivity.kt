package ipca.example.carlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import ipca.example.carlist.ui.Car.CarView
import ipca.example.carlist.ui.auth.LoginView
import ipca.example.carlist.ui.auth.RegisterView
import ipca.example.carlist.ui.cart.CartView
import ipca.example.carlist.ui.home.HomeView
import ipca.example.carlist.ui.profile.ProfileView
import ipca.example.carlist.ui.purchases.PurchasesView
import ipca.example.carlist.ui.theme.theme.CarListTheme

const val TAG = "CarList"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            CarListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable("login"){
                            LoginView(
                                navController = navController
                            )
                        }

                        composable("home") {
                            HomeView(navController = navController)
                        }

                        composable("cart"){
                            CartView( navController = navController )
                        }

                        composable("profile") {
                            ProfileView(navController = navController)
                        }

                        composable("register") {
                            RegisterView(navController = navController)
                        }

                        composable("purchases") {
                            PurchasesView(navController = navController)
                        }

                        composable("car/{idCar}") { backStackEntry ->
                            val idCar = backStackEntry.arguments?.getString("idCar") ?: ""
                            CarView(navController = navController, idCar = idCar)
                        }
                    }

//                    LaunchedEffect(Unit) {
//                       val userId = Firebase.auth.currentUser?.uid
//                          if (userId != null) {
//                            navController.navigate("home")
//                        }
//                    }

                }
            }
        }
    }
}

