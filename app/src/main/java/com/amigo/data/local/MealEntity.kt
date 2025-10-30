package com.amigo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amigo.model.Meal

/**
 * Room entity for storing meal data
 */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val timestamp: Long
) {
    fun toDomain(): Meal {
        return Meal(
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

