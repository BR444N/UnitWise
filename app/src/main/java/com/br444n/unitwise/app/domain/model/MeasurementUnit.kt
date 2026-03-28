package com.br444n.unitwise.app.domain.model

import java.util.Locale

/**
 * Supported measurement units grouped by compatibility family.
 * Products can only be compared within the same family.
 */
object MeasurementUnit {

    enum class Family {
        MASS,
        VOLUME,
        COUNT
    }

    data class UnitOption(
        val code: String,
        val family: Family
    )

    private val supportedUnits = listOf(
        UnitOption(code = "g", family = Family.MASS),
        UnitOption(code = "kg", family = Family.MASS),
        UnitOption(code = "ml", family = Family.VOLUME),
        UnitOption(code = "L", family = Family.VOLUME),
        UnitOption(code = "pcs", family = Family.COUNT)
    )

    val SUPPORTED_UNITS: List<String> = supportedUnits.map(UnitOption::code)

    fun familyOf(unitCode: String): Family? {
        val normalizedCode = unitCode.lowercase(Locale.US)
        return supportedUnits.firstOrNull { it.code.lowercase(Locale.US) == normalizedCode }?.family
    }

    fun compatibleUnitsFor(selectedUnit: String?): List<String> {
        val family = selectedUnit?.let(::familyOf) ?: return SUPPORTED_UNITS
        return supportedUnits
            .filter { it.family == family }
            .map(UnitOption::code)
    }

    fun areCompatible(unitA: String, unitB: String): Boolean {
        val familyA = familyOf(unitA)
        val familyB = familyOf(unitB)
        return familyA != null && familyA == familyB
    }
}
