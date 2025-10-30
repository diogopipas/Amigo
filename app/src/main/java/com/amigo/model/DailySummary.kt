package com.amigo.model

/**
 * Aggregated daily nutritional totals
 */
data class DailySummary(
    val date: Long,
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val mealCount: Int = 0
) {
    fun isEmpty(): Boolean = mealCount == 0
}

