package com.peng.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peng.db.mapToCartItem
import com.peng.db.mapToFavouriteItem
import com.peng.model.*
import com.peng.repo.CartRepository
import com.peng.repo.DataStorePrefsRepository
import com.peng.repo.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.FieldPosition
import javax.inject.Inject

@HiltViewModel
class SharedActivityViewModel @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository,
    private val cartRepository: CartRepository,
    private val favouriteRepository: FavouriteRepository
): ViewModel() {

    private var _products:
            MutableLiveData<VMResult<List<Product>>> = MutableLiveData(VMResult.Loading())
    val products: LiveData<VMResult<List<Product>>> = _products

    private var _cartItems:
            MutableLiveData<VMResult<List<CartItem>>> = MutableLiveData(VMResult.Loading())
    val cartItems: LiveData<VMResult<List<CartItem>>> = _cartItems

    private var _favouriteItems:
            MutableLiveData<VMResult<List<FavouriteItem>>> = MutableLiveData(VMResult.Loading())
    val favouriteItems: LiveData<VMResult<List<FavouriteItem>>> = _favouriteItems

    init {
        viewModelScope.launch {
            fetchAndUpdateCartItemList()
            fetchProducts()
        }
    }

    private suspend fun fetchProducts() {
        val products = Product.products
        updateProductList(VMResult.Success(products))
    }

    private suspend fun updateProductList(result: VMResult<List<Product>>) {
        when (result) {
            is VMResult.Success -> {
                var products = indicateCartItems(
                    result.data,
                    cartRepository.getAllCartItems().map { cartItemEntity ->
                        cartItemEntity.mapToCartItem()
                    }
                )
                products = indicateFavouriteItems(
                    products,
                    favouriteRepository.getAllFavouriteItems().map { favouriteItemEntity ->
                        favouriteItemEntity.mapToFavouriteItem()
                    }
                )
                _products.value = VMResult.Success(products)
            }
            else -> {}
        }
    }


    private suspend fun fetchAndUpdateCartItemList() {
        val cartItems = cartRepository.getAllCartItems().map { cartItemEntity ->
            cartItemEntity.mapToCartItem()
        }
        _cartItems.value = VMResult.Success(cartItems)
    }

    private suspend fun fetchAndUpdateFavouriteItemList() {
        val favouriteItems = favouriteRepository.getAllFavouriteItems().map { favouriteItemEntity ->
            favouriteItemEntity.mapToFavouriteItem()
        }
        _favouriteItems.value = VMResult.Success(favouriteItems)
    }

    fun addOrRemoveItemFromCart(item: CartItem) {
        viewModelScope.launch {
            val entity = cartRepository.getCartItem(item.id)
            if (entity != null) {
                cartRepository.removeCartItem(entity)
            } else {
                cartRepository.insertCartItem(item.mapToCartItemEntity())
            }
            updateProductList(_products.value ?: VMResult.Success(emptyList()))
            fetchAndUpdateCartItemList()
        }
    }

    fun addOrRemoveItemFromFavourite(item: FavouriteItem) {
        viewModelScope.launch {
            val entity = favouriteRepository.getFavouriteItem(item.id)
            if (entity != null) {
                favouriteRepository.removeFavouriteItem(entity)
            } else {
                favouriteRepository.insertFavouriteItem(item.mapToFavouriteItemEntity())
            }
            updateProductList(_products.value ?: VMResult.Success(emptyList()))
            fetchAndUpdateFavouriteItemList()
        }
    }

    private fun indicateCartItems(
        products: List<Product>,
        cartItems: List<CartItem>
    ): List<Product> {
        val cartItemIds = cartItems.map { it.id }
        val productIds =  products.map { it.id }
        val commonIds = cartItemIds.intersect(productIds)
        return products.map { product: Product ->
            if (commonIds.contains(product.id)) {
                product.copy(isInCart = true)
            } else {
                product.copy(isInCart = false)
            }
        }
    }

    private fun indicateFavouriteItems(
        products: List<Product>,
        favouriteItems: List<FavouriteItem>
    ): List<Product> {
        val favouriteItemIds = favouriteItems.map { it.id }
        val productIds =  products.map { it.id }
        val commonIds = favouriteItemIds.intersect(productIds)
        return products.map { product: Product ->
            if (commonIds.contains(product.id)) {
                product.copy(isInFavourite = true)
            } else {
                product.copy(isInFavourite = false)
            }
        }
    }

    suspend fun isItemInCart(id: String): Boolean {
        return cartRepository.getCartItem(id) != null
    }

    suspend fun isItemInFavourite(id: String): Boolean {
        return favouriteRepository.getFavouriteItem(id) != null
    }

    fun updateCartItemQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity == 0) {
                cartRepository.removeCartItem(item.mapToCartItemEntity())
            } else {
                cartRepository.updateCartItem(item.mapToCartItemEntity())
            }
            fetchAndUpdateCartItemList()
            updateProductList(_products.value ?: VMResult.Success(emptyList()))
        }
    }

    suspend fun getAppConfig(): DataStorePrefsRepository.AppConfigPreferences {
        return dataStorePrefsRepository.fetchInitialPreferences()
    }

    suspend fun updateGridPref(columns: Int) {
        dataStorePrefsRepository.updateGridPref(columns)
    }

}