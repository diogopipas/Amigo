package com.amigo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Weight operations
 */
@Dao
interface WeightDao {
    @Query("SELECT * FROM weights ORDER BY timestamp DESC")
    fun getAllWeights(): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weights ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeight(): WeightEntity?

    @Query("SELECT * FROM weights ORDER BY timestamp ASC")
    fun getAllWeightsAscending(): Flow<List<WeightEntity>>

    @Insert
    suspend fun insertWeight(weight: WeightEntity): Long

    @Delete
    suspend fun deleteWeight(weight: WeightEntity)

    @Query("DELETE FROM weights WHERE id = :weightId")
    suspend fun deleteWeightById(weightId: Int)
}

