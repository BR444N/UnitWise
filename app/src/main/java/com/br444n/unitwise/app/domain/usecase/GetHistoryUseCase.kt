package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val repository: ComparisonRepository) {
    operator fun invoke(): Flow<List<ComparisonEntity>> {
        return repository.getAllComparisons()
    }
}
