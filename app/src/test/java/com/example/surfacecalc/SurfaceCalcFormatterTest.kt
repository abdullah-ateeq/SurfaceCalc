package com.example.surfacecalc

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.domain.SurfaceCalcFormatter
import com.example.surfacecalc.domain.SurfaceCalculatorCoordinator
import java.math.BigDecimal
import java.math.RoundingMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceCalcFormatterTest {

    // 39. HALF_UP
    @Test
    fun testRoundingHalfUp() {
        val val1 = BigDecimal("2.55")
        assertEquals("2.6", SurfaceCalcFormatter.formatNumber(val1, 1, RoundingMode.HALF_UP))
        val val2 = BigDecimal("2.54")
        assertEquals("2.5", SurfaceCalcFormatter.formatNumber(val2, 1, RoundingMode.HALF_UP))
    }

    // 40. HALF_DOWN
    @Test
    fun testRoundingHalfDown() {
        val val1 = BigDecimal("2.55")
        assertEquals("2.5", SurfaceCalcFormatter.formatNumber(val1, 1, RoundingMode.HALF_DOWN))
        val val2 = BigDecimal("2.56")
        assertEquals("2.6", SurfaceCalcFormatter.formatNumber(val2, 1, RoundingMode.HALF_DOWN))
    }

    // 41. HALF_EVEN
    @Test
    fun testRoundingHalfEven() {
        val val1 = BigDecimal("2.5")
        // 2 is even -> 2
        assertEquals("2", SurfaceCalcFormatter.formatNumber(val1, 0, RoundingMode.HALF_EVEN))
        val val2 = BigDecimal("3.5")
        // 3 is odd -> rounds to nearest even -> 4
        assertEquals("4", SurfaceCalcFormatter.formatNumber(val2, 0, RoundingMode.HALF_EVEN))
    }

    // 42. UP
    @Test
    fun testRoundingUp() {
        val v = BigDecimal("2.11")
        assertEquals("2.2", SurfaceCalcFormatter.formatNumber(v, 1, RoundingMode.UP))
    }

    // 43. DOWN
    @Test
    fun testRoundingDown() {
        val v = BigDecimal("2.99")
        assertEquals("2.9", SurfaceCalcFormatter.formatNumber(v, 1, RoundingMode.DOWN))
    }

    // 44. CEILING
    @Test
    fun testRoundingCeiling() {
        val v = BigDecimal("2.11")
        assertEquals("2.2", SurfaceCalcFormatter.formatNumber(v, 1, RoundingMode.CEILING))
    }

    // 45. FLOOR
    @Test
    fun testRoundingFloor() {
        val v = BigDecimal("2.89")
        assertEquals("2.8", SurfaceCalcFormatter.formatNumber(v, 1, RoundingMode.FLOOR))
    }

    // 46. Trailing-zero formatting
    @Test
    fun testTrailingZeroFormatting() {
        val v1 = BigDecimal("150.000000")
        assertEquals("150", SurfaceCalcFormatter.formatNumber(v1, 6, RoundingMode.HALF_UP))
        assertEquals("150 cm²", SurfaceCalcFormatter.formatArea(v1, AreaUnit.SQUARE_CENTIMETER, 6, RoundingMode.HALF_UP))

        val v2 = BigDecimal("150.500000")
        assertEquals("150.5", SurfaceCalcFormatter.formatNumber(v2, 6, RoundingMode.HALF_UP))
        assertEquals("150.5 cm²", SurfaceCalcFormatter.formatArea(v2, AreaUnit.SQUARE_CENTIMETER, 6, RoundingMode.HALF_UP))

        val v3 = BigDecimal("150.123456")
        assertEquals("150.123456", SurfaceCalcFormatter.formatNumber(v3, 6, RoundingMode.HALF_UP))
        assertEquals("150.123456 cm²", SurfaceCalcFormatter.formatArea(v3, AreaUnit.SQUARE_CENTIMETER, 6, RoundingMode.HALF_UP))
    }

    @Test
    fun testCopyAndShareFormatting() {
        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CYLINDER,
            inputs = mapOf("radius" to "3", "height" to "10"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()

        val copyText = SurfaceCalcFormatter.formatCopyText(result, 6, RoundingMode.HALF_UP)
        assertTrue(copyText.contains("SurfaceCalc"))
        assertTrue(copyText.contains("Shape: Cylinder"))
        assertTrue(copyText.contains("Radius: 3 cm"))
        assertTrue(copyText.contains("Height: 10 cm"))
        assertTrue(copyText.contains("Total Surface Area: 245.044227 cm²"))
    }
}
