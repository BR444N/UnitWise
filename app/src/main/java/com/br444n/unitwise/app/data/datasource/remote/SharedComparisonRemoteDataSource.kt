package com.br444n.unitwise.app.data.datasource.remote

import com.br444n.unitwise.app.feature.share.SharedComparisonRecord
import io.github.jan.supabase.postgrest.from

class SharedComparisonRemoteDataSource {
    private val client = SupabaseProvider.client

    suspend fun upsertComparison(record: SharedComparisonRecord) {
        client.from("shared_comparisons").upsert(record.toDto()) {
            onConflict = "share_id"
        }
    }

    suspend fun getComparisonByShareId(shareId: String): SharedComparisonRecord? {
        return client.from("shared_comparisons")
            .select {
                filter {
                    eq("share_id", shareId)
                }
            }
            .decodeList<SharedComparisonDto>()
            .firstOrNull()
            ?.toRecord()
    }
}

private fun SharedComparisonRecord.toDto(): SharedComparisonDto {
    return SharedComparisonDto(
        shareId = shareId,
        ciphertext = ciphertext,
        iv = iv,
        expiresAt = expiresAt,
        createdAt = createdAt
    )
}

private fun SharedComparisonDto.toRecord(): SharedComparisonRecord {
    return SharedComparisonRecord(
        shareId = shareId,
        ciphertext = ciphertext,
        iv = iv,
        expiresAt = expiresAt,
        createdAt = createdAt
    )
}
