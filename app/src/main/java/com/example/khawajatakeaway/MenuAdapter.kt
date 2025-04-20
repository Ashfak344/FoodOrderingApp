package com.example.khawajatakeaway

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide



class MenuAdapter (
    private val menuCategories: List<MenuCategory>,
    private val onCategoryClick: (MenuCategory) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>(){

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val categoryImage: ImageView = view.findViewById(R.id.categoryImageView)
        val categoryName: TextView = view.findViewById(R.id.categoryNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent,false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {



        val category = menuCategories[position]

        // Loading the image with Glide
        Glide.with(holder.itemView.context)
            .load(category.imageRes) // Using the Url from 'category.imageRes'.
            .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
            .into(holder.categoryImage)

        // Set the category name
        holder.categoryName.text = category.name

        // For handling click on the category
        holder.itemView.setOnClickListener{onCategoryClick(category)}
    }

    override fun getItemCount(): Int = menuCategories.size
}