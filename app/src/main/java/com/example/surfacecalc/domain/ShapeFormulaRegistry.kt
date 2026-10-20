package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.ShapeFormula
import com.example.surfacecalc.data.model.SurfaceCalculationDetails
import com.example.surfacecalc.data.model.SurfaceShape
import java.math.BigDecimal
import java.math.RoundingMode

object ShapeFormulaRegistry {

    fun getFormula(shape: SurfaceShape): ShapeFormula {
        return when (shape) {
            SurfaceShape.CUBE -> ShapeFormula(
                shape = shape,
                formula = "A = 6s²",
                description = "Total surface area of 6 equal square faces."
            )
            SurfaceShape.RECTANGULAR_PRISM -> ShapeFormula(
                shape = shape,
                formula = "A = 2(lw + lh + wh)",
                description = "Total surface area of 3 pairs of identical rectangular faces."
            )
            SurfaceShape.CYLINDER -> ShapeFormula(
                shape = shape,
                formula = "A = 2πr² + 2πrh",
                description = "Total surface area of 2 circular bases plus the curved surface."
            )
            SurfaceShape.SPHERE -> ShapeFormula(
                shape = shape,
                formula = "A = 4πr²",
                description = "Complete external surface area of the sphere."
            )
            SurfaceShape.CONE -> ShapeFormula(
                shape = shape,
                formula = "A = πr² + πrl",
                description = "Circular base area plus curved surface using slant height (l)."
            )
            SurfaceShape.SQUARE_PYRAMID -> ShapeFormula(
                shape = shape,
                formula = "A = s² + 2sl",
                description = "Square base area plus 4 triangular faces using slant height (l)."
            )
            SurfaceShape.TRIANGULAR_PRISM -> ShapeFormula(
                shape = shape,
                formula = "A = 2T + (a+b+c)L",
                description = "2 triangular bases (via Heron's formula T) plus 3 rectangular sides."
            )
            SurfaceShape.HEMISPHERE -> ShapeFormula(
                shape = shape,
                formula = "A = 3πr²",
                description = "Curved dome surface (2πr²) plus circular base (πr²)."
            )
        }
    }
}

object SurfaceCalculationExplainer {

    fun generateStepsAndDetails(
        shape: SurfaceShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        outputUnit: AreaUnit,
        resultArea: BigDecimal,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): Pair<SurfaceCalculationDetails, List<String>> {
        val formula = ShapeFormulaRegistry.getFormula(shape).formula
        val steps = mutableListOf<String>()
        val resultStr = SurfaceCalcFormatter.formatArea(resultArea, outputUnit, decimalPlaces, roundingMode)

        steps.add(formula)

        when (shape) {
            SurfaceShape.CUBE -> {
                val s = inputs["side"] ?: "s"
                val u = inputUnits["side"]?.let { LengthUnitRegistry.get(it).symbol } ?: ""
                steps.add("A = 6 × $s²")
                steps.add("A = $resultStr")
            }
            SurfaceShape.RECTANGULAR_PRISM -> {
                val l = inputs["length"] ?: "l"
                val w = inputs["width"] ?: "w"
                val h = inputs["height"] ?: "h"
                steps.add("A = 2($l×$w + $l×$h + $w×$h)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.CYLINDER -> {
                val r = inputs["radius"] ?: "r"
                val h = inputs["height"] ?: "h"
                steps.add("A = 2π($r²) + 2π($r)($h)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.SPHERE -> {
                val r = inputs["radius"] ?: "r"
                steps.add("A = 4π($r²)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.CONE -> {
                val r = inputs["radius"] ?: "r"
                val l = inputs["slantHeight"] ?: "l"
                steps.add("A = π($r²) + π($r)($l)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.SQUARE_PYRAMID -> {
                val s = inputs["baseSide"] ?: "s"
                val l = inputs["slantHeight"] ?: "l"
                steps.add("A = $s² + 2($s)($l)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.TRIANGULAR_PRISM -> {
                val a = inputs["sideA"] ?: "a"
                val b = inputs["sideB"] ?: "b"
                val c = inputs["sideC"] ?: "c"
                val len = inputs["prismLength"] ?: "L"
                steps.add("s = ($a + $b + $c) / 2")
                steps.add("Triangle Area T = √(s(s-a)(s-b)(s-c))")
                steps.add("A = 2T + ($a + $b + $c)($len)")
                steps.add("A = $resultStr")
            }
            SurfaceShape.HEMISPHERE -> {
                val r = inputs["radius"] ?: "r"
                steps.add("A = 3π($r²)")
                steps.add("A = $resultStr")
            }
        }

        val calculation = steps.joinToString("\n")
        val details = SurfaceCalculationDetails(formula = formula, calculation = calculation)
        return Pair(details, steps)
    }
}
