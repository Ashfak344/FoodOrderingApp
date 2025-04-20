package com.example.khawajatakeaway

data class CategoryModel(
    val key: String = "",
    val name: String = "",
    val image: String = ""
) {
    constructor() : this("", "", "")
}
