package com.peng.db

import androidx.room.*

@Dao
interface FavouriteItemLocalDao {
    @Query("SELECT * FROM favourite_item")
    suspend fun getAllFavouriteItems(): List<FavouriteItemEntity>

    @Query("SELECT * FROM favourite_item WHERE id==:id")
    suspend fun getFavouriteItem(id: String): FavouriteItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteItem(item: FavouriteItemEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFavouriteItem(item: FavouriteItemEntity)

    @Delete
    suspend fun removeFavouriteItem(item: FavouriteItemEntity)
}