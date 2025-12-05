package ipca.example.carlist.ui.purchases

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import ipca.example.carlist.models.Purchase

data class PurchasesState(
    val purchases: List<Purchase> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class PurchasesViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState = mutableStateOf(PurchasesState())
        private set

    private var listener: ListenerRegistration? = null

    init {
        observePurchases()
    }

    private fun observePurchases() {
        val uid = auth.currentUser?.uid ?: run {
            uiState.value = PurchasesState(
                purchases = emptyList(),
                isLoading = false,
                error = "Utilizador não autenticado"
            )
            return
        }

        listener?.remove()

        listener = db.collection("users")
            .document(uid)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.map { doc ->
                    val base = doc.toObject(Purchase::class.java)
                    if (base != null) {
                        base.copy(id = doc.id)
                    } else {
                        Purchase(id = doc.id)
                    }
                } ?: emptyList()

                uiState.value = uiState.value.copy(
                    purchases = list,
                    isLoading = false,
                    error = null
                )
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
