package com.example.khawajatakeaway

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class OrderItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1
): Parcelable
