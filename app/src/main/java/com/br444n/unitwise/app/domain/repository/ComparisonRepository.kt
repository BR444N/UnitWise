package com.br444n.unitwise.app.domain.repository

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.feature.share.SharedComparisonLink
import kotlinx.coroutines.flow.Flow

interface ComparisonRepository {
    suspend fun insertComparison(comparison: ComparisonEntity): Long
    suspend fun getComparisonById(id: Int): ComparisonEntity?
    suspend fun getComparisonByShareId(shareId: String, encryptionKey: String? = null): ComparisonEntity?
    suspend fun publishSharedComparison(comparison: ComparisonEntity): SharedComparisonLink
    fun getAllComparisons(): Flow<List<ComparisonEntity>>
    suspend fun deleteAllComparisons()
}
