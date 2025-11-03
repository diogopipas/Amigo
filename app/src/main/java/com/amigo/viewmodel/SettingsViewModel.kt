package com.amigo.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * ViewModel for SettingsScreen
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = getApplication<Application>().applicationContext.dataStore

    private val apiKeyKey = stringPreferencesKey("gemini_api_key")
    private val themeKey = stringPreferencesKey("theme_mode")
    private val calorieGoalKey = intPreferencesKey("daily_calorie_goal")
    private val proteinGoalKey = floatPreferencesKey("daily_protein_goal")
    private val carbsGoalKey = floatPreferencesKey("daily_carbs_goal")
    private val fatGoalKey = floatPreferencesKey("daily_fat_goal")
    private val weightGoalKey = floatPreferencesKey("weight_goal")
    private val currentWeightKey = floatPreferencesKey("current_weight")

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _themeMode = MutableStateFlow("light")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _calorieGoal = MutableStateFlow(2000)
    val calorieGoal: StateFlow<Int> = _calorieGoal.asStateFlow()

    private val _proteinGoal = MutableStateFlow(120.0f)
    val proteinGoal: StateFlow<Float> = _proteinGoal.asStateFlow()

    private val _carbsGoal = MutableStateFlow(200.0f)
    val carbsGoal: StateFlow<Float> = _carbsGoal.asStateFlow()

    private val _fatGoal = MutableStateFlow(65.0f)
    val fatGoal: StateFlow<Float> = _fatGoal.asStateFlow()

    private val _weightGoal = MutableStateFlow(0.0f)
    val weightGoal: StateFlow<Float> = _weightGoal.asStateFlow()

    private val _currentWeight = MutableStateFlow(0.0f)
    val currentWeight: StateFlow<Float> = _currentWeight.asStateFlow()

    init {
        viewModelScope.launch {
            // Collect all values from a single DataStore stream so none block each other
            dataStore.data.collect { preferences ->
                _apiKey.value = preferences[apiKeyKey] ?: ""
                _themeMode.value = preferences[themeKey] ?: "light"
                _calorieGoal.value = preferences[calorieGoalKey] ?: 2000
                _proteinGoal.value = preferences[proteinGoalKey] ?: 120.0f
                _carbsGoal.value = preferences[carbsGoalKey] ?: 200.0f
                _fatGoal.value = preferences[fatGoalKey] ?: 65.0f
                _weightGoal.value = preferences[weightGoalKey] ?: 0.0f
                _currentWeight.value = preferences[currentWeightKey] ?: 0.0f
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

    fun setCalorieGoal(calories: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[calorieGoalKey] = calories.coerceAtLeast(0)
            }
        }
    }

    fun setProteinGoal(protein: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[proteinGoalKey] = protein.coerceAtLeast(0f)
            }
        }
    }

    fun setCarbsGoal(carbs: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[carbsGoalKey] = carbs.coerceAtLeast(0f)
            }
        }
    }

    fun setFatGoal(fat: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[fatGoalKey] = fat.coerceAtLeast(0f)
            }
        }
    }

    fun setNutritionGoals(calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[calorieGoalKey] = calories.coerceAtLeast(0)
                preferences[proteinGoalKey] = protein.coerceAtLeast(0f)
                preferences[carbsGoalKey] = carbs.coerceAtLeast(0f)
                preferences[fatGoalKey] = fat.coerceAtLeast(0f)
            }
        }
    }

    fun setWeightGoal(weight: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[weightGoalKey] = weight.coerceAtLeast(0f)
            }
        }
    }

    fun setCurrentWeight(weight: Float) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[currentWeightKey] = weight.coerceAtLeast(0f)
            }
        }
    }
}

