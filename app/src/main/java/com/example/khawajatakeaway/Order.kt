import com.example.khawajatakeaway.OrderItem

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val name: String = "",
    val mobile: String = "",
    val address: String = "",
    val timestamp: String = "",
    var status: String = "Pending",  // Default value "Pending"
    val items: List<OrderItem> = listOf()  // A list of OrderItem objects
)
