package com.peng

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsFragmentViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
): ViewModel() {

    suspend fun getAppConfig(): AppConfigRepository.AppConfigPreferences {
        return appConfigRepository.fetchInitialPreferences()
    }

    suspend fun updateGridPref(columns: Int) {
        appConfigRepository.updateGridPref(columns)
    }

}