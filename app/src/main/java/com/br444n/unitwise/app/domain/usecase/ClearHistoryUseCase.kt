package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.domain.repository.ComparisonRepository

class ClearHistoryUseCase(
    private val repository: ComparisonRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllComparisons()
    }
}
