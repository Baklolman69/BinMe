package com.tensormind.binme.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bin_me_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_UID = stringPreferencesKey("user_uid")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val USER_LOCATION = stringPreferencesKey("user_location")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    val userLocation: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_LOCATION] ?: "Lake Oswego, OR"
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setUserLocation(location: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_LOCATION] = location
        }
    }

    suspend fun saveUserUid(uid: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_UID] = uid
        }
    }

    val userUid: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_UID]
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
