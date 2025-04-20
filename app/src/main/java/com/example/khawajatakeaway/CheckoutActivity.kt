package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CheckoutActivity : AppCompatActivity() {

    private lateinit var textViewOrderSummary: TextView
    private lateinit var textViewCustomerInfo: TextView
    private lateinit var buttonPlaceOrder: Button
    private lateinit var textViewCartSummary: TextView

    private lateinit var database: DatabaseReference
    private lateinit var cartAdapter: CartAdapter


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private val cartList = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        textViewOrderSummary = findViewById(R.id.textViewOrderSummary)
        textViewCustomerInfo = findViewById(R.id.textViewCustomerInfo)
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder)
        textViewCartSummary = findViewById(R.id.textViewCartSummary)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = auth.currentUser?.uid ?: ""

        database = FirebaseDatabase.getInstance().reference



        val cartSummaryText = StringBuilder()
        for (item in cartList) {
            cartSummaryText.append("• ${item.name} - \$${item.price} x ${item.quantity}\n")
        }
        textViewCartSummary.text = cartSummaryText.toString().trim()



        fetchCustomerInfo()
        fetchCartItems()

        buttonPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun fetchCustomerInfo() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "N/A"
                    val mobile = document.getString("mobile") ?: "N/A"
                    val address = document.getString("address") ?: "N/A"

                    textViewCustomerInfo.text = "Name: $name\nMobile: $mobile\nAddress: $address"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching customer info: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCartItems() {
        userId?.let { uid ->
            val cartRef = database.child("cart").child(uid)

            cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartList.clear()

                    if (!snapshot.exists()) {
                        Log.d("CheckoutActivity", "No cart data found.")
                        return
                    }

                    for (data in snapshot.children) {
                        try {
                            val cartItem = CartItem(
                                id = data.child("id").getValue(String::class.java) ?: "",
                                name = data.child("name").getValue(String::class.java) ?: "",
                                price = data.child("price").getValue(Double::class.java) ?: 0.0,
                                quantity = data.child("quantity").getValue(Long::class.java)?.toInt() ?: 0,
                                imageRes = data.child("imageUrl").getValue(String::class.java) ?: "drawable/ic_launcher_background.xml"
                            )

                            if (cartItem.id.isNotEmpty()) {
                                cartList.add(cartItem)
                            }
                        } catch (e: Exception) {
                            Log.e("CheckoutActivity", "Error parsing cart item", e)
                        }
                    }

                    if (cartList.isEmpty()) {
                        Log.d("CheckoutActivity", "Cart is empty!")
                    } else {
                        Log.d("CheckoutActivity", "Fetched ${cartList.size} items from Firebase.")
                        updateCheckoutUI()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CheckoutActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun placeOrder() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = UUID.randomUUID().toString()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "N/A"
                    val mobile = document.getString("mobile") ?: "N/A"
                    val address = document.getString("address") ?: "N/A"

                    val orderData = hashMapOf(
                        "orderId" to orderId,
                        "userId" to userId,
                        "name" to name,
                        "mobile" to mobile,
                        "address" to address,
                        "timestamp" to timestamp,
                        "status" to "Pending",
                        "items" to cartList.map { item ->
                            mapOf(
                                "id" to item.id,
                                "name" to item.name,
                                "price" to item.price,
                                "quantity" to item.quantity
                            )
                        }
                    )

                    db.collection("orders").document(orderId)
                        .set(orderData)
                        .addOnSuccessListener {
                            clearCart()
                            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, SuccessSplash::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun clearCart() {
        db.collection("cart").document(userId).collection("items").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

    private fun updateCheckoutUI() {
        var totalPrice = 0.0
        val orderSummary = StringBuilder()

        for (cartItem in cartList) {
            totalPrice += cartItem.price * cartItem.quantity
            orderSummary.append("${cartItem.name} x${cartItem.quantity} - $${cartItem.price * cartItem.quantity}\n")
        }

        // Append total price at the end
        orderSummary.append("\nTotal: $${"%.2f".format(totalPrice)}")

        // Update a TextView in CheckoutActivity to show the order summary
        textViewOrderSummary.text = orderSummary.toString()

        // Notify the RecyclerView adapter (if applicable)
        //cartAdapter.notifyDataSetChanged()
    }


}
