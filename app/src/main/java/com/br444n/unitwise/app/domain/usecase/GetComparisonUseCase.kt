package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository

class GetComparisonUseCase(private val repository: ComparisonRepository) {
    suspend operator fun invoke(id: Int): ComparisonEntity? {
        return repository.getComparisonById(id)
    }
}
