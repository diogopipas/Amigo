package com.amigo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amigo.data.repository.MealRepository
import com.amigo.model.Meal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for HistoryScreen
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MealRepository(application)

    val meals: StateFlow<List<Meal>> = repository.getAllMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteMeal(mealId: Int) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }
}

