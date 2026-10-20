package com.example.surfacecalc

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.domain.AreaUnitConverter
import com.example.surfacecalc.domain.LengthUnitConverter
import com.example.surfacecalc.domain.MathConstants
import com.example.surfacecalc.domain.SurfaceCalculatorCoordinator
import java.math.BigDecimal
import java.math.RoundingMode
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConversionTest {

    private val mc = MathConstants.DEFAULT_MATH_CONTEXT

    // 9. mm conversion
    @Test
    fun testMillimeterConversion() {
        val mm = BigDecimal("1000")
        val meters = LengthUnitConverter.convertToMeters(mm, LengthUnit.MILLIMETER, mc)
        assertEquals(0, BigDecimal("1").compareTo(meters))
    }

    // 10. cm conversion
    @Test
    fun testCentimeterConversion() {
        val cm = BigDecimal("100")
        val meters = LengthUnitConverter.convertToMeters(cm, LengthUnit.CENTIMETER, mc)
        assertEquals(0, BigDecimal("1").compareTo(meters))
    }

    // 11. m conversion
    @Test
    fun testMeterConversion() {
        val m = BigDecimal("5.5")
        val meters = LengthUnitConverter.convertToMeters(m, LengthUnit.METER, mc)
        assertEquals(0, BigDecimal("5.5").compareTo(meters))
    }

    // 12. km conversion
    @Test
    fun testKilometerConversion() {
        val km = BigDecimal("2.5")
        val meters = LengthUnitConverter.convertToMeters(km, LengthUnit.KILOMETER, mc)
        assertEquals(0, BigDecimal("2500").compareTo(meters))
    }

    // 13. inch conversion
    @Test
    fun testInchConversion() {
        val inches = BigDecimal("10")
        val meters = LengthUnitConverter.convertToMeters(inches, LengthUnit.INCH, mc)
        assertEquals(0, BigDecimal("0.254").compareTo(meters))
    }

    // 14. foot conversion
    @Test
    fun testFootConversion() {
        val feet = BigDecimal("10")
        val meters = LengthUnitConverter.convertToMeters(feet, LengthUnit.FOOT, mc)
        assertEquals(0, BigDecimal("3.048").compareTo(meters))
    }

    // 15. yard conversion
    @Test
    fun testYardConversion() {
        val yards = BigDecimal("10")
        val meters = LengthUnitConverter.convertToMeters(yards, LengthUnit.YARD, mc)
        assertEquals(0, BigDecimal("9.144").compareTo(meters))
    }

    // 16. Square-unit conversion (linear factors squared)
    @Test
    fun testSquareUnitConversion() {
        // 1 m² = 10,000 cm² = 1,000,000 mm²
        val oneSquareMeter = BigDecimal("1")
        val inCm2 = AreaUnitConverter.convertArea(oneSquareMeter, AreaUnit.SQUARE_METER, AreaUnit.SQUARE_CENTIMETER, mc)
        assertEquals(0, BigDecimal("10000").compareTo(inCm2))

        val inMm2 = AreaUnitConverter.convertArea(oneSquareMeter, AreaUnit.SQUARE_METER, AreaUnit.SQUARE_MILLIMETER, mc)
        assertEquals(0, BigDecimal("1000000").compareTo(inMm2))

        // Cube: Side = 1 cm -> Area = 6 cm²
        // Convert to m²: 6 * 0.0001 = 0.0006 m²
        // Convert to mm²: 6 * 100 = 600 mm²
        val sixCm2 = BigDecimal("6")
        val m2FromCm2 = AreaUnitConverter.convertArea(sixCm2, AreaUnit.SQUARE_CENTIMETER, AreaUnit.SQUARE_METER, mc)
        assertEquals(0, BigDecimal("0.0006").compareTo(m2FromCm2))

        val mm2FromCm2 = AreaUnitConverter.convertArea(sixCm2, AreaUnit.SQUARE_CENTIMETER, AreaUnit.SQUARE_MILLIMETER, mc)
        assertEquals(0, BigDecimal("600").compareTo(mm2FromCm2))
    }

    // 17. Mixed input units
    @Test
    fun testMixedInputUnits() {
        // Rectangular prism:
        // Length = 1 m
        // Width = 50 cm
        // Height = 1000 mm
        // Normalize: 1 m, 0.5 m, 1 m
        // Expected surface area: 2(1*0.5 + 1*1 + 0.5*1) = 2(0.5 + 1 + 0.5) = 4 m²
        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.RECTANGULAR_PRISM,
            inputs = mapOf("length" to "1", "width" to "50", "height" to "1000"),
            inputUnits = mapOf(
                "length" to LengthUnit.METER,
                "width" to LengthUnit.CENTIMETER,
                "height" to LengthUnit.MILLIMETER
            ),
            outputUnit = AreaUnit.SQUARE_METER
        ).getOrThrow()

        assertEquals(0, BigDecimal("4").compareTo(result.rawValue))
    }

    // 18. Result unit conversion (inch and foot area conversion)
    @Test
    fun testResultUnitConversionInchAndFoot() {
        // Cube: Side = 1 in -> Area = 6 in²
        // 1 in = 0.0254 m -> 1 in² = 0.00064516 m² -> 6 in² = 0.00387096 m²
        // In cm²: 0.00387096 / 0.0001 = 38.7096 cm²
        // Let's verify with Coordinator:
        val inchCubeResult = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "1"),
            inputUnits = mapOf("side" to LengthUnit.INCH),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()

        val roundedInchCm2 = inchCubeResult.rawValue.setScale(6, RoundingMode.HALF_UP)
        assertEquals("38.7096", roundedInchCm2.stripTrailingZeros().toPlainString())

        // Cube: Side = 1 ft -> Area = 6 ft²
        // 1 ft = 0.3048 m -> 1 ft² = 0.09290304 m² -> 6 ft² = 0.55741824 m²
        val footCubeResult = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "1"),
            inputUnits = mapOf("side" to LengthUnit.FOOT),
            outputUnit = AreaUnit.SQUARE_METER
        ).getOrThrow()

        val roundedFootM2 = footCubeResult.rawValue.setScale(8, RoundingMode.HALF_UP)
        assertEquals("0.55741824", roundedFootM2.stripTrailingZeros().toPlainString())
    }

    // 34. Same-unit result
    @Test
    fun testSameUnitResult() {
        val result = SurfaceCalculatorCoordinator.calculate(
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            outputUnit = AreaUnit.SQUARE_CENTIMETER
        ).getOrThrow()

        assertEquals(0, BigDecimal("150").compareTo(result.rawValue))
    }
}
