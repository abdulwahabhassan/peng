package com.peng.model

import com.peng.db.CartItemEntity
import com.peng.db.mapToCartItem

data class CartItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    var quantity: Int = 1,
)

fun CartItem.mapToCartItemEntity(): CartItemEntity {
    return CartItemEntity(
        this.id,
        this.name,
        this.description,
        this.price,
        this.image,
        this.quantity
    )
}