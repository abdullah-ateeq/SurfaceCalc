package com.example.surfacecalc.data.model

import java.math.BigDecimal

enum class LengthUnit {
    MILLIMETER,
    CENTIMETER,
    METER,
    KILOMETER,
    INCH,
    FOOT,
    YARD
}

data class LengthUnitDefinition(
    val unit: LengthUnit,
    val name: String,
    val symbol: String,
    val metersFactor: BigDecimal
)
