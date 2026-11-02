package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.CalculationHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    /**
     * Latest history entries, capped so memory can't grow unboundedly
     * during long sessions.
     */
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 100")
    fun observeAll(): Flow<List<CalculationHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CalculationHistoryItem)

    @Query("UPDATE history SET isFavorite = (CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END) WHERE id = :itemId")
    suspend fun toggleFavorite(itemId: String)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}
