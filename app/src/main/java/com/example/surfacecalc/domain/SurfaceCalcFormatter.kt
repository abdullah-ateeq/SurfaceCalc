package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.SurfaceAreaResult
import com.example.surfacecalc.data.model.SurfaceCalculationDetails
import java.math.BigDecimal
import java.math.RoundingMode

object SurfaceCalcFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val rounded = value.setScale(decimalPlaces, roundingMode)
        val stripped = rounded.stripTrailingZeros()
        val normal = if (stripped.scale() < 0) stripped.setScale(0) else stripped
        return normal.toPlainString()
    }

    fun formatArea(
        value: BigDecimal,
        unit: AreaUnit,
        decimalPlaces: Int,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formattedNum = formatNumber(value, decimalPlaces, roundingMode)
        val symbol = AreaUnitRegistry.get(unit).symbol
        return "$formattedNum $symbol"
    }

    fun formatFormula(
        details: SurfaceCalculationDetails
    ): String {
        return details.formula
    }

    fun formatCopyText(
        result: SurfaceAreaResult,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val sb = StringBuilder()
        sb.append("SurfaceCalc\n\n")
        sb.append("Shape: ${result.shape.displayName}\n")

        for (field in SurfaceCalcValidator.getFieldsForShape(result.shape)) {
            val v = result.inputs[field.key] ?: ""
            val u = result.inputUnits[field.key]
            val unitDef = u?.let { LengthUnitRegistry.get(it) }
            val unitSymbol = unitDef?.symbol ?: ""
            sb.append("${field.label}: $v $unitSymbol\n")
        }

        val formattedResult = formatArea(result.rawValue, result.outputUnit, decimalPlaces, roundingMode)
        sb.append("Total Surface Area: $formattedResult")
        return sb.toString()
    }

    fun formatShareText(
        result: SurfaceAreaResult,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return formatCopyText(result, decimalPlaces, roundingMode)
    }
}
