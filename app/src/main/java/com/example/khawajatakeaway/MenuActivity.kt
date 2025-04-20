package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var menuAdapter: MenuAdapter
    private val menuCategories = mutableListOf<MenuCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Setting up RecyclerView
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initializing the adapter with an empty list
        menuAdapter = MenuAdapter(menuCategories) { category ->
            val intent = Intent(this@MenuActivity, ItemListActivity::class.java)
            intent.putExtra("CATEGORY_KEY", category.key)
            startActivity(intent)
        }

        // Attach the adapter to RecyclerView
        recyclerView.adapter = menuAdapter

        // Initializing Firebase reference
        database = FirebaseDatabase.getInstance().getReference("menuCategory")

        // Fetch data from Firebase
        fetchMenuCategories()

        // Floating Action Button
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCart2).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun fetchMenuCategories() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuCategories.clear() // Clearing the list to avoid duplicates
                for (data in snapshot.children) {
                    var category = data.getValue(MenuCategory::class.java)
                    if (category != null) {
                        category.key = data.key.toString()
                        menuCategories.add(category)
                    }
                }

                // Notifying the adapter that the data has changed
                menuAdapter.notifyDataSetChanged()

                // Setting adapter with a click listener
                menuAdapter = MenuAdapter(menuCategories){category->
                    openItemListActivity(category)
                }
                recyclerView.adapter = menuAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuActivity", "Failed to fetch menu categories", error.toException())
                Toast.makeText(this@MenuActivity, "Failed to fetch data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openItemListActivity(category: MenuCategory){
        val intent = Intent(this, ItemListActivity::class.java)
        intent.putExtra("CATEGORY_KEY", category.key)
        startActivity(intent)
    }
}
