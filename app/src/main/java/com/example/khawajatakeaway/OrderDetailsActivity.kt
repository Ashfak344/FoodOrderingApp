package com.example.khawajatakeaway

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var orderIdText: TextView
    private lateinit var nameText: TextView
    private lateinit var mobileText: TextView
    private lateinit var addressText: TextView
    private lateinit var timestampText: TextView
    private lateinit var statusText: TextView
    private lateinit var orderItemsRecyclerView: RecyclerView
    private lateinit var acceptButton: Button
    private lateinit var rejectButton: Button
    private lateinit var orderItemsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        // Bind the views
        orderIdText = findViewById(R.id.orderIdText)
        nameText = findViewById(R.id.nameText)
        mobileText = findViewById(R.id.mobileText)
        addressText = findViewById(R.id.addressText)
        timestampText = findViewById(R.id.timestampText)
        statusText = findViewById(R.id.statusText)
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        acceptButton = findViewById(R.id.acceptButton)
        rejectButton = findViewById(R.id.rejectButton)

        // Retrieve the data from the intent
        val orderId = intent.getStringExtra("orderId") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val mobile = intent.getStringExtra("mobile") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val timestamp = intent.getStringExtra("timestamp") ?: ""
        val status = intent.getStringExtra("status") ?: ""
        val items = intent.getParcelableArrayListExtra<OrderItem>("items") ?: ArrayList()

        // Set the order details in the UI
        orderIdText.text = "Order ID: $orderId"
        nameText.text = "Customer Name: $name"
        mobileText.text = "Mobile: $mobile"
        addressText.text = "Address: $address"
        timestampText.text = "Timestamp: $timestamp"
        statusText.text = "Status: $status"

        // Set RecyclerView for items (Use an adapter to display items)
        orderItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderItemsAdapter(items ?: emptyList()) // Adapter for order items
        orderItemsRecyclerView.adapter = adapter

        // Set action buttons (Accept/Reject)
        acceptButton.setOnClickListener {
            updateOrderStatus(orderId, "Accepted")
        }
        rejectButton.setOnClickListener {
            updateOrderStatus(orderId, "Rejected")
        }
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        // Here you can update the status in Firebase or wherever you store the orders
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document(orderId)

        orderRef.update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Order $status", Toast.LENGTH_SHORT).show()
                // Optionally, go back to the previous activity
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
