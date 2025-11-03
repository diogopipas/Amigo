package com.amigo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amigo.data.repository.MealRepository
import com.amigo.model.DailySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for StatisticsScreen
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MealRepository(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _dailySummaries = MutableStateFlow<List<DailySummary>>(emptyList())
    val dailySummaries: StateFlow<List<DailySummary>> = _dailySummaries.asStateFlow()

    private val _periodSummary = MutableStateFlow<DailySummary?>(null)
    val periodSummary: StateFlow<DailySummary?> = _periodSummary.asStateFlow()

    private val _totalMeals = MutableStateFlow(0)
    val totalMeals: StateFlow<Int> = _totalMeals.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(Period.WEEK)
    val selectedPeriod: StateFlow<Period> = _selectedPeriod.asStateFlow()

    init {
        loadStatistics()
    }

    fun setPeriod(period: Period) {
        _selectedPeriod.value = period
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (startTime, endTime) = getPeriodTimestamps(_selectedPeriod.value)
                
                _dailySummaries.value = repository.getDailySummaries(startTime, endTime)
                _periodSummary.value = repository.getPeriodSummary(startTime, endTime)
                _totalMeals.value = repository.getTotalMealCount()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getPeriodTimestamps(period: Period): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        when (period) {
            Period.WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            Period.MONTH -> calendar.add(Calendar.DAY_OF_YEAR, -30)
            Period.THREE_MONTHS -> calendar.add(Calendar.DAY_OF_YEAR, -90)
            Period.YEAR -> calendar.add(Calendar.DAY_OF_YEAR, -365)
        }
        
        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }
}

enum class Period(val displayName: String) {
    WEEK("Week"),
    MONTH("Month"),
    THREE_MONTHS("3 Months"),
    YEAR("Year")
}

