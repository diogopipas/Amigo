package com.amigo.model

import com.amigo.data.local.MealEntity

/**
 * Domain model for a meal entry
 */
data class Meal(
    val id: Int = 0,
    val imageUri: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toEntity(): MealEntity {
        return MealEntity(
            id = id,
            imageUri = imageUri,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            timestamp = timestamp
        )
    }
}

