package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceCalcError
import com.example.surfacecalc.data.model.SurfaceShape
import java.math.BigDecimal
import java.math.MathContext

object SurfaceCalcValidator {

    data class DimensionField(
        val key: String,
        val label: String,
        val defaultUnit: LengthUnit = LengthUnit.CENTIMETER
    )

    fun getFieldsForShape(shape: SurfaceShape): List<DimensionField> {
        return when (shape) {
            SurfaceShape.CUBE -> listOf(
                DimensionField("side", "Side")
            )
            SurfaceShape.RECTANGULAR_PRISM -> listOf(
                DimensionField("length", "Length"),
                DimensionField("width", "Width"),
                DimensionField("height", "Height")
            )
            SurfaceShape.CYLINDER -> listOf(
                DimensionField("radius", "Radius"),
                DimensionField("height", "Height")
            )
            SurfaceShape.SPHERE -> listOf(
                DimensionField("radius", "Radius")
            )
            SurfaceShape.CONE -> listOf(
                DimensionField("radius", "Radius"),
                DimensionField("slantHeight", "Slant Height")
            )
            SurfaceShape.SQUARE_PYRAMID -> listOf(
                DimensionField("baseSide", "Base Side"),
                DimensionField("slantHeight", "Slant Height")
            )
            SurfaceShape.TRIANGULAR_PRISM -> listOf(
                DimensionField("sideA", "Side A"),
                DimensionField("sideB", "Side B"),
                DimensionField("sideC", "Side C"),
                DimensionField("prismLength", "Prism Length")
            )
            SurfaceShape.HEMISPHERE -> listOf(
                DimensionField("radius", "Radius")
            )
        }
    }

    /**
     * Validates inputs for the specified shape.
     * Returns Result.success with map of parsed BigDecimals or Result.failure with SurfaceCalcException.
     */
    fun validateAndParse(
        shape: SurfaceShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): Result<Map<String, BigDecimal>> {
        val fields = getFieldsForShape(shape)
        val parsedValues = mutableMapOf<String, BigDecimal>()

        for (field in fields) {
            val raw = inputs[field.key]
            if (raw.isNullOrBlank()) {
                return Result.failure(SurfaceCalcException(SurfaceCalcError.MissingDimension, "Please enter ${field.label.lowercase()}."))
            }

            val parseResult = SurfaceCalcParser.parseDimension(raw)
            if (parseResult.isFailure) {
                val error = (parseResult.exceptionOrNull() as? SurfaceCalcException)?.error ?: SurfaceCalcError.InvalidNumber
                return Result.failure(SurfaceCalcException(error, error.userMessage(field.label)))
            }

            parsedValues[field.key] = parseResult.getOrThrow()
        }

        // Special validation for triangle sides in Triangular Prism
        if (shape == SurfaceShape.TRIANGULAR_PRISM) {
            val rawA = parsedValues["sideA"]!!
            val rawB = parsedValues["sideB"]!!
            val rawC = parsedValues["sideC"]!!

            val unitA = inputUnits["sideA"] ?: LengthUnit.CENTIMETER
            val unitB = inputUnits["sideB"] ?: LengthUnit.CENTIMETER
            val unitC = inputUnits["sideC"] ?: LengthUnit.CENTIMETER

            // Normalize triangle sides to meters for comparison
            val aMeters = LengthUnitConverter.convertToMeters(rawA, unitA, mathContext)
            val bMeters = LengthUnitConverter.convertToMeters(rawB, unitB, mathContext)
            val cMeters = LengthUnitConverter.convertToMeters(rawC, unitC, mathContext)

            val aPlusB = aMeters.add(bMeters, mathContext)
            val aPlusC = aMeters.add(cMeters, mathContext)
            val bPlusC = bMeters.add(cMeters, mathContext)

            val isStrictlyValid = aPlusB.compareTo(cMeters) > 0 &&
                aPlusC.compareTo(bMeters) > 0 &&
                bPlusC.compareTo(aMeters) > 0

            if (!isStrictlyValid) {
                return Result.failure(
                    SurfaceCalcException(
                        SurfaceCalcError.InvalidTriangle,
                        "These side lengths cannot form a valid triangle."
                    )
                )
            }
        }

        return Result.success(parsedValues)
    }

    /**
     * Checks if three lengths form a valid non-degenerate triangle.
     */
    fun isValidTriangle(
        a: BigDecimal,
        b: BigDecimal,
        c: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): Boolean {
        if (a.signum() <= 0 || b.signum() <= 0 || c.signum() <= 0) return false
        val aPlusB = a.add(b, mathContext)
        val aPlusC = a.add(c, mathContext)
        val bPlusC = b.add(c, mathContext)
        return aPlusB.compareTo(c) > 0 &&
            aPlusC.compareTo(b) > 0 &&
            bPlusC.compareTo(a) > 0
    }
}
