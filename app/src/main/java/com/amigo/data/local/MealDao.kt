package com.amigo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Meal operations
 */
@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Int): MealEntity?

    @Query("""
        SELECT * FROM meals 
        WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        ORDER BY timestamp DESC
    """)
    fun getTodayMeals(): Flow<List<MealEntity>>

    @Query("""
        SELECT 
            date(timestamp / 1000, 'unixepoch', 'localtime') as date,
            SUM(calories) as totalCalories,
            SUM(protein) as totalProtein,
            SUM(carbs) as totalCarbs,
            SUM(fat) as totalFat,
            COUNT(*) as mealCount
        FROM meals
        WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        GROUP BY date(timestamp / 1000, 'unixepoch', 'localtime')
    """)
    suspend fun getTodaySummary(): DailySummaryEntity?

    @Insert
    suspend fun insertMeal(meal: MealEntity): Long

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Int)

    @Query("SELECT * FROM meals ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMeals(limit: Int): List<MealEntity>
}

data class DailySummaryEntity(
    val date: String,
    val totalCalories: Int?,
    val totalProtein: Double?,
    val totalCarbs: Double?,
    val totalFat: Double?,
    val mealCount: Int?
) {
    fun toDomain(): com.amigo.model.DailySummary {
        return com.amigo.model.DailySummary(
            date = System.currentTimeMillis(),
            totalCalories = totalCalories ?: 0,
            totalProtein = totalProtein ?: 0.0,
            totalCarbs = totalCarbs ?: 0.0,
            totalFat = totalFat ?: 0.0,
            mealCount = mealCount ?: 0
        )
    }
}

