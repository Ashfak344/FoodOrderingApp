package com.example.khawajatakeaway
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrendingAdapter(
    private var trendingList: MutableList<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit // Click listener for handling clicks
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val item = trendingList[position]
        holder.trendingName.text = item.name
        holder.trendingPrice.text = "$${item.price}" // Ensure FoodItem has a `price` property

        // Load Image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageRes) // Ensure FoodItem has an `image` property (URL or resource)
            .placeholder(R.drawable.ic_launcher_background) // Fallback image
            .into(holder.trendingImage)

        // Handle click event
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = trendingList.size

    class TrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trendingImage: ImageView = itemView.findViewById(R.id.trendingImage)
        val trendingName: TextView = itemView.findViewById(R.id.trendingName)
        val trendingPrice: TextView = itemView.findViewById(R.id.trendingPrice)
    }

    // 🔥 Efficiently update data instead of re-creating adapter
    fun updateList(newList: List<FoodItem>) {
        trendingList.clear()
        trendingList.addAll(newList)
        notifyDataSetChanged()
    }
}
