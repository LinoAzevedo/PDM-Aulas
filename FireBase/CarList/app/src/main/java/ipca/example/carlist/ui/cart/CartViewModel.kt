package ipca.example.carlist.ui.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import ipca.example.carlist.models.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class CartViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState = mutableStateOf(CartState())
        private set

    private var listener: ListenerRegistration? = null

    init {
        observeCart()
    }

    // ----------------- LISTEN AO CARRINHO -----------------

    private fun observeCart() {
        val uid = auth.currentUser?.uid ?: run {
            uiState.value = CartState(
                items = emptyList(),
                isLoading = false,
                error = "Utilizador não autenticado"
            )
            return
        }

        listener?.remove()

        listener = db.collection("users")
            .document(uid)
            .collection("cart")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.map { doc ->
                    CartItem(
                        id = doc.id,
                        carId = doc.getString("carId"),
                        brand = doc.getString("brand"),
                        model = doc.getString("model"),
                        price = doc.getDouble("price") ?: 0.0,
                        imageUrl = doc.getString("imageUrl"),
                        quantity = (doc.getLong("quantity") ?: 1L).toInt(),
                        userId = doc.getString("userId")
                    )
                } ?: emptyList()

                uiState.value = uiState.value.copy(
                    items = list,
                    isLoading = false,
                    error = null
                )
            }
    }

    // ----------------- CRUD DO CARRINHO -----------------

    fun addToCart(
        carId: String,
        brand: String,
        model: String,
        price: Double,
        imageUrl: String?
    ) {
        val uid = auth.currentUser?.uid ?: return

        val itemRef = db.collection("users")
            .document(uid)
            .collection("cart")
            .document(carId)   // 1 doc por carro

        db.runTransaction { tx ->
            val snap = tx.get(itemRef)
            if (snap.exists()) {
                val currentQty = (snap.getLong("quantity") ?: 1L).toInt()
                tx.update(itemRef, "quantity", currentQty + 1)
            } else {
                val item = CartItem(
                    id = carId,
                    carId = carId,
                    brand = brand,
                    model = model,
                    price = price,
                    imageUrl = imageUrl,
                    quantity = 1,
                    userId = uid
                )
                tx.set(itemRef, item)
            }
        }.addOnFailureListener { e ->
            uiState.value = uiState.value.copy(error = e.localizedMessage)
        }
    }

    fun increaseQuantity(itemId: String) {
        val uid = auth.currentUser?.uid ?: return

        val itemRef = db.collection("users")
            .document(uid)
            .collection("cart")
            .document(itemId)

        db.runTransaction { tx ->
            val snap = tx.get(itemRef)
            if (!snap.exists()) return@runTransaction

            val currentQty = (snap.getLong("quantity") ?: 1L).toInt()
            tx.update(itemRef, "quantity", currentQty + 1)
        }.addOnFailureListener { e ->
            uiState.value = uiState.value.copy(error = e.localizedMessage)
        }
    }

    fun decreaseQuantity(itemId: String) {
        val uid = auth.currentUser?.uid ?: return

        val itemRef = db.collection("users")
            .document(uid)
            .collection("cart")
            .document(itemId)

        db.runTransaction { tx ->
            val snap = tx.get(itemRef)
            if (!snap.exists()) return@runTransaction

            val currentQty = (snap.getLong("quantity") ?: 1L).toInt()
            if (currentQty <= 1) {
                tx.delete(itemRef)
            } else {
                tx.update(itemRef, "quantity", currentQty - 1)
            }
        }.addOnFailureListener { e ->
            uiState.value = uiState.value.copy(error = e.localizedMessage)
        }
    }

    fun removeItem(itemId: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("cart")
            .document(itemId)
            .delete()
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(error = e.localizedMessage)
            }
    }

    // ----------------- TOTAL & FINALIZAR -----------------

    fun total(): Double =
        uiState.value.items.sumOf { it.price * it.quantity }

    /**
     * Finaliza a compra:
     *  - Cria um documento em users/{uid}/orders
     *  - Apaga os itens de users/{uid}/cart
     */
    fun finalizeOrder() {
        val uid = auth.currentUser?.uid ?: return
        val currentItems = uiState.value.items
        if (currentItems.isEmpty()) return

        val userDoc = db.collection("users").document(uid)

        val orderData = mapOf(
            "userId" to uid,
            "items" to currentItems,
            "total" to total(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        userDoc.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                // limpar carrinho depois de criar a encomenda
                val batch = db.batch()
                currentItems.forEach { item ->
                    item.id?.let { id ->
                        val ref = userDoc.collection("cart").document(id)
                        batch.delete(ref)
                    }
                }
                batch.commit()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(error = e.localizedMessage)
            }
    }

    // ----------------- LIMPEZA -----------------

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}

