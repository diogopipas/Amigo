package com.amigo.model

/**
 * Domain model for weight entry
 */
data class Weight(
    val id: Int = 0,
    val weight: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toEntity(): com.amigo.data.local.WeightEntity {
        return com.amigo.data.local.WeightEntity(
            id = id,
            weight = weight,
            timestamp = timestamp
        )
    }
}

