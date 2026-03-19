package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.feature.home.components.ProductInputState
import java.util.Locale
import kotlin.math.abs

data class ComparisonResult(
    val isProductAWinner: Boolean,
    val isTie: Boolean,
    val savingsTotal: String,
    val monthlySavings: String,
    val savingsPerStandardUnit: String,
    val standardUnitDesc: String,
    val standardUnitPrice: String
)

class CompareProductsUseCase {

    operator fun invoke(productA: ProductInputState, productB: ProductInputState): ComparisonResult {
        // 1. Normalize Inputs
        val priceA = productA.price.toDoubleOrNull() ?: 0.0
        val contentA = productA.contentAmount.toDoubleOrNull() ?: 1.0
        val qtyA = productA.quantity.toDoubleOrNull() ?: 1.0

        val priceB = productB.price.toDoubleOrNull() ?: 0.0
        val contentB = productB.contentAmount.toDoubleOrNull() ?: 1.0
        val qtyB = productB.quantity.toDoubleOrNull() ?: 1.0
        
        // Let's assume for now units match or are directly comparable.
        // A full implementation would apply multiplier converting kg to g, L to ml here based on selectedUnit.
        val multiplierA = getUnitMultiplier(productA.selectedUnit)
        val multiplierB = getUnitMultiplier(productB.selectedUnit)
        
        val normalizedContentA = contentA * multiplierA
        val normalizedContentB = contentB * multiplierB

        // 2. Price per Unit (PPU)
        val ppuA = priceA / normalizedContentA
        val ppuB = priceB / normalizedContentB

        // 3. Determine Winner
        val isTie = abs(ppuA - ppuB) < 0.0001
        val isProductAWinner = ppuA <= ppuB

        // 4. Calculate Savings
        val winnerPPU = if (isProductAWinner) ppuA else ppuB
        val loserPPU = if (isProductAWinner) ppuB else ppuA
        
        val winnerContent = if (isProductAWinner) normalizedContentA else normalizedContentB
        val winnerQty = if (isProductAWinner) qtyA else qtyB
        val winnerPrice = if (isProductAWinner) priceA else priceB

        val totalVolumeToBuy = winnerContent * winnerQty
        val costIfBoughtLoser = totalVolumeToBuy * loserPPU
        val actualCostWinner = winnerPrice * winnerQty

        val savingsTotal = costIfBoughtLoser - actualCostWinner

        // 5. Monthly Savings
        val monthlySavings = savingsTotal * 4

        // 6. Savings per Standard Unit
        val baseUnitRaw = if (isProductAWinner) productA.selectedUnit else productB.selectedUnit
        val baseUnitLower = baseUnitRaw.lowercase(Locale.US)
        val ppuDiff = maxOf(0.0, loserPPU - winnerPPU)

        val standardUnitDesc: String
        val savingsPerStandard: Double
        val standardUnitPriceValue: Double

        when (baseUnitLower) {
            "g", "ml" -> {
                savingsPerStandard = ppuDiff * 100.0
                standardUnitPriceValue = winnerPPU * 100.0
                standardUnitDesc = "100 $baseUnitLower"
            }
            "kg", "l", "lts" -> {
                savingsPerStandard = ppuDiff * 1000.0
                standardUnitPriceValue = winnerPPU * 1000.0
                val displayUnit = if (baseUnitLower == "kg") "kg" else "l"
                standardUnitDesc = "1 $displayUnit"
            }
            else -> {
                savingsPerStandard = ppuDiff * 1.0 // pcs
                standardUnitPriceValue = winnerPPU * 1.0
                standardUnitDesc = "1 $baseUnitRaw"
            }
        }

        return ComparisonResult(
            isProductAWinner = isProductAWinner,
            isTie = isTie,
            savingsTotal = String.format(Locale.US, "%.2f", maxOf(0.0, savingsTotal)),
            monthlySavings = String.format(Locale.US, "%.2f", maxOf(0.0, monthlySavings)),
            savingsPerStandardUnit = String.format(Locale.US, "%.2f", savingsPerStandard),
            standardUnitDesc = standardUnitDesc,
            standardUnitPrice = String.format(Locale.US, "%.2f", standardUnitPriceValue)
        )
    }

    private fun getUnitMultiplier(unit: String): Double {
        return when (unit.lowercase(Locale.US)) {
            "kg" -> 1000.0
            "l", "lts" -> 1000.0
            else -> 1.0 // g, ml, pcs
        }
    }
}
