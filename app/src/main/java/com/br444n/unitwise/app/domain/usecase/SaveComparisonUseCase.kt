package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import com.br444n.unitwise.app.feature.home.components.ProductInputState

class SaveComparisonUseCase(private val repository: ComparisonRepository) {
    suspend operator fun invoke(
        productA: ProductInputState,
        productB: ProductInputState,
        comparisonId: Int? = null
    ): Long {
        val entity = ComparisonEntity(
            id = comparisonId ?: 0,
            productAName = productA.productName,
            productAContent = productA.contentAmount,
            productAUnit = productA.selectedUnit,
            productAPrice = productA.price,
            productAQuantity = productA.quantity,
            
            productBName = productB.productName,
            productBContent = productB.contentAmount,
            productBUnit = productB.selectedUnit,
            productBPrice = productB.price,
            productBQuantity = productB.quantity
        )
        return repository.insertComparison(entity)
    }
}
