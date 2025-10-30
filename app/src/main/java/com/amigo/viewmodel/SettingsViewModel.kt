package com.amigo.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * ViewModel for SettingsScreen
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = getApplication<Application>().applicationContext.dataStore

    private val apiKeyKey = stringPreferencesKey("gemini_api_key")
    private val themeKey = stringPreferencesKey("theme_mode")

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _themeMode = MutableStateFlow("light")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[apiKeyKey] ?: ""
            }.collect { apiKey ->
                _apiKey.value = apiKey
            }

            dataStore.data.map { preferences ->
                preferences[themeKey] ?: "light"
            }.collect { theme ->
                _themeMode.value = theme
            }
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[apiKeyKey] = key
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[themeKey] = mode
            }
        }
    }
}

