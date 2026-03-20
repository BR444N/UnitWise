package com.br444n.unitwise.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br444n.unitwise.app.data.local.dao.ComparisonDao
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity

@Database(entities = [ComparisonEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun comparisonDao(): ComparisonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unitwise_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
