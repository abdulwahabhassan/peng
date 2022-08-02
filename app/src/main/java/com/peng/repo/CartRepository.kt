package com.peng.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peng.db.CartItemEntity
import com.peng.db.CartItemLocalDao
import javax.inject.Inject

// Repository to manage cart items
class CartRepository @Inject constructor(private val cartItemLocalDao: CartItemLocalDao) {

    suspend fun getAllCartItems(): List<CartItemEntity> {
        return cartItemLocalDao.getAllCartItems()
    }

    suspend fun getCartItem(id: String): CartItemEntity? {
        return cartItemLocalDao.getCartItem(id)
    }

    suspend fun updateCartItem(item: CartItemEntity) {
        cartItemLocalDao.updateCartItem(item)
    }

    suspend fun insertCartItem(item: CartItemEntity) {
        cartItemLocalDao.insertCartItem(item)
    }

    suspend fun removeCartItem(item: CartItemEntity) {
        cartItemLocalDao.removeCartItem(item)
    }

    suspend fun removeAllCartItems(items: List<CartItemEntity>) {
        cartItemLocalDao.removeAllCartItems(items)
    }
}