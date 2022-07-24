package com.peng.db

import androidx.room.*

@Dao
interface CartItemLocalDao {
    @Query("SELECT * FROM cart_item")
    suspend fun getAllCartItems(): List<CartItemEntity>

    @Query("SELECT * FROM cart_item WHERE id==:id")
    suspend fun getCartItem(id: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun removeCartItem(item: CartItemEntity)

    @Delete
    suspend fun removeAllCartItems(items: List<CartItemEntity>)
}