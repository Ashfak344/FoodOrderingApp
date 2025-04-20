package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var trendingAdapter: TrendingAdapter
    private lateinit var database: DatabaseReference

    private lateinit var fabCart : FloatingActionButton

    private val categoryList = mutableListOf<MenuCategory>()
    private val trendingList = mutableListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnViewMenu: MaterialButton = findViewById(R.id.btnViewMenu)

        btnViewMenu.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)
        }

        recyclerViewCategories = findViewById(R.id.recyclerViewCategory)
        recyclerViewTrending = findViewById(R.id.recyclerViewTrending)

        fabCart = findViewById(R.id.fabCart)

        // Scalling-up and scalling-down animation for the FAB with Intent to CartActivity
        fabCart.setOnClickListener {
            // Scale-up animation
            ViewCompat.animate(fabCart)
                .scaleX(1.2f)      // Scalling the button to 120% of its original size
                .scaleY(1.2f)     // Scalling the button to 120% of its original size
                .setDuration(150)      // Duration of the scale-up animation
                .withEndAction {
                    // Scale back to original size after scale-up
                    ViewCompat.animate(fabCart)
                        .scaleX(1f) // Scalling back to original size (100%)
                        .scaleY(1f) // Scalling back to original size (100%)
                        .setDuration(150) // Duration of the scale-down animation
                        .withEndAction {
                            // Now starting the Intent after animation completes
                            val intent = Intent(this@HomeActivity, CartActivity::class.java)
                            startActivity(intent) // Start the CartActivity
                        }
                        .start() // Starting the scale-down animation
                }
                .start() // Starting the scale-up animation
        }



        // Setting Item Animators with Fade-In Effect
        recyclerViewCategories.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                holder?.let {
                    // Fade in animation for each added item
                    ViewCompat.animate(it.itemView)
                        .alpha(0f)
                        .setDuration(500)
                        .alpha(1f)
                        .start()
                }
                return true
            }
        }

        recyclerViewTrending.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                holder?.let {
                    // Fade in animation for each added item
                    ViewCompat.animate(it.itemView)
                        .alpha(0f)
                        .setDuration(500)
                        .alpha(1f)
                        .start()
                }
                return true
            }
        }


        // Profile Click Listener
        findViewById<ImageView>(R.id.imgProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

//        // Floating Cart Button Click Listener
//        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCart).setOnClickListener {
//            startActivity(Intent(this, CartActivity::class.java))
//        }


        recyclerViewCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrending.layoutManager = GridLayoutManager(this, 2)

        //  Initializing Adapters BEFORE fetching data
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            val intent = Intent(this@HomeActivity, ItemListActivity::class.java)
            intent.putExtra("CATEGORY_KEY", category.key)
            startActivity(intent)
        }


        recyclerViewCategories.adapter = categoryAdapter

        trendingAdapter = TrendingAdapter(mutableListOf()) { item ->
            val intent = Intent(this, ItemDescription::class.java).apply {
                putExtra("ITEM_NAME", item.name)
                putExtra("ITEM_DESCRIPTION", item.des)
                putExtra("ITEM_PRICE", item.price) // Assuming price is a Double
                putExtra("ITEM_IMAGE_URL", item.imageRes) // Assuming imageUrl exists
            }
            startActivity(intent)
        }

        recyclerViewTrending.adapter = trendingAdapter

        database = FirebaseDatabase.getInstance().reference

        fetchCategories()
        fetchTrendingItems()
    }

    //  Fetching Categories Correctly in Firebase
    private fun fetchCategories() {
        database.child("menuCategory").get()
            .addOnSuccessListener { snapshot ->
                val tempList = mutableListOf<MenuCategory>()
                for (data in snapshot.children) {
                    val category = data.getValue(MenuCategory::class.java)
                    if (category != null) {
                        category.key = data.key.toString()
                        tempList.add(category)
                    }
                }

                if (tempList.isEmpty()) {
                    Toast.makeText(this, "No categories found!", Toast.LENGTH_SHORT).show()
                } else {
                    //  Instead of just updating the list, reinitialize adapter with click listener
                    categoryAdapter = CategoryAdapter(tempList) { category ->
                        val intent = Intent(this@HomeActivity, ItemListActivity::class.java)
                        intent.putExtra("CATEGORY_KEY", category.key)
                        startActivity(intent)
                    }
                    recyclerViewCategories.adapter = categoryAdapter
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    //  Fetching Trending Items from Firebase
    private fun fetchTrendingItems() {
        database.child("trendingItems").get()
            .addOnSuccessListener { snapshot ->
                val tempList = mutableListOf<FoodItem>()
                for (data in snapshot.children) {
                    val item = data.getValue(FoodItem::class.java)
                    if (item != null) {
                        tempList.add(item)
                    }
                }

                if (tempList.isEmpty()) {
                    Toast.makeText(this, "No trending items found!", Toast.LENGTH_SHORT).show()
                }

                trendingAdapter.updateList(tempList) // ✅ Updates the existing adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun openItemListActivity(category: MenuCategory){
        val intent = Intent(this, ItemListActivity::class.java)
        intent.putExtra("CATEGORY_KEY", category.key)
        startActivity(intent)
    }
}
