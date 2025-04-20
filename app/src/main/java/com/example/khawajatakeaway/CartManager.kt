package com.example.khawajatakeaway
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    // Function to save cart items
    fun saveCart(cartItems: List<CartItem>) {
        val jsonString = gson.toJson(cartItems)
        sharedPreferences.edit().putString("cart_items", jsonString).apply()
    }

    // Function to retrieve cart items
    fun getCart(): List<CartItem> {
        val jsonString = sharedPreferences.getString("cart_items", null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    // Function to clear the cart
    fun clearCart() {
        sharedPreferences.edit().remove("cart_items").apply()
    }
}
