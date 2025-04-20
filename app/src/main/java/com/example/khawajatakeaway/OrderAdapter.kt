package com.example.khawajatakeaway

import Order
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orderList: List<Order>,
    private val context: Context,
    private val onOrderAction: (Order, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.orderIdText.text = "Order ID: ${order.orderId}"
        holder.userNameText.text = "Name: ${order.name}"
        holder.userMobileText.text = "Mobile: ${order.mobile}"
        holder.userAddressText.text = "Address: ${order.address}"
        holder.orderTimestampText.text = "Timestamp: ${order.timestamp}"
        holder.orderStatusText.text = "Status: ${order.status}"

        val itemsText = StringBuilder()
        for (item in order.items) {
            itemsText.append("Item: ${item.name}\nPrice: \$${item.price}\nQuantity: ${item.quantity}\n\n")
        }
        holder.orderItemsText.text = itemsText.toString()

        // Open OrderDetailsActivity when the order is clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            intent.putExtra("name", order.name)
            intent.putExtra("mobile", order.mobile)
            intent.putExtra("address", order.address)
            intent.putExtra("timestamp", order.timestamp)
            intent.putExtra("status", order.status)
            // Pass the items as a Parcelable or serializable object
            intent.putParcelableArrayListExtra("items", ArrayList(order.items))
            context.startActivity(intent)
        }

        // Button logic based on status
        if (order.status == "Accepted") {
            holder.acceptButton.text = "Accepted"
            holder.acceptButton.isEnabled = false
            holder.rejectButton.visibility = View.GONE
        } else if (order.status == "Rejected") {
            holder.acceptButton.visibility = View.GONE
            holder.rejectButton.text = "Rejected"
            holder.rejectButton.isEnabled = false
        } else {
            holder.acceptButton.text = "Accept"
            holder.acceptButton.isEnabled = true
            holder.rejectButton.visibility = View.VISIBLE
            holder.rejectButton.text = "Reject"
            holder.rejectButton.isEnabled = true
        }

        holder.acceptButton.setOnClickListener {
            onOrderAction(order, "Accepted")
            order.status = "Accepted"
            notifyItemChanged(position)
        }

        holder.rejectButton.setOnClickListener {
            onOrderAction(order, "Rejected")
            order.status = "Rejected"
            notifyItemChanged(position)
        }


    }

    override fun getItemCount(): Int = orderList.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdText: TextView = itemView.findViewById(R.id.orderIdText)
        val userNameText: TextView = itemView.findViewById(R.id.userNameText)
        val userMobileText: TextView = itemView.findViewById(R.id.userMobileText)
        val userAddressText: TextView = itemView.findViewById(R.id.userAddressText)
        val orderTimestampText: TextView = itemView.findViewById(R.id.orderTimestampText)
        val orderStatusText: TextView = itemView.findViewById(R.id.orderStatusText)
        val orderItemsText: TextView = itemView.findViewById(R.id.orderItemsText)
        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        val rejectButton: Button = itemView.findViewById(R.id.rejectButton)
    }
}
