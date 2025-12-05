package ipca.example.carlist.models

import com.google.firebase.Timestamp

data class Purchase(
    val id: String? = null,
    val userId: String? = null,
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val createdAt: Timestamp? = null
)
