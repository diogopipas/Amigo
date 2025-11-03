package com.amigo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amigo.data.repository.MealRepository
import com.amigo.model.Meal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for DetailsScreen
 */
class DetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MealRepository(application)

    private val _meal = MutableStateFlow<Meal?>(null)
    val meal: StateFlow<Meal?> = _meal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadMeal(mealId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _meal.value = repository.getMealById(mealId)
            _isLoading.value = false
        }
    }

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }

    fun scaleMealPortions(mealId: Int, factor: Double) {
        viewModelScope.launch {
            val meal = repository.getMealById(mealId) ?: return@launch
            val safeFactor = if (factor.isFinite() && factor > 0.0) factor else 1.0
            val newCalories = (meal.calories * safeFactor).toInt()
            val newProtein = meal.protein * safeFactor
            val newCarbs = meal.carbs * safeFactor
            val newFat = meal.fat * safeFactor
            repository.updateMealValues(mealId, newCalories, newProtein, newCarbs, newFat)
            _meal.value = repository.getMealById(mealId)
        }
    }
}

