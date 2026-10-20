package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.AreaUnit
import java.math.BigDecimal
import java.math.MathContext

object AreaUnitConverter {

    fun convertArea(
        value: BigDecimal,
        fromUnit: AreaUnit,
        toUnit: AreaUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (fromUnit == toUnit) return value

        val fromLinear = AreaUnitRegistry.get(fromUnit).metersFactor
        val toLinear = AreaUnitRegistry.get(toUnit).metersFactor

        // Area factors are squared linear factors
        val fromAreaFactor = fromLinear.multiply(fromLinear, mathContext)
        val toAreaFactor = toLinear.multiply(toLinear, mathContext)

        val areaInMetersSquared = value.multiply(fromAreaFactor, mathContext)
        return areaInMetersSquared.divide(toAreaFactor, mathContext)
    }

    fun convertFromMetersSquared(
        areaInMetersSquared: BigDecimal,
        toUnit: AreaUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (toUnit == AreaUnit.SQUARE_METER) return areaInMetersSquared

        val toLinear = AreaUnitRegistry.get(toUnit).metersFactor
        val toAreaFactor = toLinear.multiply(toLinear, mathContext)
        return areaInMetersSquared.divide(toAreaFactor, mathContext)
    }

    fun convertToMetersSquared(
        areaInUnit: BigDecimal,
        fromUnit: AreaUnit,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (fromUnit == AreaUnit.SQUARE_METER) return areaInUnit

        val fromLinear = AreaUnitRegistry.get(fromUnit).metersFactor
        val fromAreaFactor = fromLinear.multiply(fromLinear, mathContext)
        return areaInUnit.multiply(fromAreaFactor, mathContext)
    }
}
