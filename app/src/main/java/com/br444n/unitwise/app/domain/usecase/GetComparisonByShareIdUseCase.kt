package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository

class GetComparisonByShareIdUseCase(private val repository: ComparisonRepository) {
    suspend operator fun invoke(shareId: String, encryptionKey: String?): ComparisonEntity? {
        return repository.getComparisonByShareId(shareId, encryptionKey)
    }
}
