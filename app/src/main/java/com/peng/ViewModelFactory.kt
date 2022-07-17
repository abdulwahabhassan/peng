package com.peng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.peng.repo.AppConfigRepository
import com.peng.vm.CartFragmentViewModel
import com.peng.vm.ProductDetailsFragmentViewModel
import com.peng.vm.ProductsFragmentViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    val appConfigRepository: AppConfigRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ProductsFragmentViewModel::class.java) -> {
                return ProductsFragmentViewModel(appConfigRepository) as T
            }
            modelClass.isAssignableFrom(ProductDetailsFragmentViewModel::class.java) -> {
                return ProductDetailsFragmentViewModel(appConfigRepository) as T
            }
            modelClass.isAssignableFrom(CartFragmentViewModel::class.java) -> {
                return CartFragmentViewModel(appConfigRepository) as T
            }
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}