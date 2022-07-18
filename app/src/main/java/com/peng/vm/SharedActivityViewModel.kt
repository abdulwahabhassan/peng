package com.peng.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peng.db.mapToCartItem
import com.peng.model.CartItem
import com.peng.model.Product
import com.peng.model.VMResult
import com.peng.model.mapToCartItemEntity
import com.peng.repo.CartRepository
import com.peng.repo.DataStorePrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.FieldPosition
import javax.inject.Inject

@HiltViewModel
class SharedActivityViewModel @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository,
    private val cartRepository: CartRepository
): ViewModel() {

    private var productsRVScrollPosition: Int = 0

    private var _products:
            MutableLiveData<VMResult<List<Product>>> = MutableLiveData(VMResult.Loading())
    val products: LiveData<VMResult<List<Product>>> = _products

    private var _cartItems:
            MutableLiveData<VMResult<List<CartItem>>> = MutableLiveData(VMResult.Loading())
    val cartItems: LiveData<VMResult<List<CartItem>>> = _cartItems

    init {
        viewModelScope.launch {
            fetchAndUpdateCartItemList()
            fetchProducts()
        }
    }

    suspend fun getAppConfig(): DataStorePrefsRepository.AppConfigPreferences {
        return dataStorePrefsRepository.fetchInitialPreferences()
    }

    suspend fun updateGridPref(columns: Int) {
        dataStorePrefsRepository.updateGridPref(columns)
    }

    fun updateCartItemQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity == 0) {
                cartRepository.removeCartItem(item.mapToCartItemEntity())
            } else {
                cartRepository.updateCartItem(item.mapToCartItemEntity())
            }
            fetchAndUpdateCartItemList()
        }
    }

    private suspend fun fetchAndUpdateCartItemList() {
        val cartItems = cartRepository.getAllCartItems().map { cartItemEntity ->
            cartItemEntity.mapToCartItem()
        }
        _cartItems.value = VMResult.Success(cartItems)
    }

    fun addOrRemoveItemFromCart(item: CartItem) {
        Log.d("PO", "Add or Remove")
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


    private suspend fun updateProductList(result: VMResult<List<Product>>) {
        when (result) {
            is VMResult.Success -> {
                _products.value = VMResult.Success(
                    indicateCartItems(
                        result.data,
                        cartRepository.getAllCartItems().map { cartItemEntity ->
                            cartItemEntity.mapToCartItem()
                        }
                    )
                )
            }
            else -> {}
        }
    }

    private suspend fun fetchProducts() {
        val products = Product.products
        updateProductList(VMResult.Success(products))
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

    suspend fun isItemInCart(id: String): Boolean {
        return cartRepository.getCartItem(id) != null
    }

    fun getProductsRVScrollPosition(): Int {
        return productsRVScrollPosition
    }

    fun setProductsRVScrollPosition(position: Int) {
        productsRVScrollPosition = position
    }

}