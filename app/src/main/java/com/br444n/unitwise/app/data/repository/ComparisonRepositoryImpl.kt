package com.br444n.unitwise.app.data.repository

import android.util.Log
import com.br444n.unitwise.app.data.datasource.remote.SharedComparisonRemoteDataSource
import com.br444n.unitwise.app.data.local.dao.ComparisonDao
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import com.br444n.unitwise.app.feature.share.SharedComparisonLink
import com.br444n.unitwise.app.feature.share.createEncryptedSharedComparison
import com.br444n.unitwise.app.feature.share.decodeSharedComparison
import kotlinx.coroutines.flow.Flow

class ComparisonRepositoryImpl(
    private val dao: ComparisonDao,
    private val remoteDataSource: SharedComparisonRemoteDataSource
) : ComparisonRepository {
    override suspend fun insertComparison(comparison: ComparisonEntity): Long {
        return dao.insertComparison(comparison)
    }

    override suspend fun getComparisonById(id: Int): ComparisonEntity? {
        return dao.getComparisonById(id)
    }

    override suspend fun getComparisonByShareId(shareId: String, encryptionKey: String?): ComparisonEntity? {
        if (encryptionKey.isNullOrBlank()) {
            return null
        }

        val record = remoteDataSource.getComparisonByShareId(shareId) ?: return null
        return decodeSharedComparison(record, encryptionKey)
    }

    override suspend fun publishSharedComparison(comparison: ComparisonEntity): SharedComparisonLink {
        val (shareLink, encryptedRecord) = createEncryptedSharedComparison(comparison)
        runCatching {
            remoteDataSource.upsertComparison(encryptedRecord)
        }.onFailure {
            Log.w(TAG, "Failed to publish encrypted shared comparison", it)
            throw it
        }
        return shareLink
    }

    override fun getAllComparisons(): Flow<List<ComparisonEntity>> {
        return dao.getAllComparisons()
    }

    override suspend fun deleteAllComparisons() {
        dao.deleteAllComparisons()
    }

    private companion object {
        const val TAG = "ComparisonRepository"
    }
}
