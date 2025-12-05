package ipca.example.carlist.models

import com.google.firebase.firestore.DocumentId

data class Car(
    @DocumentId
    val idCar: String? = null,
    var brand: String? = null,
    var model: String? = null,
    var price: Double? = null,
    var image: String? = null
)