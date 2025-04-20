package com.example.khawajatakeaway

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem(
    var id: String = "",
    var name: String = "",
    var des: String = "",
    var price: Double,  // ✅ Changed to Double
    var imageRes: String = "",
    var category: String = ""
) : Parcelable {
    constructor() : this("", "", "", 0.0, "") // ✅ No-Arg Constructor for Firebase
}

