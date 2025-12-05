package ipca.example.carlist.models

data class Cart(
    var docID : String? = null,
    var name: String? = null,
    var owners: List<String>? = null,
)