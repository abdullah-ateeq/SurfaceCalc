package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.LengthUnitDefinition
import java.math.BigDecimal

object LengthUnitRegistry {

    val MILLIMETER = LengthUnitDefinition(
        unit = LengthUnit.MILLIMETER,
        name = "Millimeter",
        symbol = "mm",
        metersFactor = BigDecimal("0.001")
    )

    val CENTIMETER = LengthUnitDefinition(
        unit = LengthUnit.CENTIMETER,
        name = "Centimeter",
        symbol = "cm",
        metersFactor = BigDecimal("0.01")
    )

    val METER = LengthUnitDefinition(
        unit = LengthUnit.METER,
        name = "Meter",
        symbol = "m",
        metersFactor = BigDecimal("1")
    )

    val KILOMETER = LengthUnitDefinition(
        unit = LengthUnit.KILOMETER,
        name = "Kilometer",
        symbol = "km",
        metersFactor = BigDecimal("1000")
    )

    val INCH = LengthUnitDefinition(
        unit = LengthUnit.INCH,
        name = "Inch",
        symbol = "in",
        metersFactor = BigDecimal("0.0254")
    )

    val FOOT = LengthUnitDefinition(
        unit = LengthUnit.FOOT,
        name = "Foot",
        symbol = "ft",
        metersFactor = BigDecimal("0.3048")
    )

    val YARD = LengthUnitDefinition(
        unit = LengthUnit.YARD,
        name = "Yard",
        symbol = "yd",
        metersFactor = BigDecimal("0.9144")
    )

    val ALL: List<LengthUnitDefinition> = listOf(
        MILLIMETER,
        CENTIMETER,
        METER,
        KILOMETER,
        INCH,
        FOOT,
        YARD
    )

    private val UNIT_MAP: Map<LengthUnit, LengthUnitDefinition> = ALL.associateBy { it.unit }

    fun get(unit: LengthUnit): LengthUnitDefinition {
        return UNIT_MAP[unit] ?: CENTIMETER
    }

    fun search(query: String): List<LengthUnitDefinition> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return ALL
        return ALL.filter { def ->
            def.name.lowercase().contains(q) ||
                def.symbol.lowercase().contains(q) ||
                matchesCommonAliases(def.unit, q)
        }
    }

    private fun matchesCommonAliases(unit: LengthUnit, query: String): Boolean {
        return when (unit) {
            LengthUnit.MILLIMETER -> query in listOf("milli", "mm", "millimetre", "millimeters", "millimetres")
            LengthUnit.CENTIMETER -> query in listOf("centi", "cm", "centimetre", "centimeters", "centimetres")
            LengthUnit.METER -> query in listOf("meter", "metre", "meters", "metres", "m")
            LengthUnit.KILOMETER -> query in listOf("kilo", "km", "kilometre", "kilometers", "kilometres")
            LengthUnit.INCH -> query in listOf("in", "inch", "inches", "\"")
            LengthUnit.FOOT -> query in listOf("ft", "foot", "feet", "'")
            LengthUnit.YARD -> query in listOf("yd", "yard", "yards")
        }
    }
}
