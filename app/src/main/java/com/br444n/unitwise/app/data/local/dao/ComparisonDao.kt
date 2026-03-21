package com.br444n.unitwise.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComparisonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComparison(comparison: ComparisonEntity): Long

    @Query("SELECT * FROM comparisons WHERE id = :id")
    suspend fun getComparisonById(id: Int): ComparisonEntity?

    @Query("SELECT * FROM comparisons ORDER BY timestamp DESC")
    fun getAllComparisons(): Flow<List<ComparisonEntity>>

    @Query("DELETE FROM comparisons")
    suspend fun deleteAllComparisons()
}
