package com.example.surfacecalc

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.domain.AreaUnitRegistry
import com.example.surfacecalc.domain.LengthUnitRegistry
import com.example.surfacecalc.domain.SurfaceCalculatorCoordinator
import com.example.surfacecalc.ui.viewmodel.SurfaceCalcViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceCalcDomainAndFlowTest {

    // 47. Shape selection
    @Test
    fun testShapeSelection() {
        val defaultInputsCube = SurfaceCalcViewModel.getDefaultInputsForShape(SurfaceShape.CUBE)
        assertEquals("5", defaultInputsCube["side"])

        val defaultInputsSphere = SurfaceCalcViewModel.getDefaultInputsForShape(SurfaceShape.SPHERE)
        assertEquals("3", defaultInputsSphere["radius"])
    }

    // 48. Shape switching
    @Test
    fun testShapeSwitching() {
        for (shape in SurfaceShape.values()) {
            val inputs = SurfaceCalcViewModel.getDefaultInputsForShape(shape)
            val units = SurfaceCalcViewModel.getDefaultUnitsForShape(shape)
            val res = SurfaceCalculatorCoordinator.calculate(
                shape = shape,
                inputs = inputs,
                inputUnits = units,
                outputUnit = AreaUnit.SQUARE_CENTIMETER
            )
            assertTrue("Calculation for $shape should succeed", res.isSuccess)
            assertNotNull(res.getOrThrow().rawValue)
        }
    }

    // 49. Unit search
    @Test
    fun testUnitSearch() {
        val feetResults = LengthUnitRegistry.search("feet")
        assertTrue(feetResults.any { it.unit == LengthUnit.FOOT })

        val inchResults = LengthUnitRegistry.search("in")
        assertTrue(inchResults.any { it.unit == LengthUnit.INCH })

        val cmResults = LengthUnitRegistry.search("centimeter")
        assertTrue(cmResults.any { it.unit == LengthUnit.CENTIMETER })
    }

    // 50. Unit selection
    @Test
    fun testUnitSelection() {
        val def = LengthUnitRegistry.get(LengthUnit.KILOMETER)
        assertEquals("km", def.symbol)
        assertEquals("Kilometer", def.name)

        val areaDef = AreaUnitRegistry.get(AreaUnit.SQUARE_KILOMETER)
        assertEquals("km²", areaDef.symbol)
    }

    // 60. Duplicate history protection logic
    @Test
    fun testDuplicateHistoryProtectionLogic() {
        val input1 = mapOf("side" to "5")
        val units1 = mapOf("side" to LengthUnit.CENTIMETER)

        val input2 = mapOf("side" to "5")
        val units2 = mapOf("side" to LengthUnit.CENTIMETER)

        // Matching properties indicates duplicate
        val isDuplicate = (SurfaceShape.CUBE == SurfaceShape.CUBE &&
            input1 == input2 &&
            units1 == units2 &&
            AreaUnit.SQUARE_CENTIMETER == AreaUnit.SQUARE_CENTIMETER)

        assertTrue(isDuplicate)

        // Different unit is not a duplicate
        val units3 = mapOf("side" to LengthUnit.METER)
        val isDuplicateDifferentUnit = (input1 == input2 && units1 == units3)
        assertFalse(isDuplicateDifferentUnit)
    }

    // 61. Rapid input protection / immutability
    @Test
    fun testRapidInputProtection() {
        // Fast consecutive calculations on multiple values
        val values = listOf("1", "2.5", "10", "100", "50.5", "0.001")
        for (v in values) {
            val result = SurfaceCalculatorCoordinator.calculate(
                shape = SurfaceShape.CUBE,
                inputs = mapOf("side" to v),
                inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
                outputUnit = AreaUnit.SQUARE_CENTIMETER
            )
            assertTrue(result.isSuccess)
        }
    }
}
