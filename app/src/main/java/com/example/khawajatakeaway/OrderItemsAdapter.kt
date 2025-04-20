package com.example.khawajatakeaway

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemsAdapter(private val items: List<OrderItem>) : RecyclerView.Adapter<OrderItemsAdapter.OrderItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_details, parent, false)
        return OrderItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemsViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = "Price: $${item.price}"
        holder.itemQuantity.text = "Quantity: ${item.quantity}"
    }

    override fun getItemCount(): Int = items.size

    class OrderItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
    }
}
