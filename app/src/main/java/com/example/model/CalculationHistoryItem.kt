package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class CalculationHistoryItem(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val modeName: String,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
