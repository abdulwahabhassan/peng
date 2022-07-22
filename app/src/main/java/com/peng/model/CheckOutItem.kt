package com.peng.model

import com.peng.db.CartItemEntity

data class CheckOutItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    var quantity: Int = 1,
)
