package ipca.example.carlist.ui.Car

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import ipca.example.carlist.models.Car
import kotlinx.coroutines.launch

data class CarDetailState(
    var car: Car = Car(),
    var error: String? = null,
    var isLoading: Boolean = false
)

class CarDetailViewModel : ViewModel() {
    var uiState = mutableStateOf(CarDetailState())
        private set

    val db = Firebase.firestore

    fun fetchCars(idCar: String) {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val docRef = db.collection("cars").document(idCar)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val car = snapshot.toObject(Car::class.java)
                uiState.value = uiState.value.copy(
                    car = car!!,
                    isLoading = false,
                    error = null
                )
            } else {
                Log.d(TAG, "Car not found")
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Car not found"
                )
            }
        }
    }

    fun addCar(car: Car, onResult: (Boolean) -> Unit) {
        // Opcional: Indicar que está a carregar
        uiState.value = uiState.value.copy(isLoading = true)

        db.collection("cars")
            .add(car) // .add cria um documento com ID automático
            .addOnSuccessListener { documentReference ->
                // Sucesso!
                Log.d("AddCar", "Carro adicionado com ID: ${documentReference.id}")

                // Atualiza o estado
                uiState.value = uiState.value.copy(isLoading = false)

                // Avisa a UI que correu tudo bem (true)
                onResult(true)
            }
            .addOnFailureListener { e ->
                // Erro
                Log.w("AddCar", "Erro ao adicionar", e)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
                onResult(false)
            }
    }

}