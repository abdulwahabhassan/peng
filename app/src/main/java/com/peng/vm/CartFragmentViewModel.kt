package com.peng.vm

import androidx.lifecycle.ViewModel
import com.peng.repo.DataStorePrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartFragmentViewModel @Inject constructor(
    private val dataStorePrefsRepository: DataStorePrefsRepository
): ViewModel() {

    suspend fun getAppConfig(): DataStorePrefsRepository.AppConfigPreferences {
        return dataStorePrefsRepository.fetchInitialPreferences()
    }

}