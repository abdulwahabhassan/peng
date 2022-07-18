package com.peng.vm

import androidx.lifecycle.ViewModel
import com.peng.repo.DataStorePrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsFragmentViewModel @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository
): ViewModel() {

    suspend fun getAppConfig(): DataStorePrefsRepository.AppConfigPreferences {
        return dataStorePrefsRepository.fetchInitialPreferences()
    }

    suspend fun updateGridPref(columns: Int) {
        dataStorePrefsRepository.updateGridPref(columns)
    }

}