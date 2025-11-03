package com.amigo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amigo.model.Weight

/**
 * Room entity for storing weight data
 */
@Entity(tableName = "weights")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weight: Float,
    val timestamp: Long
) {
    fun toDomain(): Weight {
        return Weight(
            id = id,
            weight = weight,
            timestamp = timestamp
        )
    }
}

