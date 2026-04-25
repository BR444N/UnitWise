package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.domain.model.MeasurementUnit
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
    val unitPriceA: String,
    val unitPriceB: String
)

class IncompatibleMeasurementUnitsException : IllegalArgumentException()

class CompareProductsUseCase {

    operator fun invoke(productA: ProductInputState, productB: ProductInputState): ComparisonResult {
        if (!MeasurementUnit.areCompatible(productA.selectedUnit, productB.selectedUnit)) {
            throw IncompatibleMeasurementUnitsException()
        }

        // 1. Normalize Inputs
        val priceA = requireNotNull(productA.price.toDoubleOrNull())
        val contentA = requireNotNull(productA.contentAmount.toDoubleOrNull())
        val qtyA = requireNotNull(productA.quantity.toDoubleOrNull())

        val priceB = requireNotNull(productB.price.toDoubleOrNull())
        val contentB = requireNotNull(productB.contentAmount.toDoubleOrNull())
        val qtyB = requireNotNull(productB.quantity.toDoubleOrNull())

        require(contentA > 0)
        require(contentB > 0)
        require(qtyA > 0)
        require(qtyB > 0)
        require(priceA >= 0)
        require(priceB >= 0)
        
        val multiplierA = getUnitMultiplier(productA.selectedUnit)
        val multiplierB = getUnitMultiplier(productB.selectedUnit)
        
        val normalizedContentA = contentA * multiplierA
        val normalizedContentB = contentB * multiplierB

        val totalContentA = normalizedContentA * qtyA
        val totalContentB = normalizedContentB * qtyB

        val totalPriceA = priceA * qtyA
        val totalPriceB = priceB * qtyB

        // 2. Price per Unit (PPU)
        val ppuA = totalPriceA / totalContentA
        val ppuB = totalPriceB / totalContentB

        // 3. Determine Winner
        // Improved precision: 0.0001 instead of 0.01 to handle price per ml/g
        val isTie = abs(ppuA - ppuB) < 0.0001
        val isProductAWinner = when {
            isTie -> false
            else -> ppuA < ppuB
        }

        // 4. Calculate Savings
        val winnerPPU = if (isProductAWinner) ppuA else ppuB
        val loserPPU = if (isProductAWinner) ppuB else ppuA
        
        val winnerTotalContent = if (isProductAWinner) totalContentA else totalContentB
        val winnerTotalPrice = if (isProductAWinner) totalPriceA else totalPriceB

        val costIfBoughtLoser = winnerTotalContent * loserPPU
        val savingsTotal = costIfBoughtLoser - winnerTotalPrice

        // 5. Monthly Savings
        val monthlySavings = savingsTotal * 4

        // 6. Standardized Comparisons
        // We use the winner's unit scale for the "Why better" section
        val baseUnitRaw = if (isProductAWinner) productA.selectedUnit else productB.selectedUnit
        val baseUnitLower = baseUnitRaw.lowercase(Locale.US)
        val ppuDiff = maxOf(0.0, loserPPU - winnerPPU)

        val standardUnitDesc: String
        val savingsPerStandard: Double
        val unitPriceAValue: Double
        val unitPriceBValue: Double

        when (baseUnitLower) {
            "g", "ml" -> {
                standardUnitDesc = "100 $baseUnitLower"
                savingsPerStandard = ppuDiff * 100.0
                unitPriceAValue = ppuA * 100.0
                unitPriceBValue = ppuB * 100.0
            }
            "kg", "l", "lts" -> {
                val displayUnit = if (baseUnitLower == "kg") "kg" else "l"
                standardUnitDesc = "1 $displayUnit"
                savingsPerStandard = ppuDiff * 1000.0
                unitPriceAValue = ppuA * 1000.0
                unitPriceBValue = ppuB * 1000.0
            }
            else -> {
                standardUnitDesc = "1 $baseUnitRaw"
                savingsPerStandard = ppuDiff * 1.0
                unitPriceAValue = ppuA * 1.0
                unitPriceBValue = ppuB * 1.0
            }
        }

        return ComparisonResult(
            isProductAWinner = isProductAWinner,
            isTie = isTie,
            savingsTotal = String.format(Locale.US, "%.2f", maxOf(0.0, savingsTotal)),
            monthlySavings = String.format(Locale.US, "%.2f", maxOf(0.0, monthlySavings)),
            savingsPerStandardUnit = String.format(Locale.US, "%.2f", savingsPerStandard),
            standardUnitDesc = standardUnitDesc,
            unitPriceA = String.format(Locale.US, "%.2f", unitPriceAValue),
            unitPriceB = String.format(Locale.US, "%.2f", unitPriceBValue)
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
