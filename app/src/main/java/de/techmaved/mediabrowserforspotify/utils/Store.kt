package de.techmaved.mediabrowserforspotify.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Store(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("store")
        private val USER_NAME_KEY = stringPreferencesKey("userName")
    }

    val getUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }

    suspend fun saveUsername(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }
}