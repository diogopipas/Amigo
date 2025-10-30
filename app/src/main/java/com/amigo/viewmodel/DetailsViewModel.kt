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
}

