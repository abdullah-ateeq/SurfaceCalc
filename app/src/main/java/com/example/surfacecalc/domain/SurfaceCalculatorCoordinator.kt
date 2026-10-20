package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceAreaResult
import com.example.surfacecalc.data.model.SurfaceShape
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object SurfaceCalculatorCoordinator {

    fun calculate(
        shape: SurfaceShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        outputUnit: AreaUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): Result<SurfaceAreaResult> {
        val validationResult = SurfaceCalcValidator.validateAndParse(shape, inputs, inputUnits, mathContext)
        if (validationResult.isFailure) {
            return Result.failure(validationResult.exceptionOrNull()!!)
        }
        val parsed = validationResult.getOrThrow()

        fun toMeters(key: String): BigDecimal {
            val raw = parsed[key]!!
            val unit = inputUnits[key] ?: LengthUnit.CENTIMETER
            return LengthUnitConverter.convertToMeters(raw, unit, mathContext)
        }

        val areaInMetersSquared = try {
            when (shape) {
                SurfaceShape.CUBE -> {
                    SurfaceAreaCalculator.calculateCube(toMeters("side"), mathContext)
                }
                SurfaceShape.RECTANGULAR_PRISM -> {
                    SurfaceAreaCalculator.calculateRectangularPrism(
                        toMeters("length"),
                        toMeters("width"),
                        toMeters("height"),
                        mathContext
                    )
                }
                SurfaceShape.CYLINDER -> {
                    SurfaceAreaCalculator.calculateCylinder(
                        toMeters("radius"),
                        toMeters("height"),
                        mathContext
                    )
                }
                SurfaceShape.SPHERE -> {
                    SurfaceAreaCalculator.calculateSphere(
                        toMeters("radius"),
                        mathContext
                    )
                }
                SurfaceShape.CONE -> {
                    SurfaceAreaCalculator.calculateCone(
                        toMeters("radius"),
                        toMeters("slantHeight"),
                        mathContext
                    )
                }
                SurfaceShape.SQUARE_PYRAMID -> {
                    SurfaceAreaCalculator.calculateSquarePyramid(
                        toMeters("baseSide"),
                        toMeters("slantHeight"),
                        mathContext
                    )
                }
                SurfaceShape.TRIANGULAR_PRISM -> {
                    SurfaceAreaCalculator.calculateTriangularPrism(
                        toMeters("sideA"),
                        toMeters("sideB"),
                        toMeters("sideC"),
                        toMeters("prismLength"),
                        mathContext
                    )
                }
                SurfaceShape.HEMISPHERE -> {
                    SurfaceAreaCalculator.calculateHemisphere(
                        toMeters("radius"),
                        mathContext
                    )
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val convertedArea = AreaUnitConverter.convertFromMetersSquared(areaInMetersSquared, outputUnit, mathContext)

        val (details, steps) = SurfaceCalculationExplainer.generateStepsAndDetails(
            shape = shape,
            inputs = inputs,
            inputUnits = inputUnits,
            outputUnit = outputUnit,
            resultArea = convertedArea,
            decimalPlaces = decimalPlaces,
            roundingMode = roundingMode
        )

        return Result.success(
            SurfaceAreaResult(
                shape = shape,
                rawValue = convertedArea,
                outputUnit = outputUnit,
                details = details,
                calculationSteps = steps,
                inputs = inputs,
                inputUnits = inputUnits,
                calculatedAt = System.currentTimeMillis()
            )
        )
    }
}
