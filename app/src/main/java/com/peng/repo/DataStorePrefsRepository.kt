package com.peng.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

// Repository to manage all user preferences / configurations
class DataStorePrefsRepository @Inject constructor (private val dataStore: DataStore<Preferences>) {

    // an observable flow of user preferences in datastore that can be collected in view models
    val appConfigPreferencesFlow: Flow<AppConfigPreferences> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            Timber.d(exception, "Error reading preferences.")
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        mapUserPreferences(preferences)
    }

    // obtain the first element emitted from data store by the flow
    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())

    // map preferences from data store to AppConfigPreferences data class when retrieving all user
    // preferences in data store
    private fun mapUserPreferences(preferences: Preferences): AppConfigPreferences {
        return AppConfigPreferences(
            gridColumns = preferences[PreferencesKeys.GRID] ?: 2,
            selectedPaymentCard = preferences[PreferencesKeys.PAYMENT_CARD] ?: "9090 9833 7331 0900"
        )
    }

    // update the grid columns pref
    suspend fun updateGridPref(columns: Int) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.GRID] = columns
        }
    }

    suspend fun updateSelectedPaymentCard(cardNumber: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.PAYMENT_CARD] = cardNumber
        }
    }


    // data class to hold all configurable fields in app settings screen and in-app configurable actions
    // that represent a user's preferences
    data class AppConfigPreferences(
        val gridColumns: Int,
        val selectedPaymentCard: String
    )

    // object to hold preference keys used to retrieve and update user preferences
    private object PreferencesKeys {
        val GRID = intPreferencesKey("grid")
        val PAYMENT_CARD = stringPreferencesKey("preferred_payment_card")

    }
}
