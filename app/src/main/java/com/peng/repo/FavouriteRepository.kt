package com.peng.repo

import com.peng.db.CartItemEntity
import com.peng.db.CartItemLocalDao
import com.peng.db.FavouriteItemEntity
import com.peng.db.FavouriteItemLocalDao
import javax.inject.Inject

// Repository to manage cart items
class FavouriteRepository @Inject constructor (private val favouriteItemLocalDao: FavouriteItemLocalDao) {

    suspend fun getAllFavouriteItems(): List<FavouriteItemEntity> {
        return favouriteItemLocalDao.getAllFavouriteItems()
    }

    suspend fun getFavouriteItem(id: String): FavouriteItemEntity? {
        return favouriteItemLocalDao.getFavouriteItem(id)
    }

    suspend fun updateFavouriteItem(item: FavouriteItemEntity) {
        favouriteItemLocalDao.updateFavouriteItem(item)
    }

    suspend fun insertFavouriteItem(item: FavouriteItemEntity) {
        favouriteItemLocalDao.insertFavouriteItem(item)
    }

    suspend fun removeFavouriteItem(item: FavouriteItemEntity) {
        favouriteItemLocalDao.removeFavouriteItem(item)
    }
}