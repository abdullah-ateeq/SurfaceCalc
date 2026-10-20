package com.example.surfacecalc.data.model

import java.math.BigDecimal

data class SurfaceAreaResult(
    val shape: SurfaceShape,
    val rawValue: BigDecimal,
    val outputUnit: AreaUnit,
    val details: SurfaceCalculationDetails,
    val calculationSteps: List<String> = emptyList(),
    val inputs: Map<String, String> = emptyMap(),
    val inputUnits: Map<String, LengthUnit> = emptyMap(),
    val calculatedAt: Long = System.currentTimeMillis()
) {
    val value: BigDecimal get() = rawValue
    val formula: String get() = details.formula
    val calculation: String get() = details.calculation
}
