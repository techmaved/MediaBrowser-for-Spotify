package de.techmaved.mediabrowserforspotify.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class Store(val context: Context) {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("store")
        private val USER_NAME_KEY = stringPreferencesKey("userName")
        val SETTINGS = stringPreferencesKey("settings")
    }

    val getUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }

    suspend fun saveUsername(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    inline fun <reified T> getSetting(key: String): Flow<T?> {
        return context.dataStore.data.map { preferences ->
            val preference = preferences[SETTINGS];

            if (preference == null) {
                return@map null
            }

            val currentSettings = Json.decodeFromString<Map<String, String>>(preference)
            val setting = currentSettings[key]

            if (setting == null) {
                return@map null
            }

            return@map Json.decodeFromString<T>(setting)
        }
    }

    suspend inline fun <reified T: Any> saveSetting(setting: T, key: String) {
        val jsonString = Json.encodeToString(setting)
        val currentSettings = context.dataStore.data.map { it[SETTINGS] }

        currentSettings.collect { settings ->
            var map: MutableMap<String, String>?

            if (settings == null) {
                map = mutableMapOf()
                map[key] = jsonString
            } else {
                map = Json.decodeFromString<MutableMap<String, String>>(settings);
                map[key] = jsonString
            }
            
            context.dataStore.edit { preferences ->
                preferences[SETTINGS] = Json.encodeToString(map)
            }
        }
    }
}