package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var database: DatabaseReference
    private val cartList = mutableListOf<CartItem>()
    private lateinit var btncheckhOut: Button
    private lateinit var cartDatabase: DatabaseReference

    // Firebase Authentication instance
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userId: String? get() = auth.currentUser?.uid // Get current user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        btncheckhOut = findViewById(R.id.btnCheckOut)

        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(cartList, this::removeCartItem)
        recyclerViewCart.adapter = cartAdapter

        database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        cartDatabase = FirebaseDatabase.getInstance().getReference("cart").child(userId)



        fetchCartItems()

        btncheckhOut.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, CheckoutActivity::class.java))
            }
        }
    }

    private fun fetchCartItems() {
        userId?.let { uid ->
            val cartRef = database.child("cart").child(uid)

            cartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartList.clear()
                    for (data in snapshot.children) {
                        try {
                            // Use the `getValue` method safely to ensure the data is properly deserialized
                            val cartItem = CartItem(
                                id = data.child("id").getValue(String::class.java) ?: "",
                                name = data.child("name").getValue(String::class.java) ?: "",
                                price = data.child("price").getValue(Double::class.java) ?: 0.0,
                                quantity = data.child("quantity").getValue(Int::class.java) ?: 0,
                                imageRes = data.child("imageUrl").getValue(String::class.java) ?: "drawable/ic_launcher_background.xml"
                            )


                            if (cartItem != null) {
                                // Ensure that the CartItem object is added to the list
                                cartList.add(cartItem)
                            } else {
                                Log.e("CartActivity", "Error deserializing CartItem: ${data.key}")
                            }
                        } catch (e: Exception) {
                            Log.e("CartActivity", "Error parsing cart item", e)
                        }
                    }
                    cartAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun removeCartItem(cartItem: CartItem) {
        userId?.let { uid ->
            val uniqueId = "${cartItem.id}_${cartItem.category}"
            val cartRef = cartDatabase.child(uniqueId) // ✅ Use cartDatabase here!

            cartRef.removeValue().addOnSuccessListener {
                cartList.remove(cartItem)
                cartAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
