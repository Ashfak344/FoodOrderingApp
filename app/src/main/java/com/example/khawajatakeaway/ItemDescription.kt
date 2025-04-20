package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ItemDescription : AppCompatActivity() {

    private lateinit var itemDetailName: TextView
    private lateinit var itemDetailImage: ImageView
    private lateinit var itemDetailDescription: TextView
    private lateinit var itemDetailPrice: TextView
    private lateinit var btnViewMenu: Button
    private lateinit var database: DatabaseReference
    private lateinit var cartDatabase: DatabaseReference

    private var itemId: String = ""
    private var itemName: String = ""
    private var itemPrice: Double = 0.0
    private var itemImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_description)

        // Initialize the views
        itemDetailName = findViewById(R.id.itemDetailName)
        itemDetailImage = findViewById(R.id.itemDetailImage)
        itemDetailDescription = findViewById(R.id.itemDetailDescription)
        itemDetailPrice = findViewById(R.id.itemDetailPrice)
        btnViewMenu = findViewById(R.id.btnViewMenu)


        // Get Firebase user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please log in to add items to cart", Toast.LENGTH_SHORT).show()
            finish()
            return
        }



        // Firebase references
       // database = FirebaseDatabase.getInstance().getReference("menuCategory/$categoryKey/items")
        cartDatabase = FirebaseDatabase.getInstance().getReference("cart").child(userId)

        btnViewMenu.setOnClickListener{
            val intent = Intent(this, MenuActivity::class.java)

            startActivity(intent)
        }


        // Get the passed FoodItem details
        val itemName = intent.getStringExtra("ITEM_NAME")
        val itemDescription = intent.getStringExtra("ITEM_DESCRIPTION")
        val itemPrice = intent.getDoubleExtra("ITEM_PRICE", 0.0)
        val itemImageUrl = intent.getStringExtra("ITEM_IMAGE_URL")

        // Populate the views
        itemDetailName.text = itemName
        itemDetailDescription.text = itemDescription
        itemDetailPrice.text = "$${"%.2f".format(itemPrice)}"


        // Load the image using Glide (add the Glide dependency in build.gradle if not already added)
        Glide.with(this)
            .load(itemImageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
            .error(R.drawable.ic_launcher_foreground) // Optional error image
            .into(itemDetailImage)


    }


}
