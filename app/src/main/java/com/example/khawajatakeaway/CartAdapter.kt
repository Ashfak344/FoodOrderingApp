package com.example.khawajatakeaway

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartList: MutableList<CartItem>,
    private val onRemoveClick: (CartItem) -> Unit = {}
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]

        Log.d("CartAdapter", "Image URL: ${cartItem.imageRes}")
        holder.cartName.text = cartItem.name
        holder.cartPrice.text = "$%.2f".format(cartItem.price)
        holder.cartQuantity.text = "Qty: ${cartItem.quantity}"
        // Glide image loading, only load if the image URL is not empty
        Glide.with(holder.itemView.context)
            .load(cartItem.imageRes.ifEmpty { null })
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.cartImage)





        holder.btnRemove.setOnClickListener {
            onRemoveClick(cartItem)
        }
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartName: TextView = itemView.findViewById(R.id.cartName)
        val cartPrice: TextView = itemView.findViewById(R.id.cartPrice)
        val cartQuantity: TextView = itemView.findViewById(R.id.cartQuantity)
        val cartImage: ImageView = itemView.findViewById(R.id.cartImage)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemove)
    }
}

