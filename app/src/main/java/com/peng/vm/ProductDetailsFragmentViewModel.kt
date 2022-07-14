package com.peng.vm

import androidx.lifecycle.ViewModel
import com.peng.repo.AppConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailsFragmentViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
): ViewModel() {

    suspend fun getAppConfig(): AppConfigRepository.AppConfigPreferences {
        return appConfigRepository.fetchInitialPreferences()
    }

}