package ipca.example.carlist.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import ipca.example.carlist.models.Car
import ipca.example.carlist.models.Cart

data class HomeViewState(
    var cars: List<Car> = emptyList(),
    var error: String? = null,
    var isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

    var uiState = mutableStateOf(HomeViewState())
        private set

    val db = Firebase.firestore

    fun fetchCars() {
        uiState.value = uiState.value.copy(isLoading = true)

        val docRef = db.collection("cars")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
                return@addSnapshotListener
            }

            val fetchedCars = mutableListOf<Car>()
            for (doc in snapshot?.documents ?: emptyList()) {
                val car = doc.toObject(Car::class.java)
                car?.let {
                    fetchedCars.add(it)
                }
            }

            uiState.value = uiState.value.copy(
                cars = fetchedCars,
                isLoading = false,
                error = null
            )

        }
    }

//    fun createCart() {
//        uiState.value = uiState.value.copy(loading = true)
//
//        val userID = Firebase.auth.currentUser?.uid
//
//        val user = Cart(
//            name = "New cart ${uiState.value.carts.size + 1}",
//            owners = listOf<String>(userID!!),
//        )
//
//        db.collection("carts")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }
//
//    }

}