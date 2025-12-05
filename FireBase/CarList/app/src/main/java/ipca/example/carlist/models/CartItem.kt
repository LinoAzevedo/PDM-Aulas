package ipca.example.carlist.models

data class CartItem(
    val id: String? = null,         // igual ao carId (id do doc em cart)
    val carId: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val quantity: Int = 1,
    val userId: String? = null
)