package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CalculationHistoryItem

@Database(
    entities = [CalculationHistoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class HistoryDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun get(context: Context): HistoryDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "calcquick.db"
                ).build().also { INSTANCE = it }
            }
    }
}
