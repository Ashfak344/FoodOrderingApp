package com.example.khawajatakeaway

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemListAdapter(
    private var items: List<FoodItem>, // List is now non-mutable
    private val onAddClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {

    class ItemListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemListDishImage: ImageView = view.findViewById(R.id.dish_image)
        val itemListDishName: TextView = view.findViewById(R.id.dish_name)
        val itemListDishDes: TextView = view.findViewById(R.id.dish_des)
        val itemListDishPrice: TextView = view.findViewById(R.id.dish_price)
        val addButton: Button = view.findViewById(R.id.add_button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemlist, parent, false)
        return ItemListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val dishItem = items[position]

        holder.itemListDishName.text = dishItem.name
        holder.itemListDishDes.text = dishItem.des
        holder.itemListDishPrice.text = dishItem.price.toString()

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(dishItem.imageRes) // Firebase image URL
            .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
            .into(holder.itemListDishImage)

        // Log.d("ItemListAdapter", "Item: ${dishItem.name}, Image URL: ${dishItem.imageRes}")


        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, ItemDescription::class.java)
            intent.putExtra("ITEM_NAME", dishItem.name)
            intent.putExtra("ITEM_DESCRIPTION", dishItem.des)
            intent.putExtra("ITEM_PRICE", dishItem.price)
            intent.putExtra("ITEM_IMAGE_URL", dishItem.imageRes)
            context.startActivity(intent)

        }
        holder.addButton.setOnClickListener { onAddClick(dishItem) }
    }



    override fun getItemCount(): Int = items.size

    // Update the data dynamically when Firebase updates
    fun updateData(newItems: List<FoodItem>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class ItemDiffCallback(
        private val oldList: List<FoodItem>,
        private val newList: List<FoodItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].name == newList[newItemPosition].name
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

}
