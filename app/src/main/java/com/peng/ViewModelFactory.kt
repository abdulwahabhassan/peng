package com.peng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}