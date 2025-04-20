package com.example.khawajatakeaway

import Order
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AdminOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orderList = mutableListOf<Order>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)

        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(orderList, this) { order, action -> onOrderAction(order, action) }
        recyclerView.adapter = orderAdapter

        fetchOrders()
    }

    private fun fetchOrders() {
        db.collection("orders").get()
            .addOnSuccessListener { documents ->
                orderList.clear()
                for (document in documents) {
                    val order = Order(
                        orderId = document.getString("orderId") ?: "",
                        userId = document.getString("userId") ?: "",
                        name = document.getString("name") ?: "",
                        mobile = document.getString("mobile") ?: "",
                        address = document.getString("address") ?: "",
                        timestamp = document.getString("timestamp") ?: "",
                        status = document.getString("status") ?: "Pending",
                        items = (document.get("items") as? List<Map<String, Any>>)?.map { item ->
                            OrderItem(
                                id = item["id"] as String,
                                name = item["name"] as String,
                                price = item["price"] as Double,
                                quantity = (item["quantity"] as Long).toInt()
                            )
                        } ?: listOf()
                    )
                    orderList.add(order)
                }
                orderAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching orders: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun onOrderAction(order: Order, action: String) {
        val orderRef: DocumentReference = db.collection("orders").document(order.orderId)

        // Calculate the delivery time (1 hour from now)
        val deliveryTime = System.currentTimeMillis() + 3600000 // 1 hour in milliseconds
        val deliveryTimeDate = java.util.Date(deliveryTime)

        val updatedStatus = when (action) {
            "Accept" -> "Accepted"
            "Reject" -> "Rejected"
            else -> order.status
        }

        orderRef.update("status", updatedStatus)
            .addOnSuccessListener {
                // After updating status, refresh the order list
                fetchOrders()
                Toast.makeText(this, "Order $updatedStatus", Toast.LENGTH_SHORT).show()
                // Generate the PDF bill after accepting the order
                generateBill(order)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to update order status: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun generateBill(order: Order) {
        try {
            // Create the PDF document
            val pdfDocument = PdfDocument()

            // Generate the bill content (same as before)

            // Get the internal storage directory for saving the PDF
            val fileName = "Order_${order.orderId}_Bill.pdf"
            val filePath = File(filesDir, fileName)

            // Create an output stream to write the PDF to the app's internal storage
            val outputStream = FileOutputStream(filePath)

            // Write the document to the output stream
            pdfDocument.writeTo(outputStream)

            // Close the document and the output stream
            pdfDocument.close()
            outputStream.close()

            // Show success message
            Toast.makeText(this, "Bill generated: $filePath", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating bill: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}