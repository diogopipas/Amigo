package com.amigo.data.repository

import android.content.Context
import android.net.Uri
import com.amigo.data.local.AppDatabase
import com.amigo.data.local.MealEntity
import com.amigo.data.remote.GeminiApiService
import com.amigo.model.DailySummary
import com.amigo.model.Meal
import com.amigo.model.NutritionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing meal data and API calls
 */
class MealRepository(context: Context) {
    private val mealDao = AppDatabase.getDatabase(context).mealDao()
    private val geminiApiService = GeminiApiService(context)

    /**
     * Analyze meal image and return nutrition data
     */
    suspend fun analyzeMealImage(imageUri: Uri): Result<NutritionData> {
        return geminiApiService.analyzeMealImage(imageUri)
    }

    /**
     * Save meal to local database
     */
    suspend fun saveMeal(meal: Meal): Long {
        return mealDao.insertMeal(meal.toEntity())
    }

    /**
     * Get all meals as Flow
     */
    fun getAllMeals(): Flow<List<Meal>> {
        return mealDao.getAllMeals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get meal by ID
     */
    suspend fun getMealById(mealId: Int): Meal? {
        return mealDao.getMealById(mealId)?.toDomain()
    }

    /**
     * Get today's meals
     */
    fun getTodayMeals(): Flow<List<Meal>> {
        return mealDao.getTodayMeals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get today's summary
     */
    suspend fun getTodaySummary(): DailySummary {
        return mealDao.getTodaySummary()?.toDomain()
            ?: DailySummary(date = System.currentTimeMillis())
    }

    /**
     * Get recent meals
     */
    suspend fun getRecentMeals(limit: Int = 3): List<Meal> {
        return mealDao.getRecentMeals(limit).map { it.toDomain() }
    }

    /**
     * Delete meal
     */
    suspend fun deleteMeal(mealId: Int) {
        mealDao.deleteMealById(mealId)
    }

    /**
     * Update a meal's nutrition values
     */
    suspend fun updateMealValues(mealId: Int, calories: Int, protein: Double, carbs: Double, fat: Double) {
        mealDao.updateMealValues(mealId, calories, protein, carbs, fat)
    }

    /**
     * Get daily summaries within a date range
     */
    suspend fun getDailySummaries(startTimestamp: Long, endTimestamp: Long): List<DailySummary> {
        return mealDao.getDailySummaries(startTimestamp, endTimestamp).map { it.toDomain() }
    }

    /**
     * Get summary for a specific period
     */
    suspend fun getPeriodSummary(startTimestamp: Long, endTimestamp: Long): DailySummary {
        return mealDao.getPeriodSummary(startTimestamp, endTimestamp)?.toDomain()
            ?: DailySummary(date = System.currentTimeMillis())
    }

    /**
     * Get total meal count
     */
    suspend fun getTotalMealCount(): Int {
        return mealDao.getTotalMealCount()
    }
}

