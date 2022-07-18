package com.peng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peng.repo.CartRepository
import com.peng.repo.DataStorePrefsRepository
import com.peng.vm.CartFragmentViewModel
import com.peng.vm.ProductDetailsFragmentViewModel
import com.peng.vm.ProductsFragmentViewModel
import com.peng.vm.SharedActivityViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository,
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ProductsFragmentViewModel::class.java) -> {
                return ProductsFragmentViewModel(dataStorePrefsRepository) as T
            }
            modelClass.isAssignableFrom(ProductDetailsFragmentViewModel::class.java) -> {
                return ProductDetailsFragmentViewModel(dataStorePrefsRepository) as T
            }
            modelClass.isAssignableFrom(CartFragmentViewModel::class.java) -> {
                return CartFragmentViewModel(dataStorePrefsRepository) as T
            }
            modelClass.isAssignableFrom(CartFragmentViewModel::class.java) -> {
                return SharedActivityViewModel(
                    dataStorePrefsRepository,
                    cartRepository
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}