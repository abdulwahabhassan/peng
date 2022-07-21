package com.peng.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peng.model.CartItem
import com.peng.model.FavouriteItem

@Entity(tableName = "favourite_item")
data class FavouriteItemEntity (
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
    @ColumnInfo(name = "rating")
    val rating: Int
)

fun FavouriteItemEntity.mapToFavouriteItem(): FavouriteItem {
    return FavouriteItem(
        this.id,
        this.name,
        this.description,
        this.price,
        this.image,
        this.rating
    )
}