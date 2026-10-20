package com.example.surfacecalc

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceCalcError
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.domain.SurfaceCalcException
import com.example.surfacecalc.domain.SurfaceCalcParser
import com.example.surfacecalc.domain.SurfaceCalcValidator
import com.example.surfacecalc.domain.SurfaceCalculatorCoordinator
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceCalcParserValidatorTest {

    // 19. Zero input
    @Test
    fun testZeroInputRejected() {
        val res = SurfaceCalcParser.parseDimension("0")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.NonPositiveValue, ex.error)
    }

    // 20. Negative input
    @Test
    fun testNegativeInputRejected() {
        val res = SurfaceCalcParser.parseDimension("-5")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.NonPositiveValue, ex.error)
    }

    // 21. Decimal input
    @Test
    fun testDecimalInput() {
        val parsed = SurfaceCalcParser.parseDimension("2.5").getOrThrow()
        assertEquals(0, BigDecimal("2.5").compareTo(parsed))

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "2.5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        // 6 * 2.5^2 = 6 * 6.25 = 37.5
        assertEquals(0, BigDecimal("37.5").compareTo(result.rawValue))
    }

    // 22. Leading decimal
    @Test
    fun testLeadingDecimalInput() {
        val parsed = SurfaceCalcParser.parseDimension(".5").getOrThrow()
        assertEquals(0, BigDecimal("0.5").compareTo(parsed))

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to ".5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        // 6 * 0.5^2 = 6 * 0.25 = 1.5
        assertEquals(0, BigDecimal("1.5").compareTo(result.rawValue))
    }

    // 23. Trailing decimal
    @Test
    fun testTrailingDecimalInput() {
        val parsed = SurfaceCalcParser.parseDimension("5.").getOrThrow()
        assertEquals(0, BigDecimal("5").compareTo(parsed))

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "5."),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        assertEquals(0, BigDecimal("150").compareTo(result.rawValue))
    }

    // 24. Scientific notation
    @Test
    fun testScientificNotation() {
        val parsed = SurfaceCalcParser.parseDimension("1E+1").getOrThrow()
        assertEquals(0, BigDecimal("10").compareTo(parsed))

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "1E+1"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        // 6 * 10^2 = 600
        assertEquals(0, BigDecimal("600").compareTo(result.rawValue))
    }

    // 25. Very small values
    @Test
    fun testVerySmallValues() {
        val parsed = SurfaceCalcParser.parseDimension("0.000001").getOrThrow()
        assertEquals(0, BigDecimal("0.000001").compareTo(parsed))

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "0.000001"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        // 6 * (10^-6)^2 = 6 * 10^-12 = 0.000000000006
        assertEquals(0, BigDecimal("0.000000000006").compareTo(result.rawValue))
    }

    // 26. Very large values (no primitive overflow)
    @Test
    fun testVeryLargeValuesNoOverflow() {
        val largeValStr = "999999999999999999999999"
        val parsed = SurfaceCalcParser.parseDimension(largeValStr).getOrThrow()
        assertEquals(largeValStr, parsed.toPlainString())

        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to largeValStr),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()
        assertTrue(result.rawValue > BigDecimal("1000000000000000000000000"))
    }

    // 32. Triangle inequality
    @Test
    fun testTriangleInequalityRejected() {
        // a=3, b=4, c=8 -> 3 + 4 = 7 < 8 -> Invalid triangle
        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.TRIANGULAR_PRISM,
            inputs = mapOf("sideA" to "3", "sideB" to "4", "sideC" to "8", "prismLength" to "10"),
            inputUnits = mapOf(
                "sideA" to LengthUnit.CENTIMETER,
                "sideB" to LengthUnit.CENTIMETER,
                "sideC" to LengthUnit.CENTIMETER,
                "prismLength" to LengthUnit.CENTIMETER
            ),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        )
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.InvalidTriangle, ex.error)
        assertEquals("These side lengths cannot form a valid triangle.", ex.message)
    }

    // 33. Degenerate triangle (a + b = c)
    @Test
    fun testDegenerateTriangleRejected() {
        // a=3, b=4, c=7 -> 3 + 4 = 7 -> Degenerate triangle
        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.TRIANGULAR_PRISM,
            inputs = mapOf("sideA" to "3", "sideB" to "4", "sideC" to "7", "prismLength" to "10"),
            inputUnits = mapOf(
                "sideA" to LengthUnit.CENTIMETER,
                "sideB" to LengthUnit.CENTIMETER,
                "sideC" to LengthUnit.CENTIMETER,
                "prismLength" to LengthUnit.CENTIMETER
            ),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        )
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.InvalidTriangle, ex.error)
    }

    // 35. Invalid input
    @Test
    fun testInvalidInputRejected() {
        val r1 = SurfaceCalcParser.parseDimension("abc")
        assertTrue(r1.isFailure)
        val r2 = SurfaceCalcParser.parseDimension("--")
        assertTrue(r2.isFailure)
    }

    // 36. Empty input
    @Test
    fun testEmptyInputRejected() {
        val r = SurfaceCalcParser.parseDimension("   ")
        assertTrue(r.isFailure)
        val ex = r.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.EmptyInput, ex.error)
    }

    // 37. Multiple decimal points
    @Test
    fun testMultipleDecimalPointsRejected() {
        val r1 = SurfaceCalcParser.parseDimension("12..5")
        assertTrue(r1.isFailure)
        val r2 = SurfaceCalcParser.parseDimension("1.2.3")
        assertTrue(r2.isFailure)
    }

    // 38. Maximum input length (500 digits)
    @Test
    fun testMaximumInputLength() {
        val validDigits = "1".repeat(500)
        val validRes = SurfaceCalcParser.parseDimension(validDigits)
        assertTrue(validRes.isSuccess)

        val tooManyDigits = "1".repeat(501)
        val invalidRes = SurfaceCalcParser.parseDimension(tooManyDigits)
        assertTrue(invalidRes.isFailure)
        val ex = invalidRes.exceptionOrNull() as SurfaceCalcException
        assertEquals(SurfaceCalcError.InputTooLong, ex.error)
    }
}
