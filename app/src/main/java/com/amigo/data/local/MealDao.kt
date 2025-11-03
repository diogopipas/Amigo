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

    @Query("""
        UPDATE meals SET 
            calories = :calories,
            protein = :protein,
            carbs = :carbs,
            fat = :fat
        WHERE id = :mealId
    """)
    suspend fun updateMealValues(
        mealId: Int,
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double
    )

    // Statistics queries
    @Query("""
        SELECT 
            date(timestamp / 1000, 'unixepoch', 'localtime') as date,
            SUM(calories) as totalCalories,
            SUM(protein) as totalProtein,
            SUM(carbs) as totalCarbs,
            SUM(fat) as totalFat,
            COUNT(*) as mealCount
        FROM meals
        WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp
        GROUP BY date(timestamp / 1000, 'unixepoch', 'localtime')
        ORDER BY date ASC
    """)
    suspend fun getDailySummaries(startTimestamp: Long, endTimestamp: Long): List<DailySummaryEntity>

    @Query("""
        SELECT 
            '' as date,
            SUM(calories) as totalCalories,
            SUM(protein) as totalProtein,
            SUM(carbs) as totalCarbs,
            SUM(fat) as totalFat,
            COUNT(*) as mealCount
        FROM meals
        WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp
    """)
    suspend fun getPeriodSummary(startTimestamp: Long, endTimestamp: Long): DailySummaryEntity?

    @Query("SELECT COUNT(*) FROM meals")
    suspend fun getTotalMealCount(): Int
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
        // Parse date string (YYYY-MM-DD) or use current time if empty/invalid
        val parsedDate = if (date.isNotEmpty()) {
            try {
                val parts = date.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, year)
                        set(java.util.Calendar.MONTH, month - 1)
                        set(java.util.Calendar.DAY_OF_MONTH, day)
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }.timeInMillis
                } else {
                    System.currentTimeMillis()
                }
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
        
        return com.amigo.model.DailySummary(
            date = parsedDate,
            totalCalories = totalCalories ?: 0,
            totalProtein = totalProtein ?: 0.0,
            totalCarbs = totalCarbs ?: 0.0,
            totalFat = totalFat ?: 0.0,
            mealCount = mealCount ?: 0
        )
    }
}

