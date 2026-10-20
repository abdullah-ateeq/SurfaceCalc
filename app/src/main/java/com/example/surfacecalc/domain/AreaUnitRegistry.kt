package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.AreaUnitDefinition
import java.math.BigDecimal

object AreaUnitRegistry {

    val SQUARE_MILLIMETER = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_MILLIMETER,
        name = "Square Millimeter",
        symbol = "mm²",
        metersFactor = BigDecimal("0.001")
    )

    val SQUARE_CENTIMETER = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_CENTIMETER,
        name = "Square Centimeter",
        symbol = "cm²",
        metersFactor = BigDecimal("0.01")
    )

    val SQUARE_METER = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_METER,
        name = "Square Meter",
        symbol = "m²",
        metersFactor = BigDecimal("1")
    )

    val SQUARE_KILOMETER = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_KILOMETER,
        name = "Square Kilometer",
        symbol = "km²",
        metersFactor = BigDecimal("1000")
    )

    val SQUARE_INCH = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_INCH,
        name = "Square Inch",
        symbol = "in²",
        metersFactor = BigDecimal("0.0254")
    )

    val SQUARE_FOOT = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_FOOT,
        name = "Square Foot",
        symbol = "ft²",
        metersFactor = BigDecimal("0.3048")
    )

    val SQUARE_YARD = AreaUnitDefinition(
        unit = AreaUnit.SQUARE_YARD,
        name = "Square Yard",
        symbol = "yd²",
        metersFactor = BigDecimal("0.9144")
    )

    val ALL: List<AreaUnitDefinition> = listOf(
        SQUARE_MILLIMETER,
        SQUARE_CENTIMETER,
        SQUARE_METER,
        SQUARE_KILOMETER,
        SQUARE_INCH,
        SQUARE_FOOT,
        SQUARE_YARD
    )

    private val UNIT_MAP: Map<AreaUnit, AreaUnitDefinition> = ALL.associateBy { it.unit }

    fun get(unit: AreaUnit): AreaUnitDefinition {
        return UNIT_MAP[unit] ?: SQUARE_CENTIMETER
    }

    fun search(query: String): List<AreaUnitDefinition> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return ALL
        return ALL.filter { def ->
            def.name.lowercase().contains(q) ||
                def.symbol.lowercase().contains(q) ||
                matchesCommonAliases(def.unit, q)
        }
    }

    private fun matchesCommonAliases(unit: AreaUnit, query: String): Boolean {
        return when (unit) {
            AreaUnit.SQUARE_MILLIMETER -> query in listOf("mm", "mm2", "mm²", "sq mm", "sqmm")
            AreaUnit.SQUARE_CENTIMETER -> query in listOf("cm", "cm2", "cm²", "sq cm", "sqcm")
            AreaUnit.SQUARE_METER -> query in listOf("m", "m2", "m²", "sq m", "sqm")
            AreaUnit.SQUARE_KILOMETER -> query in listOf("km", "km2", "km²", "sq km", "sqkm")
            AreaUnit.SQUARE_INCH -> query in listOf("in", "in2", "in²", "sq in", "sqin", "inch", "inches")
            AreaUnit.SQUARE_FOOT -> query in listOf("ft", "ft2", "ft²", "sq ft", "sqft", "foot", "feet")
            AreaUnit.SQUARE_YARD -> query in listOf("yd", "yd2", "yd²", "sq yd", "sqyd", "yard", "yards")
        }
    }
}
