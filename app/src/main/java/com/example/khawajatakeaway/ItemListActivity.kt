package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var cartDatabase: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemListAdapter
    private lateinit var viewCartButton: FloatingActionButton

    private val items = mutableListOf<FoodItem>()
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        // Retrieve category key from intent
        val categoryKey = intent.getStringExtra("CATEGORY_KEY") ?: ""
        if (categoryKey.isEmpty()) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Firebase references
        database = FirebaseDatabase.getInstance().getReference("menuCategory/$categoryKey/items")
        cartDatabase = FirebaseDatabase.getInstance().getReference("cart").child(userId)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerview_itemlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewCartButton = findViewById(R.id.fabCart)

        // Initialize adapter
        itemAdapter = ItemListAdapter(items) { foodItem ->
            addToCart(foodItem)
        }
        recyclerView.adapter = itemAdapter

        // Fetch items from Firebase
        fetchFoodItems()

        // View Cart button click event
        viewCartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun fetchFoodItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (data in snapshot.children) {
                    try {
                        val item = data.getValue(FoodItem::class.java)
                        if (item != null) {
                            item.id = data.key ?: ""
                            item.price = data.child("price").getValue(Double::class.java)?.toDouble() ?: 0.0
                            item.imageRes = data.child("imageRes").getValue(String::class.java) ?: ""
                            item.category = intent.getStringExtra("CATEGORY_KEY") ?: "" // 👈 set category here

                            items.add(item)
                        }
                    } catch (e: Exception) {
                        Log.e("ItemListActivity", "Error parsing item", e)
                    }
                }
                itemAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ItemListActivity", "Failed to fetch items", error.toException())
                Toast.makeText(this@ItemListActivity, "Failed to load items", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun addToCart(foodItem: FoodItem) {
        // Generating a unique ID combining the food item's ID and its category (or use UUID)
        val uniqueId = "${foodItem.id}_${foodItem.category}"

        val cartRef = cartDatabase.child(uniqueId) // Using the unique ID as the key

        cartRef.get().addOnSuccessListener { snapshot ->
            val existingItem = snapshot.getValue(CartItem::class.java)

            if (existingItem != null) {
                // If the item already exists, update the quantity
                val updatedQuantity = existingItem.quantity + 1
                cartRef.child("quantity").setValue(updatedQuantity)
                    .addOnSuccessListener {
                        Toast.makeText(this, "${foodItem.name} quantity updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update cart", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // If the item doesn't exist, add it as a new entry
                val cartItem = CartItem(
                    id = foodItem.id,
                    name = foodItem.name,
                    price = foodItem.price,
                    imageRes = foodItem.imageRes,
                    quantity = 1,
                    category = foodItem.category // Make sure to include the category if you're using that
                )

                // Set the cart item in Firebase under the unique ID
                cartRef.setValue(cartItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "${cartItem.name} added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error checking cart", Toast.LENGTH_SHORT).show()
        }
    }



}



