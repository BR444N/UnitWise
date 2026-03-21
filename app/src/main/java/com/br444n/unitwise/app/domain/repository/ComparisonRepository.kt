package com.br444n.unitwise.app.domain.repository

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import kotlinx.coroutines.flow.Flow

interface ComparisonRepository {
    suspend fun insertComparison(comparison: ComparisonEntity): Long
    suspend fun getComparisonById(id: Int): ComparisonEntity?
    fun getAllComparisons(): Flow<List<ComparisonEntity>>
    suspend fun deleteAllComparisons()
}
