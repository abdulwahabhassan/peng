package com.peng.vm

import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peng.db.PaymentCardEntity
import com.peng.db.mapToCartItem
import com.peng.db.mapToFavouriteItem
import com.peng.db.mapToPaymentCard
import com.peng.model.*
import com.peng.repo.CartRepository
import com.peng.repo.DataStorePrefsRepository
import com.peng.repo.FavouriteRepository
import com.peng.repo.PaymentCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedActivityViewModel @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository,
    private val cartRepository: CartRepository,
    private val favouriteRepository: FavouriteRepository,
    private val paymentCardRepository: PaymentCardRepository
): ViewModel() {

    private var _products:
            MutableLiveData<VMResult<List<Product>>> = MutableLiveData(VMResult.Loading())
    val products: LiveData<VMResult<List<Product>>> = _products

    private var _searchProductSuggestion:
            MutableLiveData<VMResult<List<SearchProductSuggestion>>> = MutableLiveData(VMResult.Loading())
    val searchProductSuggestion: LiveData<VMResult<List<SearchProductSuggestion>>> = _searchProductSuggestion

    private var _cartItems:
            MutableLiveData<VMResult<List<CartItem>>> = MutableLiveData(VMResult.Loading())
    val cartItems: LiveData<VMResult<List<CartItem>>> = _cartItems

    private var _favouriteItems:
            MutableLiveData<VMResult<List<FavouriteItem>>> = MutableLiveData(VMResult.Loading())
    val favouriteItems: LiveData<VMResult<List<FavouriteItem>>> = _favouriteItems

    private var _paymentCards:
            MutableLiveData<VMResult<List<PaymentCard>>> = MutableLiveData(VMResult.Loading())
    val paymentCards: LiveData<VMResult<List<PaymentCard>>> = _paymentCards

    private var _appConfigPreferences:
            MutableLiveData<DataStorePrefsRepository.AppConfigPreferences> = MutableLiveData(DataStorePrefsRepository.AppConfigPreferences())
    val appConfigPreferences: LiveData<DataStorePrefsRepository.AppConfigPreferences> = _appConfigPreferences

    init {
        collectAppConfigPreferences()
        viewModelScope.launch {
            fetchAndUpdateCartItemList()
            fetchProducts()
            fetchAndUpdateFavouriteItemList()
            fetchAndUpdatePaymentCardList()
            updateSearchProductsResult("")
        }
    }

    private fun collectAppConfigPreferences() {
        viewModelScope.launch {
            dataStorePrefsRepository.appConfigPreferencesFlow.collectLatest { prefs ->
                _appConfigPreferences.value = prefs
            }
        }
    }

    suspend fun fetchProducts(queryText: String = "") {
        val products: List<Product> = if (queryText.isNotEmpty()) {
            Product.products.filter { product ->
                product.name.contains(queryText, true)
            }
        } else {
            Product.products
        }
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

    fun updateSearchProductsResult(searchQuery: String) {
        viewModelScope.launch {
            _searchProductSuggestion.value = VMResult.Success(
                SearchProductSuggestion.results.filter { it.text.contains(searchQuery, true) }
            )
        }
    }

    private suspend fun fetchAndUpdateCartItemList() {
        val cartItems = cartRepository.getAllCartItems().map { cartItemEntity ->
            cartItemEntity.mapToCartItem()
        }
        _cartItems.value = VMResult.Success(cartItems)
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            dataStorePrefsRepository.updateUserEmail(email)
        }

    }

    fun updateProductsFilter(priceLowRange: Int, priceHighRange: Int, rating: Float) {
        viewModelScope.launch {
            dataStorePrefsRepository.updateProductsFilter(priceLowRange, priceHighRange, rating)
        }

    }

    private suspend fun fetchAndUpdateFavouriteItemList() {
        val favouriteItems = favouriteRepository.getAllFavouriteItems().map { favouriteItemEntity ->
            favouriteItemEntity.mapToFavouriteItem()
        }
        _favouriteItems.value = VMResult.Success(favouriteItems)
    }

    private suspend fun fetchAndUpdatePaymentCardList() {
        val paymentCards = paymentCardRepository.getAllPaymentCards().map { favouriteItemEntity ->
            favouriteItemEntity.mapToPaymentCard()
        }.map { paymentCard ->
            if (paymentCard.cardNumber ==
                dataStorePrefsRepository.fetchInitialPreferences().selectedPaymentCardNumber)
                paymentCard.copy(selected = true)
            else
                paymentCard
        }
        _paymentCards.value = VMResult.Success(paymentCards)
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

    fun clearCart() {
        viewModelScope.launch {
            val entities = cartRepository.getAllCartItems()
            if (entities.isNotEmpty()) {
                cartRepository.removeAllCartItems(entities)
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

    fun insertItemToPaymentCards(item: PaymentCard) {
        viewModelScope.launch {
            val entity = paymentCardRepository.getPaymentCard(item.cardNumber)
            if (entity != null && entity.cardType == item.cardType) {
                paymentCardRepository.updatePaymentCard(entity)
            } else {
                paymentCardRepository.insertPaymentCard(item.mapToPaymentCardEntity())
            }
            fetchAndUpdatePaymentCardList()
        }
    }

    fun removeItemFromPaymentCards(item: PaymentCard) {
        viewModelScope.launch {
            val entity = paymentCardRepository.getPaymentCard(item.cardNumber)
            if (entity != null && entity.cardType == item.cardType) {
                paymentCardRepository.removePaymentCard(entity)
            }
            fetchAndUpdatePaymentCardList()
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

    suspend fun updateGridPref(columns: Int) {
        dataStorePrefsRepository.updateGridPref(columns)
    }

    suspend fun updateSelectedPaymentCard(cardNumber: String) {
        dataStorePrefsRepository.updateSelectedPaymentCard(cardNumber)
        fetchAndUpdatePaymentCardList()
    }

}