package com.peng.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peng.model.CartItem

@Entity(tableName = "cart_item")
data class CartItemEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "quantity")
    var quantity: Int,
)

fun CartItemEntity.mapToCartItem(): CartItem {
    return CartItem(
        this.id,
        this.name,
        this.description,
        this.price,
        this.image,
        this.quantity
    )
}