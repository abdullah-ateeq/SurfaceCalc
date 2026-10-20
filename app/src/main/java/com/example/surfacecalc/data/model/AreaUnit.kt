package com.example.surfacecalc.data.model

import java.math.BigDecimal

enum class AreaUnit {
    SQUARE_MILLIMETER,
    SQUARE_CENTIMETER,
    SQUARE_METER,
    SQUARE_KILOMETER,
    SQUARE_INCH,
    SQUARE_FOOT,
    SQUARE_YARD
}

data class AreaUnitDefinition(
    val unit: AreaUnit,
    val name: String,
    val symbol: String,
    val metersFactor: BigDecimal
)
