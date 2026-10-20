package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.LengthUnit
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {

    fun convertToMeters(
        value: BigDecimal,
        unit: LengthUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (unit == LengthUnit.METER) return value
        val factor = LengthUnitRegistry.get(unit).metersFactor
        return value.multiply(factor, mathContext)
    }

    fun convertFromMeters(
        value: BigDecimal,
        unit: LengthUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (unit == LengthUnit.METER) return value
        val factor = LengthUnitRegistry.get(unit).metersFactor
        return value.divide(factor, mathContext)
    }
}
