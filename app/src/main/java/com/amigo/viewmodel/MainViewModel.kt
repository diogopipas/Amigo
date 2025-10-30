package com.amigo.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amigo.data.repository.MealRepository
import com.amigo.model.DailySummary
import com.amigo.model.Meal
import com.amigo.model.NutritionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for MainScreen
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MealRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _dailySummary = MutableStateFlow<DailySummary?>(null)
    val dailySummary: StateFlow<DailySummary?> = _dailySummary.asStateFlow()

    private val _recentMeals = MutableStateFlow<List<Meal>>(emptyList())
    val recentMeals: StateFlow<List<Meal>> = _recentMeals.asStateFlow()

    init {
        loadDailySummary()
        loadRecentMeals()
    }

    /**
     * Analyze meal image and extract nutrition data
     */
    fun analyzeMeal(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnalyzing = true,
                error = null
            )

            repository.analyzeMealImage(imageUri)
                .onSuccess { nutritionData ->
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        analyzedNutrition = nutritionData,
                        currentImageUri = imageUri
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        error = error.message ?: "Failed to analyze meal"
                    )
                }
        }
    }

    /**
     * Save analyzed meal to database
     */
    fun saveMeal() {
        val state = _uiState.value
        val nutrition = state.analyzedNutrition ?: return
        val imageUri = state.currentImageUri ?: return

        viewModelScope.launch {
            try {
                val meal = Meal(
                    imageUri = imageUri.toString(),
                    calories = nutrition.calories,
                    protein = nutrition.protein,
                    carbs = nutrition.carbs,
                    fat = nutrition.fat
                )
                repository.saveMeal(meal)
                
                // Reset state
                _uiState.value = MainUiState()
                
                // Refresh summary and recent meals
                loadDailySummary()
                loadRecentMeals()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save meal: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear current analysis state
     */
    fun clearAnalysis() {
        _uiState.value = MainUiState()
    }

    /**
     * Load daily summary
     */
    private fun loadDailySummary() {
        viewModelScope.launch {
            _dailySummary.value = repository.getTodaySummary()
        }
    }

    /**
     * Load recent meals
     */
    private fun loadRecentMeals() {
        viewModelScope.launch {
            _recentMeals.value = repository.getRecentMeals(3)
        }
    }

    /**
     * Dismiss error message
     */
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MainUiState(
    val isAnalyzing: Boolean = false,
    val analyzedNutrition: NutritionData? = null,
    val currentImageUri: Uri? = null,
    val error: String? = null
)

