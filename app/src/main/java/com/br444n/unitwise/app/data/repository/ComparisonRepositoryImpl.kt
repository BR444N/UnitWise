package com.br444n.unitwise.app.data.repository

import com.br444n.unitwise.app.data.local.dao.ComparisonDao
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import kotlinx.coroutines.flow.Flow

class ComparisonRepositoryImpl(
    private val dao: ComparisonDao
) : ComparisonRepository {
    override suspend fun insertComparison(comparison: ComparisonEntity): Long {
        return dao.insertComparison(comparison)
    }

    override suspend fun getComparisonById(id: Int): ComparisonEntity? {
        return dao.getComparisonById(id)
    }

    override fun getAllComparisons(): Flow<List<ComparisonEntity>> {
        return dao.getAllComparisons()
    }
}
