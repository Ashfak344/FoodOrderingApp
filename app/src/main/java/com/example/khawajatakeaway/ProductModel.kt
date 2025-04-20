package com.example.khawajatakeaway

import java.io.Serializable

data class ProductModel(
    val id: String,  // Unique identifier for the product
    val name: String,
    val image: String,  // Changed from Int to String for better compatibility (Firebase URLs)
    val price: String
) : Serializable
