package com.example.khawajatakeaway

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryAdapter(
    private var categoryList: MutableList<MenuCategory>,
    private val onItemClick: (MenuCategory) -> Unit // Click listener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
        Glide.with(holder.itemView.context)
            .load(category.imageRes) // Ensure FoodItem has an `image` property (URL or resource)
            .into(holder.categoryImage)

        // Click event
        holder.itemView.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount(): Int = categoryList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
    }

    // 🔥 Update list dynamically
    fun updateList(newList: List<MenuCategory>) {
        categoryList.clear()
        categoryList.addAll(newList)
        notifyDataSetChanged()
    }
}
