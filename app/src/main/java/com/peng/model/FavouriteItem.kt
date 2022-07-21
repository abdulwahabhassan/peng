package com.peng.model

import com.peng.db.FavouriteItemEntity


data class FavouriteItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val rating: Int
)

fun FavouriteItem.mapToFavouriteItemEntity(): FavouriteItemEntity {
    return FavouriteItemEntity(
        this.id,
        this.name,
        this.description,
        this.price,
        this.image,
        this.rating
    )
}