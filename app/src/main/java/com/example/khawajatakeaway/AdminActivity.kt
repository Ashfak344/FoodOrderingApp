package com.example.khawajatakeaway

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var viewAllOrdersButton: Button
    private lateinit var totalOrdersText: TextView
    private lateinit var pendingOrdersText: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        pieChart = findViewById(R.id.ordersPieChart)
        viewAllOrdersButton = findViewById(R.id.viewAllOrdersButton)
        totalOrdersText = findViewById(R.id.totalOrdersText)
        pendingOrdersText = findViewById(R.id.pendingOrdersText)

        viewAllOrdersButton.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
        val profileIcon = findViewById<ImageView>(R.id.imgProfileAdmin)
        profileIcon.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }


        loadDashboard()
    }

    private fun loadDashboard() {
        db.collection("orders").addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) return@addSnapshotListener

            var total = 0
            var pending = 0
            var accepted = 0
            var rejected = 0

            for (doc in snapshot.documents) {
                total++
                when (doc.getString("status")) {
                    "Pending" -> pending++
                    "Accepted" -> accepted++
                    "Rejected" -> rejected++
                }
            }

            totalOrdersText.text = total.toString()
            pendingOrdersText.text = pending.toString()

            val entries = listOf(
                PieEntry(pending.toFloat(), "Pending"),
                PieEntry(accepted.toFloat(), "Accepted"),
                PieEntry(rejected.toFloat(), "Rejected")
            )

            val dataSet = PieDataSet(entries, "Order Status")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextSize = 16f

            val pieData = PieData(dataSet)
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.centerText = "Orders"
            pieChart.animateY(1000)
            pieChart.invalidate()
        }
    }
}

